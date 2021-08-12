package com.bixin.ido.server.bean.DO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangcheng
 * @create 2021-08-06 5:34 下午
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdoDxLink {
    private Long id;

    private Long prdId;

    private String name;

    private String url;

    private Long createTime;

    private Long updateTime;

}