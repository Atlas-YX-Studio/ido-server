package com.bixin.ido.server.core.mapper;

import com.bixin.ido.server.bean.DO.IdoDxProduct;
import com.bixin.ido.server.core.wrapDDL.IdoDxProductDDL;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author zhangcheng
 * create 2021-08-06 5:34 下午
 */
@Mapper
public interface IdoDxProductMapper {
    long countByDDL(IdoDxProductDDL DDL);

    int deleteByDDL(IdoDxProductDDL DDL);

    int deleteByPrimaryKey(Long id);

    int insert(IdoDxProduct record);

    int insertSelective(IdoDxProduct record);

    List<IdoDxProduct> selectByDDL(IdoDxProductDDL DDL);

    IdoDxProduct selectByPrimaryKey(Long id);

    int updateByDDLSelective(@Param("record") IdoDxProduct record, @Param("DDL") IdoDxProductDDL DDL);

    int updateByDDL(@Param("record") IdoDxProduct record, @Param("DDL") IdoDxProductDDL DDL);

    int updateByPrimaryKeySelective(IdoDxProduct record);

    int updateByPrimaryKey(IdoDxProduct record);
}