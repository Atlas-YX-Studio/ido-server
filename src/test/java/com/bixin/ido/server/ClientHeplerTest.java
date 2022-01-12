package com.bixin.ido.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bixin.IdoServerApplication;
import com.bixin.common.utils.BigDecimalUtil;
import com.bixin.common.utils.StarCoinJsonUtil;
import com.bixin.core.client.ChainClientHelper;
import com.bixin.nft.bean.DO.NftGroupDo;
import com.bixin.nft.bean.DO.NftInfoDo;
import com.bixin.nft.bean.dto.NftSelfResourceDto;
import com.bixin.nft.bean.vo.NftSelfResourceVo;
import com.bixin.nft.service.NftInfoService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.mutable.MutableLong;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author zhangcheng
 * create  2022/1/10
 */
@Slf4j
@ActiveProfiles("local")
@SpringBootTest(classes = IdoServerApplication.class)
public class ClientHeplerTest {


    @Resource
    ChainClientHelper chainClientHelper;
    @Resource
    NftInfoService nftInfoService;

    @Test
    public void testSelfResource() {
        NftGroupDo groupDo = NftGroupDo.builder()
                .nftMeta("0x69f1e543a3bef043b63bed825fcd2cf6::KikoCatElement04::KikoCatMeta")
                .nftBody("0x69f1e543a3bef043b63bed825fcd2cf6::KikoCatElement04::KikoCatBody")
                .id(10012L)
                .build();
        NftGroupDo groupDo1 = NftGroupDo.builder()
                .nftMeta("0x69f1e543a3bef043b63bed825fcd2cf6::KikoCatCard04::KikoCatMeta")
                .nftBody("0x69f1e543a3bef043b63bed825fcd2cf6::KikoCatCard04::KikoCatBody")
                .id(10012L)
                .build();

        getNftList4Element("0xa85291039ddad8845d5097624c81c3fd",
                groupDo);

        getNftList4Card("0xa85291039ddad8845d5097624c81c3fd",
                groupDo1);

    }
/*

    curl --location --request POST 'https://kikoswap.com/seed' \
    --header 'Content-Type: application/json' \
    --data-raw '{
    "id":101,
    "jsonrpc":"2.0",
    "method":"contract.get_resource",
    "params":["0xa85291039ddad8845d5097624c81c3fd",
    "0x00000000000000000000000000000001::NFTGallery::NFTGallery<0x69f1e543a3bef043b63bed825fcd2cf6::KikoCatElement04::KikoCatMeta,
    0x69f1e543a3bef043b63bed825fcd2cf6::KikoCatElement04::KikoCatBody>"]
    }'


    curl --location --request POST 'https://kikoswap.com/seed' \
    --header 'Content-Type: application/json' \
    --data-raw '{
    "id":101,
    "jsonrpc":"2.0",
    "method":"contract.get_resource",
    "params":["0xa85291039ddad8845d5097624c81c3fd",
    "0x00000000000000000000000000000001::NFTGallery::NFTGallery<0x69f1e543a3bef043b63bed825fcd2cf6::KikoCatCard04::KikoCatMeta,
    0x69f1e543a3bef043b63bed825fcd2cf6::KikoCatCard04::KikoCatBody>"]
    }'

*/


    @SuppressWarnings("unchecked")
    private void getNftList4Element(String userAddress, NftGroupDo nftGroupDo) {
        MutableTriple<ResponseEntity<String>, String, HttpEntity<Map<String, Object>>> triple =
                chainClientHelper.getNftListResp(userAddress, nftGroupDo.getNftMeta(), nftGroupDo.getNftBody());
        ResponseEntity<String> resp = triple.getLeft();
        String url = triple.getMiddle();
        HttpEntity<Map<String, Object>> httpEntity = triple.getRight();

        if (resp.getStatusCode() != HttpStatus.OK) {
            log.info("nftMetaverse getNftListFromChain 获取失败 {}, {}, {}", JSON.toJSONString(httpEntity), url, JSON.toJSONString(resp));
            return;
        }
        List<JSONArray> values = StarCoinJsonUtil.parseRpcResult(resp);
        if (CollectionUtils.isEmpty(values)) {
            return;
        }
        Map<String, String> imgMap = new HashMap<>();
        Map<String, NftSelfResourceDto> propMap = new HashMap<>();
        values.forEach(value -> {
            Object[] stcResult = value.toArray();
            if ("items".equalsIgnoreCase(String.valueOf(stcResult[0]))) {
                List<JSONObject> vector = StarCoinJsonUtil.parseVectorObj(stcResult[1]);
                vector.forEach(el -> {
                    List<JSONArray> structValue = StarCoinJsonUtil.parseStructObj(el);
                    structValue.forEach(v -> {
                        Object[] info = v.toArray();
                        if ("base_meta".equals(String.valueOf(info[0]))) {
                            Map<String, JSONObject> valueMap = (Map<String, JSONObject>) info[1];
                            JSONObject structObj = valueMap.get("Struct");
                            if (Objects.isNull(structObj)) {
                                log.error("nftMetaverse struct is null 1");
                                return;
                            }
                            JSONArray structs = StarCoinJsonUtil.parseStruct2Array(structObj);
                            String eleName = "";
                            for (Object s : structs) {
                                JSONArray sv = (JSONArray) s;
                                String jsonName = (String) sv.get(0);
                                JSONObject jsonObject = (JSONObject) sv.get(1);
                                String imageLink = "";
                                if ("name".equalsIgnoreCase(jsonName)) {
                                    eleName = StarCoinJsonUtil.toHexValue(jsonObject.get("Bytes"));
                                } else if ("image".equalsIgnoreCase(jsonName)) {
                                    imageLink = StarCoinJsonUtil.toHexValue(jsonObject.get("Bytes"));
                                } else {
                                    log.error("nftMetaverse unknown element base_meta field {}, {}", jsonName, jsonObject);
                                }
                                if (StringUtils.isBlank(imgMap.get(eleName)) && StringUtils.isNotBlank(imageLink)) {
                                    imgMap.put(eleName, imageLink);
                                    break;
                                }
                            }
                        }
                        if ("type_meta".equals(String.valueOf(info[0]))) {
                            Map<String, JSONObject> valueMap = (Map<String, JSONObject>) info[1];
                            JSONObject structObj = valueMap.get("Struct");
                            if (Objects.isNull(structObj)) {
                                log.error("nftMetaverse struct is null 2");
                                return;
                            }
                            JSONArray structs = StarCoinJsonUtil.parseStruct2Array(structObj);
                            NftSelfResourceDto dto = null;
                            for (Object s : structs) {
                                JSONArray sv = (JSONArray) s;
                                String jsonName = (String) sv.get(0);
                                JSONObject jsonObject = (JSONObject) sv.get(1);
                                if ("type".equalsIgnoreCase(jsonName)) {
                                    String eleName = StarCoinJsonUtil.toHexValue(jsonObject.get("Bytes"));
                                    dto = propMap.get(eleName);
                                    if (Objects.isNull(dto)) {
                                        dto = new NftSelfResourceDto();
                                    }
                                } else if ("type_id".equalsIgnoreCase(jsonName)) {
                                    long id = NumberUtils.toLong(String.valueOf(jsonObject.get("U64")), -1);
                                    dto.setId(id);
                                } else if ("property".equalsIgnoreCase(jsonName)) {
                                    String property = StarCoinJsonUtil.toHexValue(jsonObject.get("Bytes"));
                                    dto.setProperty(property);
                                } else if ("score".equalsIgnoreCase(jsonName)) {
                                    long score = NumberUtils.toLong(String.valueOf(jsonObject.get("U128")), -1);
                                    dto.setScore(BigDecimalUtil.div(BigDecimal.valueOf(score), BigDecimal.valueOf(1000000000), 2));
                                } else {
                                    log.error("nftMetaverse unknown element type_meta field {}, {}", jsonName, jsonObject);
                                }
                            }
                        }
                    });
                });
            }
        });

        log.info("nftMetaverse nft list {}, {}", userAddress, propMap);
        Map<String, NftSelfResourceVo.ElementVo> map = new HashMap<>();
        propMap.forEach((key, value) -> {
            String imgLink = imgMap.get(key);
            NftSelfResourceVo.ElementVo vo = map.get(key);
            long tmpSum = 0;
            if (Objects.nonNull(vo)) {
                tmpSum = vo.getSum();
            }
            NftSelfResourceVo.ElementVo tmpVo = NftSelfResourceVo.ElementVo.builder()
                    .image(imgLink)
                    .sum(tmpSum + 1L)
                    .property(value.getProperty())
                    .score(value.getScore())
                    .build();
            map.put(key, tmpVo);
        });

        log.info("sssss: " + map);
    }


    @SuppressWarnings("unchecked")
    private void getNftList4Card(String userAddress, NftGroupDo nftGroupDo) {
        MutableTriple<ResponseEntity<String>, String, HttpEntity<Map<String, Object>>> triple =
                chainClientHelper.getNftListResp(userAddress, nftGroupDo.getNftMeta(), nftGroupDo.getNftBody());
        ResponseEntity<String> resp = triple.getLeft();
        String url = triple.getMiddle();
        HttpEntity<Map<String, Object>> httpEntity = triple.getRight();

        if (resp.getStatusCode() != HttpStatus.OK) {
            log.info("nftMetaverse getNftListFromChain 获取失败 {}, {}, {}", JSON.toJSONString(httpEntity), url, JSON.toJSONString(resp));
            return;
        }
        List<JSONArray> values = StarCoinJsonUtil.parseRpcResult(resp);
        if (CollectionUtils.isEmpty(values)) {
            return;
        }
        List<NftInfoDo> nftInfoDos = Lists.newArrayList();
        values.forEach(value -> {
            Object[] stcResult = value.toArray();
            if ("items".equalsIgnoreCase(String.valueOf(stcResult[0]))) {
                List<JSONObject> vector = StarCoinJsonUtil.parseVectorObj(stcResult[1]);
                vector.forEach(el -> {
                    MutableLong nftId = new MutableLong(0);
                    List<JSONArray> structValue = StarCoinJsonUtil.parseStructObj(el);
                    structValue.forEach(v -> {
                        Object[] info = v.toArray();
                        if ("id".equals(String.valueOf(info[0]))) {
                            Map<String, Object> valueMap = (Map<String, Object>) info[1];
                            nftId.setValue(Long.valueOf((String) valueMap.get("U64")));
                        }
                    });
                    if (nftId.getValue() <= 0) {
                        log.error("NFTId解析错误, groupId:{}，nftId:{}, struct:{}", nftGroupDo.getId(), nftId.getValue(), el);
                        return;
                    }
                    NftInfoDo selectNftInfoDo = new NftInfoDo();
                    selectNftInfoDo.setGroupId(nftGroupDo.getId());
                    selectNftInfoDo.setNftId(nftId.getValue());
                    NftInfoDo nftInfoDo = nftInfoService.selectByObject(selectNftInfoDo);
                    if (ObjectUtils.isEmpty(nftInfoDo)) {
                        log.error("NFTInfo不存在, groupId:{}，nftId:{}", nftGroupDo.getId(), nftId.getValue());
                        return;
                    }
                    // 以链上为准，更新当前owner
                    if (!StringUtils.equalsIgnoreCase(userAddress, nftInfoDo.getOwner())) {
                        nftInfoDo.setOwner(userAddress);
                        nftInfoService.update(nftInfoDo);
                    }
                    nftInfoDos.add(nftInfoDo);
                });
            }
        });

        log.info("fffffff: " + nftInfoDos);
    }

}
