package com.bixin.nft.core.service.impl;

import com.bixin.nft.bean.DO.TradingRecordDo;
import com.bixin.nft.core.mapper.NftMarketMapper;
import com.bixin.nft.core.mapper.TradingRecordMapper;
import com.bixin.nft.core.service.TradingRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TradingRecordServiceImpl implements TradingRecordService {

    @Resource
    private TradingRecordMapper tradingRecordMapper;

    @Override
    public List<TradingRecordDo> selectByPage(long pageSize, long pageNum, String address, String direction) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("address", address);
        paramMap.put("pageSize", pageSize);
        paramMap.put("pageFrom", (pageNum - 1) * pageSize);
        if (StringUtils.isNoneEmpty(direction) && !"all".equalsIgnoreCase(direction)) {
            paramMap.put("direction", direction);
        }
        return tradingRecordMapper.selectByPage(paramMap);
    }

    /**
     * @explain: 添加TradingRecordDo对象
     * @param:   model 对象参数
     * @return:  int
     */
    @Override
    public int insert(TradingRecordDo model) {
        return tradingRecordMapper.insert(model);
    }

    /**
     * @explain: 删除TradingRecordDo对象
     * @param:   id
     * @return:  int
     */
    @Override
    public int deleteById(Long id) {
        return tradingRecordMapper.deleteByPrimaryKey(id);
    }

    /**
     * @explain: 修改TradingRecordDo对象
     * @param:   model 对象参数
     * @return:  int
     */
    @Override
    public int update(TradingRecordDo model) {
        return tradingRecordMapper.updateByPrimaryKeySelective(model);
    }

    /**
     * @explain: 查询TradingRecordDo对象
     * @param:   id
     * @return:  TradingRecordDo
     */
    @Override
    public TradingRecordDo selectById(Long id) {
        return tradingRecordMapper.selectByPrimaryKey(id);
    }

    /**
     * @explain: 查询TradingRecordDo对象
     * @param:   model 对象参数
     * @return:  TradingRecordDo 对象
     */
    @Override
    public TradingRecordDo selectByObject(TradingRecordDo model) {
        return tradingRecordMapper.selectByPrimaryKeySelective(model);
    }

    /**
     * @explain: 查询列表
     * @param:  model  对象参数
     * @return: list
     */
    @Override
    public List<TradingRecordDo> listByObject(TradingRecordDo model) {
        return tradingRecordMapper.selectByPrimaryKeySelectiveList(model);
    }
}
