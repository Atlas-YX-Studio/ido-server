package com.bixin.ido.server.core.mapper;

import com.bixin.ido.server.bean.DO.LiquidityPool;
import com.bixin.ido.server.core.wrapDDL.LiquidityPoolDDL;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LiquidityPoolMapper {
    long countByDDL(LiquidityPoolDDL DDL);

    int deleteByDDL(LiquidityPoolDDL DDL);

    int deleteByPrimaryKey(Long id);

    int insert(LiquidityPool record);

    int insertSelective(LiquidityPool record);

    List<LiquidityPool> selectByDDL(LiquidityPoolDDL DDL);

    LiquidityPool selectByPrimaryKey(Long id);

    int updateByDDLSelective(@Param("record") LiquidityPool record, @Param("DDL") LiquidityPoolDDL DDL);

    int updateByDDL(@Param("record") LiquidityPool record, @Param("DDL") LiquidityPoolDDL DDL);

    int updateByPrimaryKeySelective(LiquidityPool record);

    int updateByPrimaryKey(LiquidityPool record);
}