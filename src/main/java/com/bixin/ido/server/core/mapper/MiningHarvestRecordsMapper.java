package com.bixin.ido.server.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bixin.ido.server.entity.MiningHarvestRecords;
import org.apache.ibatis.annotations.Mapper;

/**
 * 挖矿收益提取记录表 Mapper 接口
 *
 * @author Xiang Feihan
 * @since 2021-11-30
 */
@Mapper
public interface MiningHarvestRecordsMapper extends BaseMapper<MiningHarvestRecords> {

}
