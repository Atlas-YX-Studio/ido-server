package com.bixin.nft.bean.vo;

import com.bixin.nft.bean.DO.NftCompositeCard;
import com.bixin.nft.bean.DO.NftCompositeElement;
import com.bixin.nft.bean.DO.NftMarketDo;
import com.bixin.nft.common.enums.NftType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.MapUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
    private BigDecimal score;

    private NftType nftType;
    private List<NftCompositeElement> compositeElements;

    private String occupation;
    private String customName;
    private int sex;


    public static NftSelfSellingVo of(Map<String, Object> map) {
        return NftSelfSellingVo.builder()
                .id(MapUtils.getLong(map, "id"))
                .chainId(MapUtils.getLong(map, "chain_id"))
                .nftBoxId(MapUtils.getLong(map, "nft_box_id"))
                .groupId(MapUtils.getLong(map, "group_id"))
                .type(MapUtils.getString(map, "type"))
                .name(MapUtils.getString(map, "name"))
                .nftName(MapUtils.getString(map, "nft_name"))
                .owner(MapUtils.getString(map, "owner"))
                .payToken(MapUtils.getString(map, "pay_token"))
                .address(MapUtils.getString(map, "address"))
                .sellPrice(BigDecimal.valueOf(MapUtils.getDoubleValue(map, "sell_price")))
                .offerPrice(BigDecimal.valueOf(MapUtils.getDoubleValue(map, "offer_price")))
                .icon(MapUtils.getString(map, "icon"))
                .createTime(MapUtils.getLong(map, "create_time"))
                .updateTime(MapUtils.getLong(map, "update_time"))
                .score(BigDecimal.valueOf(MapUtils.getDoubleValue(map, "score")))
                .build();
    }

}
