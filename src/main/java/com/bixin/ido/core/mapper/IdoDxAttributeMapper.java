package com.bixin.ido.core.mapper;

import com.bixin.ido.core.wrapDDL.IdoDxAttributeDDL;
import com.bixin.ido.bean.DO.IdoDxAttribute;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangcheng
 * create 2021-08-06 5:34 下午
 */
@Mapper
public interface IdoDxAttributeMapper {
    long countByDDL(IdoDxAttributeDDL DDL);

    int deleteByDDL(IdoDxAttributeDDL DDL);

    int deleteByPrimaryKey(Long id);

    int insert(IdoDxAttribute record);

    int insertSelective(IdoDxAttribute record);

    List<IdoDxAttribute> selectByDDL(IdoDxAttributeDDL DDL);

    IdoDxAttribute selectByPrimaryKey(Long id);

    int updateByDDLSelective(@Param("record") IdoDxAttribute record, @Param("DDL") IdoDxAttributeDDL DDL);

    int updateByDDL(@Param("record") IdoDxAttribute record, @Param("DDL") IdoDxAttributeDDL DDL);

    int updateByPrimaryKeySelective(IdoDxAttribute record);

    int updateByPrimaryKey(IdoDxAttribute record);
}