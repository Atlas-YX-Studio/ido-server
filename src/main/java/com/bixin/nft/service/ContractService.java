package com.bixin.nft.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bixin.common.code.IdoErrorCode;
import com.bixin.common.exception.IdoException;
import com.bixin.common.exception.SequenceException;
import com.bixin.common.config.StarConfig;
import com.bixin.common.utils.JacksonUtil;
import com.bixin.common.utils.RetryingUtil;
import com.bixin.nft.bean.dto.ChainBalanceDto;
import com.bixin.nft.bean.dto.ChainResponseDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.novi.serde.Bytes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.starcoin.bean.ListResourceOption;
import org.starcoin.bean.ScriptFunctionObj;
import org.starcoin.bean.TypeObj;
import org.starcoin.types.Module;
import org.starcoin.types.Package;
import org.starcoin.types.*;
import org.starcoin.utils.AccountAddressUtils;
import org.starcoin.utils.SignatureUtils;
import org.starcoin.utils.StarcoinClient;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

@Slf4j
@Component
public class ContractService {
    @Resource
    private StarConfig starConfig;

    private StarcoinClient starcoinClient;

    // todo
    private Map<String, String> keyMap = new HashMap<>() {
        {
            put("0x290c7b35320a4dd26f651fd184373fe7", "98e6ded54f698a49365a0a4217d2d5d3cfc516b03c6198b81e47ce0067035c34");
            put("0xa85291039ddad8845d5097624c81c3fd", "67f9969b23ce51050ac2419b1afb1273b949573bb4159db965002ab999b0cba4");
            put("0x69f1e543a3bef043b63bed825fcd2cf6", "b90943e6bd2d69872e86cedcf33c9290d7213e484b1af7d07ea6b719754341ec");
//            test ido
            put("0xf9079064690b07e9826a4fa3d713aecb", "f00d4d28de6c1c7955ac4a573e04f8bfd41ffebda893463a06a113e4e490ad91");
//            test token
            put("0x5b876a58b0e1cff855b6489cd8cf3bec", "9c98e6db317fa2c4921f3dbbedce16a9c87468a89442c5b870bd8bcc8c3f0fe3");
        }
    };

    @PostConstruct
    public void init() {
        load("0x8355417c88d969f656935244641256ad");
        load("0x7ed4261b68ddb20158109794bbab3ae7");
        starcoinClient = new StarcoinClient(starConfig.getClient().getUrl(), starConfig.getClient().getChainId());
    }

    private void load(String address) {
        try {
            FileInputStream inputStream = new FileInputStream("/data/" + address + address + address);
            int len = inputStream.available();
            byte[] data = new byte[len];
            inputStream.read(data);
            keyMap.put(address, new String(data));
        }catch (Exception e) {
            log.info("无关紧要:{}", e);
        }
    }

    /**
     * 获取所有Resource
     *
     * @param senderAddress
     * @return
     */
    public String listResource(String senderAddress) {
        AccountAddress sender = AccountAddressUtils.create(senderAddress);
        ListResourceOption listResourceOption = new ListResourceOption();
        listResourceOption.setDecode(true);
        String result = starcoinClient.call("state.list_resource", Lists.newArrayList(new Object[]{AccountAddressUtils.hex(sender), listResourceOption}));
//        log.info("starCoin resource result:{}", result);
        return result;
    }

    public String getResource(String address, String resourceKey) {
        AccountAddress sender = AccountAddressUtils.create(address);
        ListResourceOption listResourceOption = new ListResourceOption();
        listResourceOption.setDecode(true);
        String result = starcoinClient.call("state.get_resource", Lists.newArrayList(new Object[]{AccountAddressUtils.hex(sender), resourceKey, listResourceOption}));
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
            contractBytes = new ClassPathResource(path).getInputStream().readAllBytes();
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
        return RetryingUtil.retry(
                () -> {
                    String result = callFunction(sender, privateKey, scriptFunctionObj, 10000000L);
                    log.info("合约请求 result: {}", result);
                    String txn = JSON.parseObject(result).getString("result");
                    if (StringUtils.isBlank(txn)) {
                        log.info("合约请求失败");
                        if (result.contains("SEQUENCE_NUMBER_TOO_OLD")) {
                            throw new SequenceException();
                        }
                        return false;
                    }
                    return checkTxt(txn);
                },
                5,
                4000,
                SequenceException.class);
    }

    /**
     * 签名并请求合约
     *
     * @param senderAddress
     * @param scriptFunctionObj
     * @return
     */
    public boolean callFunctionV2(String senderAddress, ScriptFunctionObj scriptFunctionObj, long gas) {
        log.info("合约请求 sender:{}, function: {}", senderAddress, JSON.toJSONString(scriptFunctionObj));
        AccountAddress sender = AccountAddressUtils.create(senderAddress);
        Ed25519PrivateKey privateKey = getPrivateKey(senderAddress);
        return RetryingUtil.retry(
                () -> {
                    String result = callFunction(sender, privateKey, scriptFunctionObj, gas);
                    log.info("合约请求 result: {}", result);
                    String txn = JSON.parseObject(result).getString("result");
                    if (StringUtils.isBlank(txn)) {
                        log.info("合约请求失败");
                        if (result.contains("SEQUENCE_NUMBER_TOO_OLD")) {
                            throw new SequenceException();
                        }
                        return false;
                    }
                    return checkTxt(txn);
                },
                5,
                4000,
                SequenceException.class);
    }

    private String callFunction(AccountAddress sender, Ed25519PrivateKey privateKey,
                                ScriptFunctionObj scriptFunctionObj, long gas) {
        TransactionPayload.ScriptFunction scriptFunction = new TransactionPayload.ScriptFunction(scriptFunctionObj.toScriptFunction());
        long sequenceNumber = starcoinClient.getAccountSequenceNumber(sender);
        ChainId chainId = new ChainId(starConfig.getClient().getChainId().byteValue());
        RawUserTransaction rawUserTransaction = new RawUserTransaction(sender, sequenceNumber, scriptFunction, gas, 1L,
                "0x1::STC::STC", getExpirationTimestampSecs(), chainId);
        return starcoinClient.submitHexTransaction(privateKey, rawUserTransaction);
    }

    private long getExpirationTimestampSecs() {
        //return System.currentTimeMillis() / 1000 + TimeUnit.HOURS.toSeconds(1);
        String resultStr = starcoinClient.call("node.info", Collections.emptyList());
        JSONObject jsonObject = JSON.parseObject(resultStr);
        return jsonObject.getJSONObject("result").getLong("now_seconds") + (2 * 60 * 60);
    }

    /**
     * 签名并请求合约
     *
     * @param senderAddress
     * @param scriptFunctionObj
     * @return
     */
    public String callFunctionAndGetHash(String senderAddress, ScriptFunctionObj scriptFunctionObj) {
        log.info("合约请求 sender:{}, function: {}", senderAddress, JSON.toJSONString(scriptFunctionObj));
        AccountAddress sender = AccountAddressUtils.create(senderAddress);
        Ed25519PrivateKey privateKey = getPrivateKey(senderAddress);
        String result = starcoinClient.callScriptFunction(sender, privateKey, scriptFunctionObj);
        log.info("合约请求 result: {}", result);
        String txn = JSON.parseObject(result).getString("result");
        if (StringUtils.isBlank(txn)) {
            log.info("合约请求失败");
            throw new IdoException(IdoErrorCode.CONTRACT_CALL_FAILURE);
        }
        return txn;
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
        return SignatureUtils.strToPrivateKey(keyMap.get(sender.toLowerCase()));
    }

    public boolean checkTxt(String txn) {
        log.info("合约hash:{}", txn);
        return RetryingUtil.retry(
                () -> {
                    String rst = starcoinClient.getTransactionInfo(txn);
                    JSONObject jsonObject = JSON.parseObject(rst);
                    JSONObject result = jsonObject.getJSONObject("result");
                    if (result == null) {
                        throw new RuntimeException("合约执行中... " + "txn:" + txn);
                    } else {
                        if ("Executed".equalsIgnoreCase(result.getString("status"))) {
                            log.info("合约执行成功，result: {}", result);
                            return true;
                        } else {
                            log.info("合约执行失败，result:{}", result);
                            return false;
                        }
                    }
                },
                60,
                2000,
                Exception.class
        );
    }

    public long getAddressAmount(String address, String token) {
        String resourceKey = "0x00000000000000000000000000000001::Account::Balance<" + token + ">";
        String result = getResource(address, resourceKey);
        ChainResponseDto<ChainBalanceDto> chainBalance = JacksonUtil.readValue(result, new TypeReference<ChainResponseDto<ChainBalanceDto>>() {});
        return Optional.ofNullable(chainBalance)
                .map(ChainResponseDto::getResult)
                .map(ChainResponseDto.ChainResource::getJson)
                .map(ChainBalanceDto::getTokenValue)
                .orElse(0L);
    }

}
