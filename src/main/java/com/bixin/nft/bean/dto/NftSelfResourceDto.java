package com.bixin.nft.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author zhangcheng
 * create  2022/1/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NftSelfResourceDto {

    private String type;
    private long id;
    private String property;
    private BigDecimal score;

}

//
//
//"value":[
//        [
//        "type",
//        {
//        "Bytes":"0x636c6f74686573"
//        }
//        ],
//        [
//        "type_id",
//        {
//        "U64":"3"
//        }
//        ],
//        [
//        "property",
//        {
//        "Bytes":"0x626c7565"
//        }
//        ],
//        [
//        "score",
//        {
//        "U128":"1000000000"
//        }
//        ]
//        ]
//


