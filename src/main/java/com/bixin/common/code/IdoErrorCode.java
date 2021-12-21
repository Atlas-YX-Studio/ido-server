package com.bixin.common.code;

/**
 * @Description: 业务错误码，错误信息在messages中配置
 * @author: 系统
 */
public enum IdoErrorCode implements ErrorCode {

    /**
     * 系统个错误
     */
    SYSTEM_ERROR(100000, "system error"),

    /**
     * 参数错误
     */
    PARAMS_ERROR(100001, "param error"),

    /**
     * 对象为空
     */
    OBJECT_IS_EMPTY(100002, "object is null"),

    /**
     * 查询结果为空
     */
    QUERY_RESULT_IS_EMPTY(100003, "query result is empty"),

    /**
     * 数据验证失败
     */
    DATA_BIND_VALIDATION_FAILURE(100004, "data bind validation failure"),

    /**
     * 数据绑定失败
     */
    DATA_VALIDATION_FAILURE(100005, "data validation failure"),

    /**
     * 文件不存在
     */
    FILE_NOT_EXIST(100006, "file not exist"),

    /**
     * 合约部署失败
     */
    CONTRACT_DEPLOY_FAILURE(100007, "contract deploy failure"),

    /**
     * 合约请求失败
     */
    CONTRACT_CALL_FAILURE(100008, "contract call failure"),

    /**
     * 数据不存在
     */
    DATA_NOT_EXIST(100009, "data not exist"),

    /**
     * 图片上传
     */
    IMAGE_UPLOAD_FAILURE(100010, "image upload failure"),

    /**
     * Token校验失败
     */
    TOKEN_VERIFY_FAILURE(100011, "token verify failure"),

    /**
     * 可提取收益不足
     */
    REWARD_NOT_INSUFFICIENT(100012, "reward not insufficient"),

    /**
     * 存在待处理提取记录
     */
    PENDING_HARVEST_RECORD_EXISTS(100013, "pending harvest record exists"),


    /**
     * 提取收益失败
     */
    REWARD_HARVEST_FAILED(100014, "reward harvest failed"),

    /**
     * 校验失败
     */
    SIGNATURE_VERIFY_FAILED(100015, "signature verify failed"),

    ;

    private final int code;

    private final String message;

    IdoErrorCode(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        return "[" + this.getCode() + "]" + this.getMessage();
    }
}
