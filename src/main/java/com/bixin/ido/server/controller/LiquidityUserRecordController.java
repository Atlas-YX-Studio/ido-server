package com.bixin.ido.server.controller;

import com.bixin.ido.server.bean.DO.LiquidityUserRecord;
import com.bixin.ido.server.bean.dto.P;
import com.bixin.ido.server.constants.PathConstant;
import com.bixin.ido.server.service.ILiquidityUserRecordService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhangcheng
 * create  2021-08-26 2:51 下午
 */
@RestController
@RequestMapping(PathConstant.SWAP_REQUEST_PATH_PREFIX + "/userLiquidityRecord")
public class LiquidityUserRecordController {

    @Resource
    ILiquidityUserRecordService liquidityUserRecordService;

    @GetMapping("/getAllByPage")
    public P getALlByPage(@RequestParam(value = "pageSize", defaultValue = "20") long pageSize,
                          @RequestParam(value = "nextId", defaultValue = "0") long nextId) {

        if (nextId < 0) {
            return P.failed("parameter is invalid");
        }
        List<LiquidityUserRecord> records = liquidityUserRecordService.getALlByPage(pageSize + 1, nextId);

        boolean hasNext = false;
        if (records.size() > pageSize) {
            records = records.subList(0, records.size() - 1);
            hasNext = true;
        }

        return P.success(records, hasNext);

    }

}
