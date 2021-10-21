package com.bixin.ido.server.constants;

/**
 * @author zhangcheng
 * create          2021-08-12 11:54 上午
 */
public interface CommonConstant {

    String USER_RECORD_EXT_KEY = "userRecordExt";

    int SWAP_EVENT_QUEUE_SIZE = 20000;


    long MAX_PAGE_SIZE = 1000;
    long DEFAULT_PAGE_SIZE = 20;

    String IMAGE_INFO_URL_PREFIX_KEY = "image_info_url_";
    String IMAGE_GROUP_URL_PREFIX_KEY = "image_group_url_";

    String HTTP_X_REQUESTED_WITH = "X-Request-With";
    String HTTP_X_TOKEN = "X-Token";

    String HTTP_AJAX_REQUEST_HEADER = "XMLHttpRequest";

}
