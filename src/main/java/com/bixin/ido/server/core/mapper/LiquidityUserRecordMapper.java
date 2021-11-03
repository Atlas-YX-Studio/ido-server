package com.bixin.ido.server.core.mapper;

import com.bixin.ido.server.bean.DO.LiquidityUserRecord;
import com.bixin.ido.server.core.wrapDDL.LiquidityUserRecordDDL;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface LiquidityUserRecordMapper {
    long countByDDL(LiquidityUserRecordDDL DDL);

    int deleteByDDL(LiquidityUserRecordDDL DDL);

    int deleteByPrimaryKey(Long id);

    int insert(LiquidityUserRecord record);

    int insertSelective(LiquidityUserRecord record);

    List<LiquidityUserRecord> selectByDDL(LiquidityUserRecordDDL DDL);

    List<LiquidityUserRecord> selectByPage(Map<String,Object> paramMap);

    LiquidityUserRecord selectByPrimaryKey(Long id);

    List<String> selectAllAddress();

    int updateByDDLSelective(@Param("record") LiquidityUserRecord record, @Param("DDL") LiquidityUserRecordDDL DDL);

    int updateByDDL(@Param("record") LiquidityUserRecord record, @Param("DDL") LiquidityUserRecordDDL DDL);

    int updateByPrimaryKeySelective(LiquidityUserRecord record);

    int updateByPrimaryKey(LiquidityUserRecord record);
}