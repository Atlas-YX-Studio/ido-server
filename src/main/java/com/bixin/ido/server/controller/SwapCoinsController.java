package com.bixin.ido.server.controller;

import com.bixin.ido.server.bean.DO.SwapCoins;
import com.bixin.ido.server.bean.vo.wrap.R;
import com.bixin.ido.server.constants.PathConstant;
import com.bixin.ido.server.service.ISwapCoinsService;
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
