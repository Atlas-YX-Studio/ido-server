package com.bixin.ido.service;

import com.bixin.ido.bean.DO.IdoDxProduct;
import com.bixin.ido.bean.vo.HomeProductVO;
import com.bixin.ido.common.enums.ProductState;

import java.util.List;

/**
 * @author zhangcheng
 * create 2021-08-06 5:00 下午
 */
public interface IDxProductService {

    List<HomeProductVO> getHomeProducts(ProductState productState);

    List<IdoDxProduct> getProducts4UpdateState(long currentTime, ProductState productState);

    List<IdoDxProduct> getLastFinishProducts(long intervalTime);

    IdoDxProduct getProduct(long pId);

    int updateProduct(IdoDxProduct product);

}
