package com.bixin.ido.server.core.mapper;

import com.bixin.ido.server.bean.DO.IdoDxLabel;
import com.bixin.ido.server.core.wrapDDL.IdoDxLabelDDL;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangcheng
 * create 2021-08-06 5:34 下午
 */
@Mapper
public interface IdoDxLabelMapper {
    long countByDDL(IdoDxLabelDDL DDL);

    int deleteByDDL(IdoDxLabelDDL DDL);

    int deleteByPrimaryKey(Long id);

    int insert(IdoDxLabel record);

    int insertSelective(IdoDxLabel record);

    List<IdoDxLabel> selectByDDL(IdoDxLabelDDL DDL);

    IdoDxLabel selectByPrimaryKey(Long id);

    int updateByDDLSelective(@Param("record") IdoDxLabel record, @Param("DDL") IdoDxLabelDDL DDL);

    int updateByDDL(@Param("record") IdoDxLabel record, @Param("DDL") IdoDxLabelDDL DDL);

    int updateByPrimaryKeySelective(IdoDxLabel record);

    int updateByPrimaryKey(IdoDxLabel record);
}