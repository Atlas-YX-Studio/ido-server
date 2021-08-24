package com.bixin.ido.server.core.mapper;

import com.bixin.ido.server.bean.DO.IdoSwapUserRecord;
import com.bixin.ido.server.core.wrapDDL.IdoSwapUserRecordDDL;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IdoSwapUserRecordMapper {
    long countByDDL(IdoSwapUserRecordDDL DDL);

    int deleteByDDL(IdoSwapUserRecordDDL DDL);

    int deleteByPrimaryKey(Long id);

    int insert(IdoSwapUserRecord record);

    int insertSelective(IdoSwapUserRecord record);

    List<IdoSwapUserRecord> selectByDDL(IdoSwapUserRecordDDL DDL);

    IdoSwapUserRecord selectByPrimaryKey(Long id);

    int updateByDDLSelective(@Param("record") IdoSwapUserRecord record, @Param("DDL") IdoSwapUserRecordDDL DDL);

    int updateByDDL(@Param("record") IdoSwapUserRecord record, @Param("DDL") IdoSwapUserRecordDDL DDL);

    int updateByPrimaryKeySelective(IdoSwapUserRecord record);

    int updateByPrimaryKey(IdoSwapUserRecord record);
}