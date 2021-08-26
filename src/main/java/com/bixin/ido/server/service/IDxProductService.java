package com.bixin.ido.server.service;

import com.bixin.ido.server.bean.DO.IdoDxProduct;
import com.bixin.ido.server.bean.vo.HomeProductVO;
import com.bixin.ido.server.enums.ProductState;

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
