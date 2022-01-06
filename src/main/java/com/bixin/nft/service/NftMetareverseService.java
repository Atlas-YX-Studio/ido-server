package com.bixin.nft.service;

import com.bixin.common.response.R;

import java.util.List;
import java.util.Map;

/**
 * @author zhangcheng
 * create  2021/12/23
 */
public interface NftMetareverseService {

    List<Map<String, Object>> getSumByOccupationGroup();

    String compositeCard(String customName, String userAddress, List<Long> elementIds);

    R analysisCard(String userAddress, long cardId);

}
