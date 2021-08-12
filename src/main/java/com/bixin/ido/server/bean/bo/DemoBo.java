package com.bixin.ido.server.bean.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description: Bo 接受前端请求的参数
 * @author: 系统
 */
@Data
public class DemoBo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String userType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

}