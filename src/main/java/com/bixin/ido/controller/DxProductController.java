package com.bixin.ido.controller;

import com.bixin.ido.bean.DO.IdoDxProduct;
import com.bixin.ido.bean.vo.HomeProductVO;
import com.bixin.common.response.R;
import com.bixin.common.constants.PathConstant;
import com.bixin.ido.common.enums.ProductState;
import com.bixin.ido.service.IDxProductService;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author zhangcheng
 * create 2021-08-06 5:34 下午
 */
@RestController
@RequestMapping(PathConstant.DX_REQUEST_PATH_PREFIX +"/product")
public class DxProductController {

    @Resource
    IDxProductService idoDxProductService;

    @GetMapping("/getAll")
    public R getAll() {

        List<HomeProductVO> initProducts = idoDxProductService.getHomeProducts(ProductState.INIT);
        List<HomeProductVO> processingProducts = idoDxProductService.getHomeProducts(ProductState.PROCESSING);
        List<HomeProductVO> finishProducts = idoDxProductService.getHomeProducts(ProductState.FINISH);

        return R.success(Map.of(
                ProductState.INIT.getDesc(), initProducts,
                ProductState.PROCESSING.getDesc(), processingProducts,
                ProductState.FINISH.getDesc(), finishProducts
        ));

    }

    @GetMapping("/kgStarter")
    public R kgStarter() {

        List<HomeProductVO> previewProducts = idoDxProductService.getHomeProducts(ProductState.PREVIEW);
        List<HomeProductVO> initProducts = idoDxProductService.getHomeProducts(ProductState.INIT);
        List<HomeProductVO> processingProducts = idoDxProductService.getHomeProducts(ProductState.PROCESSING);
        ArrayList<HomeProductVO> homeProductVOS = Lists.newArrayList(Iterables.concat(previewProducts, initProducts, processingProducts));
        homeProductVOS.sort(Comparator.comparingInt(IdoDxProduct::getWeight));
        return R.success(homeProductVOS);

    }

    @GetMapping("/get")
    public R getProduct(@RequestParam(value = "pId", defaultValue = "0") long pId) {
        if (pId <= 0) {
            return R.failed("parameter is invalid");
        }

        IdoDxProduct product = idoDxProductService.getProduct(pId);

        return R.success(product);
    }

}
