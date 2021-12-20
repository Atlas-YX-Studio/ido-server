package com.bixin.ido.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhangcheng
 * create          2021-08-12 11:16 上午
 */
@Getter
@AllArgsConstructor
public enum UserPledgeType {

    PLEDGE(1),
    RELEASE(2);


    private int code;

    public static UserPledgeType of(int code) {
        switch (code) {
            case 1:
                return PLEDGE;
            case 2:
                return RELEASE;
            default:
                return null;
        }
    }


}
