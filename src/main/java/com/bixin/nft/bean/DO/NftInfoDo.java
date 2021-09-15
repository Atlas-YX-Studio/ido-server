package com.bixin.nft.bean.DO;

import lombok.Data;

import java.io.Serializable;

/**
* @Class: NftInfoDo
* @Description: NFT信息记录表
* @author: 系统
* @created: 2021-09-15
*/
@Data
public class NftInfoDo implements Serializable {
    /**
     * 主键id
     */
    private Long id;

    /**
     * NFT id
     */
    private Long nft_id;

    /**
     * 所属分组
     */
    private Long group_id;

    /**
     * 名称
     */
    private String name;

    /**
     * 图片链接
     */
    private String image_link;

    /**
     * 创作者
     */
    private String creator;

    /**
     * 所属者
     */
    private String owner;

    /**
     * 已创建
     */
    private Boolean created;

    /**
     * 创建时间
     */
    private Long create_time;

    /**
     * 更新时间
     */
    private Long update_time;

    /**
     * 图片数据
     */
    private String image_data;

    private static final long serialVersionUID = 1L;
}