package com.bixin.ido.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;

/**
 * NFT质押事件表
 *
 * @author Xiang Feihan
 * @since 2021-11-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("nft_mining_record")
public class NftMiningRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 事件类型
     */
    @TableField("type")
    private String type;

    /**
     * group id
     */
    @TableField("group_id")
    private Long groupId;

    /**
     * 事件序号
     */
    @TableField("seq_number")
    private String seqNumber;

    /**
     * 用户地址
     */
    @TableField("sender")
    private String sender;

    /**
     * NFT info id
     */
    @TableField("info_id")
    private Long infoId;

    /**
     * 序号
     */
    @TableField("`order`")
    private Long order;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Long createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Long updateTime;


}
