package com.bixin.common.utils;

import com.bixin.common.code.IdoErrorCode;
import com.bixin.common.exception.IdoException;
import com.novi.bcs.BcsDeserializer;
import com.novi.serde.Bytes;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.starcoin.types.AccountAddress;
import org.starcoin.types.Ed25519PublicKey;
import org.starcoin.types.TransactionAuthenticator;
import org.starcoin.utils.AccountAddressUtils;
import org.starcoin.utils.Hex;

import java.util.Arrays;
import java.util.Date;

@Slf4j
public class StcSignatureUtil {

    @SneakyThrows
    public static String getAddress(String signature) {
        BcsDeserializer deserializer = new BcsDeserializer(Hex.decode(signature));
        AccountAddress accountAddress = AccountAddress.deserialize(deserializer);
        if (StringUtils.isBlank(accountAddress.toString())) {
            log.info("STC签名校验失败, signature: {}", signature);
            throw new IdoException(IdoErrorCode.SIGNATURE_VERIFY_FAILED);
        }
        Bytes messageBytes = deserializer.deserialize_bytes();
        if (messageBytes == null) {
            log.info("STC签名校验失败, signature: {}", signature);
            throw new IdoException(IdoErrorCode.SIGNATURE_VERIFY_FAILED);
        }
        String signTime = new String(messageBytes.content());
        long startTime = DateUtils.addMinutes(new Date(), -30).getTime();
        if (Long.parseLong(signTime) < DateUtils.addMinutes(new Date(), -30).getTime()) {
            log.info("STC签名校验失败, signTime: {}, startTime:{}", signTime, startTime);
            throw new IdoException(IdoErrorCode.SIGNATURE_VERIFY_FAILED);
        }

        TransactionAuthenticator.Ed25519 authenticator = (TransactionAuthenticator.Ed25519) TransactionAuthenticator.deserialize(deserializer);
        String accountFromPublicKey = getFromPublicKey(authenticator.public_key).toString();
        if (!StringUtils.equalsIgnoreCase(accountAddress.toString(), accountFromPublicKey)) {
            log.info("STC签名校验失败, accountAddress: {}, accountFromPublicKey: {}", accountAddress, accountFromPublicKey);
            throw new IdoException(IdoErrorCode.SIGNATURE_VERIFY_FAILED);
        }
        return accountAddress.toString();
    }

    private static AccountAddress getFromPublicKey(Ed25519PublicKey publicKey) {
        byte[] rawBytes = com.google.common.primitives.Bytes.concat(publicKey.value.content(), new byte[]{0});
        byte[] digestedBytes = new SHA3.Digest256().digest(rawBytes);
        byte[] addressBytes = Arrays.copyOfRange(digestedBytes, digestedBytes.length - 16, digestedBytes.length);
        return AccountAddressUtils.create(addressBytes);
    }

}
