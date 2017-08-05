package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/8/5.
 */
public class TokenCache {
    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);

    public static final String TOKEN_PREFIX = "token_";

    private static LoadingCache<String, String> localCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(2000).expireAfterAccess(12, TimeUnit.HOURS).build(new CacheLoader<String, String>() {
        //默认的数据加载实现，当调用get取值的时候，如果key没有对应的值，就调用对应的方法进行加载
        @Override
        public String load(String s) throws Exception {
            return "null";
        }
    });


    public static void setKey(String key, String value){
        localCache.put(key, value);
    }

    public static String getByKey(String key){
        String value = null;
        try {
            value = localCache.get(key);
            if ("null".equals(value)){
                return null;
            }

            return value;

        } catch (ExecutionException e) {
            logger.error("locache get errors", e);
            e.printStackTrace();
        }

        return null;
    }
}
