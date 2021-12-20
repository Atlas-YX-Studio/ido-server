package com.bixin.ido.controller;

import com.bixin.ido.bean.DO.SwapCoins;
import com.bixin.common.response.R;
import com.bixin.common.constants.PathConstant;
import com.bixin.ido.service.ISwapCoinsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhangcheng
 * create  2021-08-26 2:51 下午
 */
@RestController
@RequestMapping(PathConstant.SWAP_REQUEST_PATH_PREFIX + "/coin")
public class SwapCoinsController {

    @Resource
    ISwapCoinsService swapCoinsService;

    @GetMapping("/getAll")
    public R getAll(){

        List<SwapCoins> idoSwapCoins = swapCoinsService.selectByDDL(SwapCoins.builder().build());

        return R.success(idoSwapCoins);
    }

}
