package com.bixin.nft.core.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bixin.ido.server.common.errorcode.IdoErrorCode;
import com.bixin.ido.server.common.exception.IdoException;
import com.bixin.ido.server.config.StarConfig;
import com.bixin.ido.server.utils.RetryingUtil;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.novi.serde.Bytes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.starcoin.bean.ListResourceOption;
import org.starcoin.bean.ResourceObj;
import org.starcoin.bean.ScriptFunctionObj;
import org.starcoin.bean.TypeObj;
import org.starcoin.types.*;
import org.starcoin.types.Module;
import org.starcoin.types.Package;
import org.starcoin.utils.AccountAddressUtils;
import org.starcoin.utils.SignatureUtils;
import org.starcoin.utils.StarcoinClient;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class ContractService {
    @Resource
    private StarConfig starConfig;

    private StarcoinClient starcoinClient;

    // todo
    private Map<String, String> keyMap = new HashMap<>(){
        {
            put("0x290c7b35320a4dd26f651fd184373fe7", "0x98e6ded54f698a49365a0a4217d2d5d3cfc516b03c6198b81e47ce0067035c34");
            put("0xd30b4de81d71c1793aa4db4763211e63", "0x908fd8db21674a5bfdb617c2fd588fdd66fbcaf5198007951f24e72ba793f1a6");
        }
    };

    @PostConstruct
    public void init() {
        starcoinClient = new StarcoinClient(starConfig.getClient().getUrl(), starConfig.getClient().getChainId());
    }

    /**
     * 获取Resource
     * @param senderAddress
     * @return
     */
    public String getResource(String senderAddress) {
        AccountAddress sender = AccountAddressUtils.create(senderAddress);
        ListResourceOption listResourceOption = new ListResourceOption();
        listResourceOption.setDecode(true);
        String result = starcoinClient.call("state.list_resource", Lists.newArrayList(new Object[]{AccountAddressUtils.hex(sender), listResourceOption}));
        log.info("result:{}", result);
        return result;
    }

    /**
     * 部署合约
     *
     * @param senderAddress
     * @param path
     * @param scriptFunctionObj
     * @return
     */
    public boolean deployContract(String senderAddress, String path, ScriptFunctionObj scriptFunctionObj) {
        log.info("合约部署 sender:{}, path:{}, function: {}", senderAddress, path, JSON.toJSONString(scriptFunctionObj));
        AccountAddress sender = AccountAddressUtils.create(senderAddress);
        byte[] contractBytes;
        try {
            contractBytes = Files.toByteArray(new ClassPathResource(path).getFile());
        } catch (IOException e) {
            throw new IdoException(IdoErrorCode.FILE_NOT_EXIST, e);
        }
        // 生成payload
        Module module = new Module(new Bytes(contractBytes));
        ScriptFunction sf = Objects.isNull(scriptFunctionObj) ? null : scriptFunctionObj.toScriptFunction();
        Package contractPackage = new Package(sender, Lists.newArrayList(new Module[]{module}), Optional.ofNullable(sf));
        TransactionPayload.Package.Builder builder = new TransactionPayload.Package.Builder();
        builder.value = contractPackage;
        TransactionPayload payload = builder.build();
        // 获取private key
        Ed25519PrivateKey privateKey = getPrivateKey(senderAddress);
        String result = starcoinClient.submitTransaction(sender, privateKey, payload);

        log.info("合约部署 result: {}", result);
        String txn = JSON.parseObject(result).getString("result");
        if (StringUtils.isBlank(txn)) {
            log.info("合约部署失败");
            return false;
        }
        return checkTxt(txn);
    }

    /**
     * 签名并请求合约
     *
     * @param senderAddress
     * @param scriptFunctionObj
     * @return
     */
    public boolean callFunction(String senderAddress, ScriptFunctionObj scriptFunctionObj) {
        log.info("合约请求 sender:{}, function: {}", senderAddress, JSON.toJSONString(scriptFunctionObj));
        AccountAddress sender = AccountAddressUtils.create(senderAddress);
        Ed25519PrivateKey privateKey = getPrivateKey(senderAddress);
        String result = starcoinClient.callScriptFunction(sender, privateKey, scriptFunctionObj);
        log.info("合约请求 result: {}", result);
        String txn = JSON.parseObject(result).getString("result");
        if (StringUtils.isBlank(txn)) {
            log.info("合约请求失败");
            return false;
        }
        return checkTxt(txn);
    }

    /**
     * 转账请求
     *
     * @param senderAddress
     * @param toAddress
     * @param typeObj
     * @param amount
     * @return
     */
    public boolean transfer(String senderAddress, String toAddress, TypeObj typeObj, BigInteger amount) {
        log.info("转账请求 sender:{}, to:{}, token:{}, amount:{}", senderAddress, toAddress, typeObj.toRPCString(), amount);
        AccountAddress sender = AccountAddressUtils.create(senderAddress);
        Ed25519PrivateKey privateKey = getPrivateKey(senderAddress);
        String result = starcoinClient.transfer(sender, privateKey, AccountAddressUtils.create(toAddress),
                typeObj, amount);
        log.info("转账请求 result: {}", result);
        String txn = JSON.parseObject(result).getString("result");
        if (StringUtils.isBlank(txn)) {
            log.info("转账请求失败");
            return false;
        }
        return checkTxt(txn);
    }

    // todo
    private Ed25519PrivateKey getPrivateKey(String sender) {
        return SignatureUtils.strToPrivateKey(keyMap.get(sender));
    }

    public boolean checkTxt(String txn) {
        log.info("交易hash:{}", txn);
        try {
            return RetryingUtil.retry(
                    () -> {
                        String rst = starcoinClient.getTransactionInfo(txn);
                        JSONObject jsonObject = JSON.parseObject(rst);
                        JSONObject result = jsonObject.getJSONObject("result");
                        if (result == null) {
                            throw new RuntimeException("交易执行中...");
                        } else {
                            if ("Executed".equalsIgnoreCase(result.getString("status"))) {
                                log.info("交易执行成功，result: {}", result);
                                return true;
                            } else {
                                log.info("交易执行失败，result:{}", result);
                                return false;
                            }
                        }
                    },
                    20,
                    5000
            );
        } catch (Exception e) {
            log.info("交易执行失败，txn：{}", txn);
            return false;
        }
    }

}
