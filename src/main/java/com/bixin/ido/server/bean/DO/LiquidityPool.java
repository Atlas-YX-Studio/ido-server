package com.bixin.ido.server.bean.DO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiquidityPool {
    private Long id;

    private String userAddress;

    private String tokenCodeX;

    private String tokenCodeY;

    private Long createTime;

}