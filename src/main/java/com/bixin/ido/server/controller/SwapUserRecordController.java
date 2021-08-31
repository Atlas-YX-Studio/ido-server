package com.bixin.ido.server.controller;

import com.bixin.ido.server.bean.DO.LiquidityUserRecord;
import com.bixin.ido.server.bean.DO.SwapUserRecord;
import com.bixin.ido.server.bean.dto.P;
import com.bixin.ido.server.constants.PathConstant;
import com.bixin.ido.server.service.ISwapUserRecordService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhangcheng
 * create  2021-08-26 2:52 下午
 */
@RestController
@RequestMapping(PathConstant.SWAP_REQUEST_PATH_PREFIX + "/userSwapRecord")
public class SwapUserRecordController {

    @Resource
    ISwapUserRecordService swapUserRecordService;


    @GetMapping("/getAllByPage")
    public P getALlByPage(@RequestParam(value = "pageSize", defaultValue = "20") long pageSize,
                          @RequestParam(value = "userAddress", defaultValue = "") String userAddress,
                          @RequestParam(value = "nextId", defaultValue = "0") long nextId) {

        if (nextId < 0 || StringUtils.isEmpty(userAddress)) {
            return P.failed("parameter is invalid");
        }
        List<SwapUserRecord> records = swapUserRecordService.getALlByPage(userAddress, pageSize + 1, nextId);

        boolean hasNext = false;
        if (records.size() > pageSize) {
            records = records.subList(0, records.size() - 1);
            hasNext = true;
        }

        return P.success(records, hasNext);

    }


}
