package com.bixin.common.constants;

/**
 * @author zhangcheng
 * create          2021-08-12 11:54 上午
 */
public interface CommonConstant {

    String USER_RECORD_EXT_KEY = "userRecordExt";

    int SWAP_EVENT_QUEUE_SIZE = 20000;


    long MAX_PAGE_SIZE = 1000;
    long DEFAULT_PAGE_SIZE = 20;

    String USDT_NAME = "USDT";
    String STC_ADDRESS = "0x00000000000000000000000000000001::STC::STC";
    String KIKO_NAME = "KIKO";

    // redis key
    String IMAGE_INFO_URL_PREFIX_KEY = "image_info_url_";
    String IMAGE_GROUP_URL_PREFIX_KEY = "image_group_url_";

    String SWAP_TOKEN_TICKS_PREFIX_KEY = "swap_token_ticks_";
    String SWAP_TOKEN_MARKET_PREFIX_KEY = "swap_token_market_";

    String SWAP_SYMBOL_TICKS_PREFIX_KEY = "swap_symbol_ticks_";
    String SWAP_SYMBOL_MARKET_PREFIX_KEY = "swap_symbol_market_";

    String VOLUME_INFO_KEY = "volume_info";
    String STC_FEE_PRICE_KEY = "stc_fee_price";

    String NEW_BLOCK_SEQ_PREFIX_KEY = "new_block_seq_";
    String NFT_MINING_SEQ_PREFIX_KEY = "nft_mining_seq_";

    // http header
    String HTTP_X_REQUESTED_WITH = "X-Request-With";
    String HTTP_X_TOKEN = "X-Token";

    String HTTP_AJAX_REQUEST_HEADER = "XMLHttpRequest";

}
