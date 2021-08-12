package com.bixin.ido.server.core.mapper;

import com.bixin.ido.server.bean.DO.IdoDxEventLogs;
import com.bixin.ido.server.core.wrapDDL.IdoDxEventLogsDDL;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangcheng
 * @create 2021-08-06 5:34 下午
 */
@Mapper
public interface IdoDxEventLogsMapper {
    long countByDDL(IdoDxEventLogsDDL DDL);

    int deleteByDDL(IdoDxEventLogsDDL DDL);

    int deleteByPrimaryKey(Long id);

    int insert(IdoDxEventLogs record);

    int insertSelective(IdoDxEventLogs record);

    List<IdoDxEventLogs> selectByDDL(IdoDxEventLogsDDL DDL);

    IdoDxEventLogs selectByPrimaryKey(Long id);

    int updateByDDLSelective(@Param("record") IdoDxEventLogs record, @Param("DDL") IdoDxEventLogsDDL DDL);

    int updateByDDL(@Param("record") IdoDxEventLogs record, @Param("DDL") IdoDxEventLogsDDL DDL);

    int updateByPrimaryKeySelective(IdoDxEventLogs record);

    int updateByPrimaryKey(IdoDxEventLogs record);
}