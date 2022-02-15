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
 * @author zhangcheng
 * create  2022/1/13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NFTResolveEventDto {

    private String owner;
    //链上 id
    private long id;

}
