package com.bixin.nft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bixin.nft.bean.DO.NftCompositeCard;
import com.bixin.nft.core.mapper.NftCompositeCardMapper;
import com.bixin.nft.service.NftMetareverseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author zhangcheng
 * create  2021/12/23
 */
@Service
public class NftMetaverseServiceImpl implements NftMetareverseService {

    @Resource
    NftCompositeCardMapper compositeCardMapper;

    @Override
    public List<Map<String, Object>> getSumByOccupationGroup() {
        QueryWrapper<NftCompositeCard> wrapper = new QueryWrapper<>();
        wrapper.select("occupation, count(id) as sum")
                .groupBy("occupation");
        return compositeCardMapper.selectMaps(wrapper);
    }


}
