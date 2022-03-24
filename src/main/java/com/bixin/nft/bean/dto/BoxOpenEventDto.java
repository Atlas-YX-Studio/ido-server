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
 * 开盲盒事件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoxOpenEventDto {

    // nft id
    private Long id;
    // 开盲盒的人
    private String owner;

    public static NftEventDo of(BoxOpenEventDto dto) {
        NftEventDo.NftEventDoBuilder builder = NftEventDo.builder()
                .nftId(dto.getId())
                .creator(dto.getOwner())
                .seller("")
                .sellingPrice(BigDecimal.ZERO)
                .bider("")
                .bidPrice(BigDecimal.ZERO)
                .type(NftEventType.BOX_OPEN_EVENT_V2.getDesc())
                .createTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                .updateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()));
        builder.payToken("");
        return builder.build();
    }
}
