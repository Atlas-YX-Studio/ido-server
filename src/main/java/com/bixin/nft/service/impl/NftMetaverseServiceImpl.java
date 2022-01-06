package com.bixin.nft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bixin.common.response.R;
import com.bixin.nft.bean.DO.NftCompositeCard;
import com.bixin.nft.bean.DO.NftCompositeElement;
import com.bixin.nft.bean.DO.NftInfoDo;
import com.bixin.nft.core.mapper.NftCompositeCardMapper;
import com.bixin.nft.core.mapper.NftCompositeElementMapper;
import com.bixin.nft.core.mapper.NftInfoMapper;
import com.bixin.nft.service.NftMetareverseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author zhangcheng
 * create  2021/12/23
 */
@Service
public class NftMetaverseServiceImpl implements NftMetareverseService {


    @Resource
    NftCompositeCardMapper compositeCardMapper;
    @Resource
    NftCompositeElementMapper compositeElementMapper;
    @Resource
    NftInfoMapper nftInfoMapper;

    @Override
    public List<Map<String, Object>> getSumByOccupationGroup() {
        QueryWrapper<NftCompositeCard> wrapper = new QueryWrapper<>();
        wrapper.select("occupation, count(id) as sum")
                .groupBy("occupation");
        return compositeCardMapper.selectMaps(wrapper);
    }


    public String compositeCard(String customName, String userAddress, List<Long> elementIds) {
        List<NftCompositeElement> elementList = compositeElementMapper.selectBatchIds(elementIds);

        //校验 需要组合的卡牌是否已经存在，如果存在则直接反悔原有的图片链接，如果不存在，继续下一步

        //http 调用远程组合卡牌服务，获取新组合的卡牌 url

        //查询原始表 nft_info 的 nft 原始数据，然后把新组合的卡牌数据插入到组合卡牌表

        //返回 卡牌的 url

        return null;
    }


    @Override
    public R analysisCard(String userAddress, long cardId) {
        NftInfoDo nftInfoDo = nftInfoMapper.selectByPrimaryKey(cardId);
        if (Objects.isNull(nftInfoDo)) {
            return R.failed("cardId is invalid");
        }
        if (!userAddress.equalsIgnoreCase(nftInfoDo.getOwner())) {
            return R.failed("userAddress is invalid");
        }




        return R.success();
    }


}
