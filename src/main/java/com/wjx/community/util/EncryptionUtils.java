package com.wjx.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * @author wjx
 * @description
 */
public class EncryptionUtils {

    /**
     * 生成加密盐
     * @return
     */
    public static String createSalt(){
        return UUID.randomUUID().toString().substring(1,7).replace("-","")+"_wjx";
    }

    /**
     * MD5加密
     * @param key
     * @return
     */
    public static String createMD5(String key){
        if (StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     * 激活码生成
     * @return
     */
    public static String createUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }

}
