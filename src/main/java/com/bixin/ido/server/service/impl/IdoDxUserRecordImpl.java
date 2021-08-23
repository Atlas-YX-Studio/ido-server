package com.bixin.ido.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bixin.ido.server.bean.DO.IdoDxUserRecord;
import com.bixin.ido.server.bean.bo.UserRecordReqBo;
import com.bixin.ido.server.constants.IdoDxCommonConstant;
import com.bixin.ido.server.core.mapper.IdoDxUserRecordMapper;
import com.bixin.ido.server.core.wrapDDL.IdoDxUserRecordDDL;
import com.bixin.ido.server.enums.UserPledgeType;
import com.bixin.ido.server.service.IIdoDxUserRecordService;
import com.bixin.ido.server.utils.LocalDateTimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author zhangcheng
 * create          2021-08-12 11:08 上午
 */
@Service
public class IdoDxUserRecordImpl implements IIdoDxUserRecordService {

    @Resource
    IdoDxUserRecordMapper idoDxUserRecordMapper;

    @Override
    public int updateUserRecord(UserRecordReqBo bean) {
        IdoDxUserRecordDDL idoDxUserRecordDDL = new IdoDxUserRecordDDL();
        IdoDxUserRecordDDL.Criteria criteria = idoDxUserRecordDDL.createCriteria();
        criteria.andUserAddressEqualTo(bean.getUserAddress());
        criteria.andPrdAddressEqualTo(bean.getPrdAddress());
        List<IdoDxUserRecord> idoDxUserRecords = idoDxUserRecordMapper.selectByDDL(idoDxUserRecordDDL);

        //质押解押数量
        BigDecimal userAmount = UserPledgeType.PLEDGE.getCode() == bean.getUserPledgeType() ? bean.getAmount() : bean.getAmount().negate();

        Long currentTime = LocalDateTimeUtil.getMilliByTime(LocalDateTime.now());

        IdoDxUserRecord idoDxUserRecord;

        if (CollectionUtils.isEmpty(idoDxUserRecords)) {
            TreeMap<Integer, BigDecimal> amountMap = new TreeMap<>();
            amountMap.put(1, userAmount);
            idoDxUserRecord = IdoDxUserRecord.builder()
                    .prdAddress(bean.getPrdAddress())
                    .userAddress(bean.getUserAddress())
                    .amount(userAmount)
                    .currency(bean.getCurrency())
                    .tokenVersion((short) 0)
                    .extInfo(JSON.toJSONString(Map.of(IdoDxCommonConstant.USER_RECORD_EXT_KEY, amountMap)))
                    .createTime(currentTime)
                    .updateTime(currentTime)
                    .build();
            idoDxUserRecordMapper.insert(idoDxUserRecord);
            return 0;
        }
        idoDxUserRecord = idoDxUserRecords.get(0);

        BigDecimal updateAmount = idoDxUserRecord.getAmount().add(userAmount);
        String extInfo = idoDxUserRecord.getExtInfo();
        TreeMap<Integer, BigDecimal> amountMap;
        if (StringUtils.isNoneBlank(extInfo)) {
            Map<String, TreeMap<Integer, BigDecimal>> extMap = JSON.parseObject(idoDxUserRecord.getExtInfo(),
                    new TypeReference<Map<String, TreeMap<Integer, BigDecimal>>>() {
                    });
            amountMap = extMap.get(IdoDxCommonConstant.USER_RECORD_EXT_KEY);
            amountMap.put(amountMap.size() + 1, userAmount);
        } else {
            amountMap = new TreeMap<>();
            amountMap.put(1, userAmount);
        }

        idoDxUserRecord.setExtInfo(JSON.toJSONString(Map.of(IdoDxCommonConstant.USER_RECORD_EXT_KEY, amountMap)));
        idoDxUserRecord.setAmount(updateAmount);
        idoDxUserRecord.setUpdateTime(currentTime);

        return updateUserRecord(idoDxUserRecord);

    }

    @Override
    public int updateUserRecord(IdoDxUserRecord record) {
        return idoDxUserRecordMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public List<IdoDxUserRecord> getUserRecord(IdoDxUserRecord record) {
        IdoDxUserRecordDDL dxUserRecordDDL = new IdoDxUserRecordDDL();
        IdoDxUserRecordDDL.Criteria criteria = dxUserRecordDDL.createCriteria();

        if (StringUtils.isNoneEmpty(record.getPrdAddress())) {
            criteria.andPrdAddressEqualTo(record.getPrdAddress());
        }
        if (StringUtils.isNoneEmpty(record.getUserAddress())) {
            criteria.andUserAddressEqualTo(record.getUserAddress());
        }
        if (Objects.nonNull(record.getTokenVersion()) && record.getTokenVersion() > 1) {
            criteria.andTokenVersionLessThan(record.getTokenVersion());
        }

        return idoDxUserRecordMapper.selectByDDL(dxUserRecordDDL);
    }

    @Override
    public List<IdoDxUserRecord> selectALlByPage(IdoDxUserRecord record, long from, long pageSize) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("prdAddress", record.getPrdAddress());
        paramMap.put("tokenVersion", record.getTokenVersion());
        paramMap.put("sort", "createTime");
        paramMap.put("order", "desc");
        paramMap.put("from", from);
        paramMap.put("pageSize", pageSize);
        return idoDxUserRecordMapper.selectALlByPage(paramMap);
    }

}
