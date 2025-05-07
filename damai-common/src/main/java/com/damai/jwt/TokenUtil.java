package com.damai.jwt;

import com.alibaba.fastjson.JSONObject;
import com.damai.enums.BaseCode;
import com.damai.exception.DaMaiFrameException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
*@author: haonan
*@description:
*/
@Slf4j
public class TokenUtil {

    public static String createToken(String id, String info, long ttlMillis, String tokenSecret) {
        long nowMillis = System.currentTimeMillis();
        // 将自定义密钥字符串转换为SecretKey对象
        SecretKey key = Keys.hmacShaKeyFor(tokenSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .id(id)
                .issuedAt(new Date(nowMillis))
                .subject(info)
                .expiration(ttlMillis >= 0 ? new Date(nowMillis + ttlMillis) : null)
                .signWith(key)
                .compact();
    }

    public static String parseToken(String token, String tokenSecret) {
        try {
            // 将自定义密钥字符串转换为SecretKey对象
            SecretKey key = Keys.hmacShaKeyFor(tokenSecret.getBytes(StandardCharsets.UTF_8));
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (ExpiredJwtException jwtException) {
            log.error("parseToken error", jwtException);
            throw new DaMaiFrameException(BaseCode.TOKEN_EXPIRE);
        }
    }

    public static void main(String[] args) {
        // 这里使用一个32字节（256位）的密钥字符串
        String tokenSecret = "CSYZWECHAT123456789012345678901234567890";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("001key", "001value");
        jsonObject.put("002key", "001value");

        String token1 = TokenUtil.createToken("1", jsonObject.toJSONString(), 10000, tokenSecret);
        System.out.println("token:" + token1);

        String subject = TokenUtil.parseToken(token1, tokenSecret);
        System.out.println("解析token后的值:" + subject);
    }
}
