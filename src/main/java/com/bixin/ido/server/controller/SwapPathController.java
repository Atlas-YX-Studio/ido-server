package com.bixin.ido.server.controller;

import com.bixin.ido.server.bean.bo.SwapBo;
import com.bixin.ido.server.bean.dto.R;
import com.bixin.ido.server.bean.vo.SwapPathInVO;
import com.bixin.ido.server.bean.vo.SwapPathOutVO;
import com.bixin.ido.server.constants.PathConstant;
import com.bixin.ido.server.service.ISwapPathService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping(PathConstant.SWAP_REQUEST_PATH_PREFIX +"/exchange")
public class SwapPathController {

    @Resource
    private ISwapPathService iSwapPathService;

    @PostMapping("/in")
    public R exchangeIn(@RequestBody SwapBo req) {
        SwapPathInVO vo = iSwapPathService.exchangeIn(req.getTokenA(), req.getTokenB(), req.getTokenAmount(), req.getSlippageTolerance());
        return R.success(vo);

    }

    @PostMapping("/out")
    public R exchangeOut(@RequestBody SwapBo req) {
        SwapPathOutVO vo = iSwapPathService.exchangeOut(req.getTokenA(), req.getTokenB(), req.getTokenAmount(), req.getSlippageTolerance());
        return R.success(vo);
    }

    @GetMapping("/totalAssets")
    public R totalAssets() {
        return R.success(iSwapPathService.totalAssets().toPlainString());
    }

}
