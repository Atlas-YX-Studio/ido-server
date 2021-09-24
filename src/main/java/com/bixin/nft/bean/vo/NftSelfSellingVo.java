package com.bixin.nft.bean.vo;

import com.bixin.nft.bean.DO.NftMarketDo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author zhangcheng
 * create   2021/9/23
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class NftSelfSellingVo extends NftMarketDo {

    private String boxToken;
    private String nftMeta;
    private String nftBody;

}
