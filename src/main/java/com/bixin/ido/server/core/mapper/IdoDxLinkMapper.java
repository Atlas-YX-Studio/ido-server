package com.bixin.ido.server.core.mapper;

import com.bixin.ido.server.bean.DO.IdoDxLink;
import com.bixin.ido.server.core.wrapDDL.IdoDxLinkDDL;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangcheng
 * @create 2021-08-06 5:34 下午
 */
@Mapper
public interface IdoDxLinkMapper {
    long countByDDL(IdoDxLinkDDL DDL);

    int deleteByDDL(IdoDxLinkDDL DDL);

    int deleteByPrimaryKey(Long id);

    int insert(IdoDxLink record);

    int insertSelective(IdoDxLink record);

    List<IdoDxLink> selectByDDL(IdoDxLinkDDL DDL);

    IdoDxLink selectByPrimaryKey(Long id);

    int updateByDDLSelective(@Param("record") IdoDxLink record, @Param("DDL") IdoDxLinkDDL DDL);

    int updateByDDL(@Param("record") IdoDxLink record, @Param("DDL") IdoDxLinkDDL DDL);

    int updateByPrimaryKeySelective(IdoDxLink record);

    int updateByPrimaryKey(IdoDxLink record);
}