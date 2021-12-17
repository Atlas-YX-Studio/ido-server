package com.bixin.ido.controller;

import com.bixin.ido.bean.bo.UserRecordReqBo;
import com.bixin.common.response.R;
import com.bixin.common.constants.PathConstant;
import com.bixin.ido.service.IDxUserRecordService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author zhangcheng
 * create 2021-08-11 8:17 下午
 */
@RestController
@RequestMapping(PathConstant.DX_REQUEST_PATH_PREFIX + "/user")
public class DxUserRecordController {

    @Resource
    IDxUserRecordService idoDxUserRecordService;

    @PostMapping("/updateUserRecord")
    public R updateUserRecord(@RequestBody UserRecordReqBo bean) {
        if (StringUtils.isEmpty(bean.getPrdAddress()) || StringUtils.isEmpty(bean.getUserAddress())) {
            return R.failed("address is empty");
        }
        if (bean.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return R.failed("amount is invalid");
        }
        idoDxUserRecordService.updateUserRecord(bean);

        return R.success(null);
    }

}
