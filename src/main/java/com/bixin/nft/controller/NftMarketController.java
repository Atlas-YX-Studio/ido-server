package com.bixin.nft.controller;

import com.bixin.ido.server.bean.DO.SwapUserRecord;
import com.bixin.ido.server.bean.vo.wrap.P;
import com.bixin.ido.server.constants.CommonConstant;
import com.bixin.nft.bean.DO.NftMarketDo;
import com.bixin.nft.core.service.NftMarketService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

import static com.bixin.ido.server.constants.PathConstant.NFT_REQUEST_PATH_PREFIX;

/**
 * @class: NftMarketController
 * @Description: NFT/box市场销售列表 Controller
 * @author: 系统
 * @created: 2021-09-17
 */
@RestController
@RequestMapping(NFT_REQUEST_PATH_PREFIX+"/market")
public class NftMarketController {

    @Resource
    public NftMarketService nftMarketService;

    @GetMapping("/getALL")
    public P getALlByPage(@RequestParam(value = "series", defaultValue = "all") String series,
                          @RequestParam(value = "currency", defaultValue = "all") String currency,
                          @RequestParam(value = "open", defaultValue = "all") String open,
                          @RequestParam(value = "sort", defaultValue = "0") int sort,
                          @RequestParam(value = "pageSize", defaultValue = "20") long pageSize,
                          @RequestParam(value = "pageNum", defaultValue = "0") long pageNum) {

        if (pageNum < 0 || pageSize <= 0 || StringUtils.isEmpty(series)
                || StringUtils.isEmpty(open) || StringUtils.isEmpty(currency)) {
            return P.failed("parameter is invalid");
        }
        List<NftMarketDo> nftMarketDos = nftMarketService.selectByPage(pageSize + 1, pageNum, sort, series, currency, open);

        boolean hasNext = false;
        if (nftMarketDos.size() > pageSize) {
            nftMarketDos = nftMarketDos.subList(0, nftMarketDos.size() - 1);
            hasNext = true;
        }

        return P.success(nftMarketDos, hasNext);
    }

}