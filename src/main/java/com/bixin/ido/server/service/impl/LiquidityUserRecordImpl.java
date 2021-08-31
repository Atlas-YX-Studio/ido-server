package com.bixin.ido.server.service.impl;

import com.bixin.ido.server.bean.DO.LiquidityUserRecord;
import com.bixin.ido.server.core.mapper.LiquidityUserRecordMapper;
import com.bixin.ido.server.service.ILiquidityUserRecordService;
import com.bixin.ido.server.utils.CaseUtil;
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
}
