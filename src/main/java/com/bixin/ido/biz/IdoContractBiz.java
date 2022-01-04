package com.bixin.ido.biz;

import com.bixin.common.code.IdoErrorCode;
import com.bixin.common.exception.IdoException;
import com.bixin.common.utils.TypeArgsUtil;
import com.bixin.nft.service.ContractService;
import com.google.common.collect.Lists;
import com.novi.serde.Bytes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.starcoin.bean.ScriptFunctionObj;
import org.starcoin.utils.AccountAddressUtils;
import org.starcoin.utils.BcsSerializeHelper;

import javax.annotation.Resource;
import java.math.BigInteger;

@Slf4j
@Component
public class IdoContractBiz {

    @Resource
    private ContractService contractService;

    public void init() {
        String address = "0xf9079064690b07e9826a4fa3d713aecb";
        if (!contractService.deployContract(address, "contract/ido/Offering.mv", null)) {
            log.error("IDO Offering部署失败");
            throw new IdoException(IdoErrorCode.CONTRACT_DEPLOY_FAILURE);
        }
        if (!contractService.deployContract(address, "contract/ido/OfferingScript.mv", null)) {
            log.error("IDO OfferingScript部署失败");
            throw new IdoException(IdoErrorCode.CONTRACT_DEPLOY_FAILURE);
        }
        ScriptFunctionObj scriptFunctionObj = ScriptFunctionObj
                .builder()
                .moduleAddress(address)
                .moduleName("OfferingScript")
                .functionName("create")
                .tyArgs(Lists.newArrayList(
                        TypeArgsUtil.parseTypeObj("0x5b876a58b0e1cff855b6489cd8cf3bec::DummyToken::STC"),
                        TypeArgsUtil.parseTypeObj("0x5b876a58b0e1cff855b6489cd8cf3bec::DummyToken::USDT"),
                        TypeArgsUtil.parseTypeObj("0x5b876a58b0e1cff855b6489cd8cf3bec::DummyToken::EOS")
                        ))
                .args(Lists.newArrayList(
                        BcsSerializeHelper.serializeU128ToBytes(BigInteger.valueOf(10000000000000L)),
                        BcsSerializeHelper.serializeU128ToBytes(BigInteger.valueOf(1000000000L)),
                        BcsSerializeHelper.serializeU128ToBytes(BigInteger.valueOf(100000000000L)),
                        BcsSerializeHelper.serializeAddressToBytes(AccountAddressUtils.create("0x5b876a58b0e1cff855b6489cd8cf3bec"))
                ))
                .build();
        if (!contractService.callFunction(address, scriptFunctionObj)) {
            log.error("Offering Create初始化失败");
            throw new IdoException(IdoErrorCode.CONTRACT_CALL_FAILURE);
        }
    }

    public void createOffering() {
        String address = "0xf9079064690b07e9826a4fa3d713aecb";
        ScriptFunctionObj scriptFunctionObj = ScriptFunctionObj
                .builder()
                .moduleAddress(address)
                .moduleName("OfferingScript")
                .functionName("create")
                .tyArgs(Lists.newArrayList(
                        TypeArgsUtil.parseTypeObj("0x5b876a58b0e1cff855b6489cd8cf3bec::DummyToken::STC"),
                        TypeArgsUtil.parseTypeObj("0x5b876a58b0e1cff855b6489cd8cf3bec::DummyToken::USDT"),
                        TypeArgsUtil.parseTypeObj("0x5b876a58b0e1cff855b6489cd8cf3bec::DummyToken::DUMMY")
                ))
                .args(Lists.newArrayList(
                        BcsSerializeHelper.serializeU128ToBytes(BigInteger.valueOf(10000000000000L)),
                        BcsSerializeHelper.serializeU128ToBytes(BigInteger.valueOf(1000000000L)),
                        BcsSerializeHelper.serializeU128ToBytes(BigInteger.valueOf(100000000000L)),
                        BcsSerializeHelper.serializeAddressToBytes(AccountAddressUtils.create("0x5b876a58b0e1cff855b6489cd8cf3bec"))
                ))
                .build();
        if (!contractService.callFunction(address, scriptFunctionObj)) {
            log.error("Offering Create初始化失败");
            throw new IdoException(IdoErrorCode.CONTRACT_CALL_FAILURE);
        }
    }

    public void stateChange(byte state) {
        String address = "0xf9079064690b07e9826a4fa3d713aecb";
        ScriptFunctionObj scriptFunctionObj = ScriptFunctionObj
                .builder()
                .moduleAddress(address)
                .moduleName("OfferingScript")
                .functionName("state_change")
                .tyArgs(Lists.newArrayList(
                        TypeArgsUtil.parseTypeObj("0x5b876a58b0e1cff855b6489cd8cf3bec::DummyToken::STC"),
                        TypeArgsUtil.parseTypeObj("0x5b876a58b0e1cff855b6489cd8cf3bec::DummyToken::USDT"),
                        TypeArgsUtil.parseTypeObj("0x5b876a58b0e1cff855b6489cd8cf3bec::DummyToken::ETH")
                ))
                .args(Lists.newArrayList(
                        Bytes.valueOf(BcsSerializeHelper.serializeU8(state))
                ))
                .build();
        if (!contractService.callFunction(address, scriptFunctionObj)) {
            log.error("Offering Create初始化失败");
            throw new IdoException(IdoErrorCode.CONTRACT_CALL_FAILURE);
        }
    }

    public void initToken(String token) {
        String address = "0x5b876a58b0e1cff855b6489cd8cf3bec";
        ScriptFunctionObj scriptFunctionObj = ScriptFunctionObj
                .builder()
                .moduleAddress("0x5b876a58b0e1cff855b6489cd8cf3bec")
                .moduleName("DummyToken")
                .functionName("initialize")
                .tyArgs(Lists.newArrayList(
                        TypeArgsUtil.parseTypeObj(token)
                ))
                .args(Lists.newArrayList())
                .build();
        if (!contractService.callFunction(address, scriptFunctionObj)) {
            log.error("Offering Create初始化失败");
            throw new IdoException(IdoErrorCode.CONTRACT_CALL_FAILURE);
        }
    }

    public void mintToken(String token, BigInteger amount) {
        String address = "0xf9079064690b07e9826a4fa3d713aecb";
        ScriptFunctionObj scriptFunctionObj = ScriptFunctionObj
                .builder()
                .moduleAddress("0x5b876a58b0e1cff855b6489cd8cf3bec")
                .moduleName("DummyToken")
                .functionName("mint_token")
                .tyArgs(Lists.newArrayList(
                        TypeArgsUtil.parseTypeObj(token)
                ))
                .args(Lists.newArrayList(
                        BcsSerializeHelper.serializeU128ToBytes(amount)
                ))
                .build();
        if (!contractService.callFunction(address, scriptFunctionObj)) {
            log.error("Offering Create初始化失败");
            throw new IdoException(IdoErrorCode.CONTRACT_CALL_FAILURE);
        }
    }
}
