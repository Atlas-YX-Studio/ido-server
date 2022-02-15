package com.bixin.nft.service;

import com.bixin.nft.bean.DO.NftCompositeCard;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * NFT 卡牌 服务类
 *
 * @author Xiang Feihan
 * @since 2021-12-20
 */
public interface NftCompositeCardService extends IService<NftCompositeCard> {

    void createCompositeNFT(long cardGroupId);

}
