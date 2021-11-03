package com.bixin.ido.server.service;

import com.bixin.ido.server.bean.DO.IdoDxUserRecord;
import com.bixin.ido.server.bean.bo.UserRecordReqBo;

import java.util.List;

/**
 * @author zhangcheng
 * create          2021-08-12 11:06 上午
 */
public interface IDxUserRecordService {

    int updateUserRecord(UserRecordReqBo bean);

    int updateUserRecord(IdoDxUserRecord record);

    List<IdoDxUserRecord> getUserRecord(IdoDxUserRecord record);

    List<IdoDxUserRecord> selectALlByPage(IdoDxUserRecord record, long from, long pageSize);

}
