package com.bixin.ido.bean.vo;

import com.bixin.ido.bean.DO.IdoDxAttribute;
import com.bixin.ido.bean.DO.IdoDxLabel;
import com.bixin.ido.bean.DO.IdoDxLink;
import com.bixin.ido.bean.DO.IdoDxProduct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author zhangcheng
 * create 2021-08-06 5:44 下午
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class HomeProductVO extends IdoDxProduct {

    private List<IdoDxAttribute> attributes;

    private List<IdoDxLabel> labels;

    private List<IdoDxLink> links;

}
