package com.bixin.ido.server.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

/**
 * JWT工具类
 */
@Slf4j
public class JwtUtil {

    private static String HMAC256_KEY = "4858BAe65490df199FE8D76aC9087620";

    private static int EXPIRED = 20 * 60 * 1000;

    private static String ISSUER = "kiko";

    /**
     * 生成jwt token
     */
    public static String encode() {
        Algorithm algorithm = Algorithm.HMAC256(HMAC256_KEY);
        return JWT.create()
                .withIssuedAt(new Date())
                .withExpiresAt(DateUtils.addMilliseconds(new Date(), EXPIRED))
                .withIssuer(ISSUER)
                .sign(algorithm);
    }

    /**
     * 验证jwt token
     */
    public static boolean decode(String token) {
        if (StringUtils.isBlank(token)) {
            log.info("JwtDecode failure: token is empty");
            return false;
        }
        try {
            Algorithm algorithm = Algorithm.HMAC256(HMAC256_KEY);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .acceptExpiresAt(0)
                    .build();
            verifier.verify(token);
            return true;
        } catch (Exception e) {
            log.error("JwtDecode failure, token:{}", token, e);
            return false;
        }
    }

}
