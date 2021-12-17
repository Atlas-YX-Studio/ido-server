package com.bixin.ido.bean.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author zhangcheng
 * create          2021-08-12 11:13 上午
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRecordReqBo {
    //项目地址
    private String prdAddress;
    //用户地址
    private String userAddress;
    //质押量
    private BigDecimal amount;
    //1 质押 ，2 解押
    private int userPledgeType;
    //质押币种
    private String currency;

}
