package com.bixin.ido.server.bean.vo;

import com.bixin.ido.server.bean.DO.SwapCoins;
import com.bixin.ido.server.service.impl.SwapPathServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@SuperBuilder
@EqualsAndHashCode()
@NoArgsConstructor
@AllArgsConstructor
public class SwapMetaVO {

    private Long visits;

    private List<Coin> tokens;

    private List<Pair> pairs;

    public static SwapMetaVO convertToMeta(List<SwapCoins> swapCoins, Map<String, SwapPathServiceImpl.Pool> liquidityPoolMap, Long visits) {
        return SwapMetaVO.builder().tokens(swapCoins.stream().map(c -> Coin.builder()
                        .shortName(c.getShortName())
                        .fullName(c.getFullName())
                        .address(c.getAddress())
                        .exchangePrecision(c.getExchangePrecision())
                        .displayPrecision(c.getDisplayPrecision()).build()).collect(Collectors.toList()))
                .pairs(liquidityPoolMap.values().stream().map(pool -> Pair.builder()
                        .tokenA(pool.tokenA)
                        .tokenB(pool.tokenB).build()).collect(Collectors.toList()))
                .visits(visits)
                .build();
    }

    @Data
    @SuperBuilder
    @EqualsAndHashCode()
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Coin {
        private String shortName;

        private String fullName;

        private String address;

        private Short exchangePrecision;

        private Short displayPrecision;
    }

    @Data
    @SuperBuilder
    @EqualsAndHashCode()
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pair {
//        private String name;

        private String tokenA;

        private String tokenB;

    }

}
