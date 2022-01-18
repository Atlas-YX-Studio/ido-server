package com.bixin.nft.service;

import com.bixin.common.response.R;
import com.bixin.nft.bean.DO.NftCompositeCard;
import com.bixin.nft.bean.DO.NftCompositeElement;
import com.bixin.nft.bean.bo.CompositeCardBean;
import com.bixin.nft.bean.vo.NftSelfResourceVo;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zhangcheng
 * create  2021/12/23
 */
public interface NftMetareverseService {

    List<NftCompositeCard> getCompositeCard(long nftId);

    List<NftCompositeCard> getCompositeCards(List<Long> nftIds);

    List<NftCompositeElement> getCompositeElements(Set<Long> nftIds);

    List<Map<String, Object>> getSumByOccupationGroup();

    Map<String, Object> compositeCard(CompositeCardBean bean);

    R analysisCard(String userAddress, long cardId);

    NftSelfResourceVo selfResource(String userAddress, String nftType);

}
