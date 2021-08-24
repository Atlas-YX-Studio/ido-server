package com.bixin.ido.server.core.mapper;

import com.bixin.ido.server.bean.DO.IdoLiquidityUserRecord;
import com.bixin.ido.server.core.wrapDDL.IdoLiquidityUserRecordDDL;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IdoLiquidityUserRecordMapper {
    long countByDDL(IdoLiquidityUserRecordDDL DDL);

    int deleteByDDL(IdoLiquidityUserRecordDDL DDL);

    int deleteByPrimaryKey(Long id);

    int insert(IdoLiquidityUserRecord record);

    int insertSelective(IdoLiquidityUserRecord record);

    List<IdoLiquidityUserRecord> selectByDDL(IdoLiquidityUserRecordDDL DDL);

    IdoLiquidityUserRecord selectByPrimaryKey(Long id);

    int updateByDDLSelective(@Param("record") IdoLiquidityUserRecord record, @Param("DDL") IdoLiquidityUserRecordDDL DDL);

    int updateByDDL(@Param("record") IdoLiquidityUserRecord record, @Param("DDL") IdoLiquidityUserRecordDDL DDL);

    int updateByPrimaryKeySelective(IdoLiquidityUserRecord record);

    int updateByPrimaryKey(IdoLiquidityUserRecord record);
}