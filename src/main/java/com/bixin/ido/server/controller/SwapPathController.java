package com.bixin.ido.server.controller;

import com.bixin.ido.server.bean.bo.SwapBo;
import com.bixin.ido.server.bean.vo.CoinStatsInfoVO;
import com.bixin.ido.server.bean.vo.wrap.P;
import com.bixin.ido.server.bean.vo.wrap.R;
import com.bixin.ido.server.bean.vo.SwapPathInVO;
import com.bixin.ido.server.bean.vo.SwapPathOutVO;
import com.bixin.ido.server.constants.PathConstant;
import com.bixin.ido.server.service.ISwapPathService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping(PathConstant.SWAP_REQUEST_PATH_PREFIX +"/exchange")
public class SwapPathController {

    @Resource
    private ISwapPathService iSwapPathService;

    @PostMapping("/in")
    public R exchangeIn(@RequestBody SwapBo req) {
        SwapPathInVO vo = iSwapPathService.exchangeIn(req.getTokenA(), req.getTokenB(), req.getTokenAmount(), req.getSlippageTolerance(), req.isMultiMode());
        return R.success(vo);

    }

    @PostMapping("/out")
    public R exchangeOut(@RequestBody SwapBo req) {
        SwapPathOutVO vo = iSwapPathService.exchangeOut(req.getTokenA(), req.getTokenB(), req.getTokenAmount(), req.getSlippageTolerance(), req.isMultiMode());
        return R.success(vo);
    }

    @GetMapping("/totalAssets")
    public R totalAssets() {
        return R.success(iSwapPathService.totalAssets().toPlainString());
    }

    @GetMapping("/coinInfos")
    public P coinInfos(@RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                       @RequestParam(name = "pageSize", defaultValue = "20") int pageSize) {
        List<CoinStatsInfoVO> coinStatsInfoVOS = iSwapPathService.coinInfos(pageNum, pageSize);
        boolean hasNext = false;
        if (coinStatsInfoVOS.size() > pageSize) {
            coinStatsInfoVOS = coinStatsInfoVOS.subList(0, pageSize);
            hasNext = true;
        }
        return P.success(coinStatsInfoVOS, hasNext);
    }

    @GetMapping("/meta")
    public R meta() {
        return R.success(iSwapPathService.meta());
    }

}
