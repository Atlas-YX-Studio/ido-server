package com.bixin.nft.bean.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 操作纪律
 */
@Data
public class OperationRecordVo {

    // 币种列表
    private String currencyName;

    //成交价格
    private BigDecimal price;

    //状态 获得，上架，铸造
    private String status;

    //地址
    private String address;

    // 操作时间
    private Long created;
}