package com.bixin.ido.server.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bixin.ido.server.entity.NftMiningRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * NFT质押事件表 Mapper 接口
 *
 * @author Xiang Feihan
 * @since 2021-11-29
 */
@Mapper
public interface NftMiningRecordMapper extends BaseMapper<NftMiningRecord> {

}
