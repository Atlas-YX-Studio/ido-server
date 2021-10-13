package com.bixin.nft.bean.DO;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
* @Class: TradingRecordDo
* @Description: 交易记录表
* @author: 系统
* @created: 2021-10-13
*/
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TradingRecordDo implements Serializable {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 用户地址
     */
    private String address;

    /**
     * 类型nft、box
     */
    private String type;

    /**
     * 关联id nft id 或 box id
     */
    private Long refId;

    /**
     * 方向buy、sell
     */
    private String direction;

    /**
     * 图片链接
     */
    private String icon;

    /**
     * ndf/box 分组全称
     */
    private String name;

    /**
     * 盲盒币种
     */
    private String boxToken;

    /**
     * nft_meta地址
     */
    private String nftMeta;

    /**
     * nft_body地址
     */
    private String nftBody;

    /**
     * 支付币种
     */
    private String payToken;

    /**
     * 状态
     */
    private String state;

    /**
     * 成交价、出价
     */
    private BigDecimal price;

    /**
     * 手续费
     */
    private BigDecimal fee;

    /**
     * 是否完结
     */
    private Boolean finish;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

    private static final long serialVersionUID = 1L;
}
