package com.bixin.ido.server.core.mapper;

import com.bixin.ido.server.bean.DO.IdoSwapCoins;
import com.bixin.ido.server.core.wrapDDL.SwapCoinsDDL;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SwapCoinsMapper {
    long countByDDL(SwapCoinsDDL DDL);

    int deleteByDDL(SwapCoinsDDL DDL);

    int deleteByPrimaryKey(Long id);

    int insert(IdoSwapCoins record);

    int insertSelective(IdoSwapCoins record);

    List<IdoSwapCoins> selectByDDL(SwapCoinsDDL DDL);

    IdoSwapCoins selectByPrimaryKey(Long id);

    int updateByDDLSelective(@Param("record") IdoSwapCoins record, @Param("DDL") SwapCoinsDDL DDL);

    int updateByDDL(@Param("record") IdoSwapCoins record, @Param("DDL") SwapCoinsDDL DDL);

    int updateByPrimaryKeySelective(IdoSwapCoins record);

    int updateByPrimaryKey(IdoSwapCoins record);
}