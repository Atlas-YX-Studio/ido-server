package com.bixin.ido.server.core.mapper;

import com.bixin.ido.server.bean.DO.SwapCoins;
import com.bixin.ido.server.core.wrapDDL.SwapCoinsDDL;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SwapCoinsMapper {
    long countByDDL(SwapCoinsDDL DDL);

    int deleteByDDL(SwapCoinsDDL DDL);

    int deleteByPrimaryKey(Long id);

    int insert(SwapCoins record);

    int insertSelective(SwapCoins record);

    List<SwapCoins> selectByDDL(SwapCoinsDDL DDL);

    SwapCoins selectByPrimaryKey(Long id);

    List<SwapCoins> selectByPage(Map<String, Object> paramMap);

    int updateByDDLSelective(@Param("record") SwapCoins record, @Param("DDL") SwapCoinsDDL DDL);

    int updateByDDL(@Param("record") SwapCoins record, @Param("DDL") SwapCoinsDDL DDL);

    int updateByPrimaryKeySelective(SwapCoins record);

    int updateByPrimaryKey(SwapCoins record);
}