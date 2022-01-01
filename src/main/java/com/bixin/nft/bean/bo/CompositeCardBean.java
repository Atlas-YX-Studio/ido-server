package com.bixin.nft.bean.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zhangcheng
 * create  2021/12/27
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompositeCardBean {

    private String userAddress;

    private String customName;

    private List<Long> elementIds;

}
