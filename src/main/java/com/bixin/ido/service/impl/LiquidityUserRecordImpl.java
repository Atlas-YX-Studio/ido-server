package com.bixin.ido.service.impl;

import com.bixin.ido.bean.DO.LiquidityUserRecord;
import com.bixin.ido.core.mapper.LiquidityUserRecordMapper;
import com.bixin.ido.service.ILiquidityUserRecordService;
import com.bixin.common.utils.CaseUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangcheng
 * create  2021-08-25 5:28 下午
 */
@Service
public class LiquidityUserRecordImpl implements ILiquidityUserRecordService {

    @Resource
    LiquidityUserRecordMapper liquidityUserRecordMapper;

    @Override
    public int insert(LiquidityUserRecord record) {
        return liquidityUserRecordMapper.insert(record);
    }

    @Override
    public List<LiquidityUserRecord> getALlByPage(String userAddress,long pageSize, long nextId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userAddress", userAddress);
        paramMap.put("pageSize", pageSize);
        paramMap.put("sort", "id");
        paramMap.put("order", "desc");

        CaseUtil.buildNoneValue(nextId, id -> paramMap.put("nextId", id));

        return liquidityUserRecordMapper.selectByPage(paramMap);
    }

    @Override
    public List<String> selectAllAddress() {
        return liquidityUserRecordMapper.selectAllAddress();
    }
}
