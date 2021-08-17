package com.bixin.ido.server.core.mapper;

import com.bixin.ido.server.bean.DO.IdoDxUserRecord;
import com.bixin.ido.server.core.wrapDDL.IdoDxUserRecordDDL;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author zhangcheng
 * create 2021-08-06 5:34 下午
 */
@Mapper
public interface IdoDxUserRecordMapper {
    long countByDDL(IdoDxUserRecordDDL DDL);

    int deleteByDDL(IdoDxUserRecordDDL DDL);

    int deleteByPrimaryKey(Long id);

    int insert(IdoDxUserRecord record);

    int insertSelective(IdoDxUserRecord record);

    List<IdoDxUserRecord> selectByDDL(IdoDxUserRecordDDL DDL);

    List<IdoDxUserRecord>  selectALlByPage(Map<String,Object> paramMap);

    IdoDxUserRecord selectByPrimaryKey(Long id);

    int updateByDDLSelective(@Param("record") IdoDxUserRecord record, @Param("DDL") IdoDxUserRecordDDL DDL);

    int updateByDDL(@Param("record") IdoDxUserRecord record, @Param("DDL") IdoDxUserRecordDDL DDL);

    int updateByPrimaryKeySelective(IdoDxUserRecord record);

    int updateByPrimaryKey(IdoDxUserRecord record);
}