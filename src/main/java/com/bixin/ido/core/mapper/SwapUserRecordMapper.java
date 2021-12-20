package com.bixin.ido.core.mapper;

import com.bixin.ido.core.wrapDDL.SwapUserRecordDDL;
import com.bixin.ido.bean.DO.SwapUserRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SwapUserRecordMapper {
    long countByDDL(SwapUserRecordDDL DDL);

    int deleteByDDL(SwapUserRecordDDL DDL);

    int deleteByPrimaryKey(Long id);

    int insert(SwapUserRecord record);

    int insertSelective(SwapUserRecord record);

    List<SwapUserRecord> selectByDDL(SwapUserRecordDDL DDL);

    List<SwapUserRecord> selectByPage(Map<String, Object> paramMap);

    SwapUserRecord selectByPrimaryKey(Long id);

    List<String> selectAllAddress();

    int updateByDDLSelective(@Param("record") SwapUserRecord record, @Param("DDL") SwapUserRecordDDL DDL);

    int updateByDDL(@Param("record") SwapUserRecord record, @Param("DDL") SwapUserRecordDDL DDL);

    int updateByPrimaryKeySelective(SwapUserRecord record);

    int updateByPrimaryKey(SwapUserRecord record);
}