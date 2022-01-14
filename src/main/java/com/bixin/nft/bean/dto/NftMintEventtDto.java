package com.bixin.nft.bean.dto;

import com.bixin.common.utils.LocalDateTimeUtil;
import com.bixin.nft.bean.DO.NftEventDo;
import com.bixin.nft.common.enums.NftEventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 铸造 nft
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NftMintEventtDto {

    // nft id
    private Long id;
    // 铸造者
    private String creator;

    private Long background_id;
    private Long fur_id;
    private Long clothes_id;
    private Long expression_id;
    private Long head_id;
    private Long accessories_id;
    private Long eyes_id;
    private Long hat_id;
    private Long costume_id;
    private Long makeup_id;
    private Long shoes_id;
    private Long mouth_id;
    private Long earring_id;
    private Long necklace_id;
    private Long neck_id;
    private Long hair_id;
    private Long horn_id;
    private Long hands_id;
    private Long body_id;
    private Long skin_id;
    private Long tattoo_id;
    private Long people_id;
    private Long characteristic_id;
    private Long hobby_id;
    private Long zodiac_id;
    private Long action_id;
    private Long toys_id;
    private Long fruits_id;
    private Long vegetables_id;
    private Long meat_id;
    private Long beverages_id;
    private Long food_id;
    private Long vehicle_id;
    private Long weather_id;
    private Long month_id;
    private Long sports_id;
    private Long music_id;
    private Long movies_id;
    private Long season_id;
    private Long outfit_id;
    private Long face_id;
    private Long arm_id;
    private Long leg_id;
    private Long foot_id;
    private Long weapon_id;
    private Long helmet_id;
    private Long armor_id;
    private Long mecha_id;
    private Long pants_id;
    private Long skirt_id;
    private Long left_hand_id;
    private Long right_hand_id;
    private Long pets_id;
    private Long gifts_id;
    private Long tail_id;

    public static NftEventDo of(NftMintEventtDto dto) {
        NftEventDo.NftEventDoBuilder builder = NftEventDo.builder()
                .nftId(dto.getId())
                .creator(dto.getCreator())
                .seller("")
                .sellingPrice(BigDecimal.ZERO)
                .bider("")
                .bidPrice(BigDecimal.ZERO)
                .type(NftEventType.NFT_MINT_EVENT.getDesc())
                .createTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                .updateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()));
        builder.payToken("");
        return builder.build();
    }
}
