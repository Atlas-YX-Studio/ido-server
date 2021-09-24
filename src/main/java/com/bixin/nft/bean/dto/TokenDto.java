package com.bixin.nft.bean.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 币种
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {

    private String address;

    private String name;

    private Integer precision;

    public static List<TokenDto> of(String tokenListString) {
        if (StringUtils.isBlank(tokenListString)) {
            return List.of();
        }
        List<TokenDto> tokenList = JSON.parseObject(tokenListString, new TypeReference<>() {});
        if (CollectionUtils.isEmpty(tokenList)) {
            return List.of();
        }
        return tokenList;
    }

}
