package com.bixin.nft.bean.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChainBalanceDto {

    private ChainTokenDto token;

    public Long getTokenValue() {
        return Optional.ofNullable(token).map(ChainTokenDto::getValue).orElse(0L);
    }

}
