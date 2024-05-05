package com.lg.guava.demo.guava;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.lg.guava.demo.Constants;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Jaa
 * @Description: 基于过期时间的删除
 * @Date 2024/5/5
 */
public class Demo3 {

    /**
     * 初始化缓存三个
     *
     * @param cache
     * @throws Exception
     */
    public static void initCache(LoadingCache<String, Object> cache) throws Exception {
        for (int i = 1; i <= 3; i++) {
            // 连接数据源   如果缓存没有则读取数据源
            cache.get(String.valueOf(i));
        }
    }

    /**
     * 显示缓存里的数据
     *
     * @param cache
     */
    public static void displayCache(LoadingCache<String, Object> cache) {
        Iterator<Map.Entry<String, Object>> it = cache.asMap().entrySet().iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }

    }

    /**
     * 读取缓存数据 如果没有则回调源数据并(自动)写入缓存
     *
     * @param key
     * @param cache
     */
    public static void get(String key, LoadingCache<String, Object> cache) throws Exception {
        cache.get(key, new Callable<Object>() {
            @Override // 回调方法用于读源并写入缓存
            public Object call() throws Exception {
                // 读源
                Object value = Constants.hm.get(key);
                // 写回缓存
                // cache.put(key,value);
                return value;
            }
        });
    }

    public static void main(String[] args) throws Exception {
        // CacheLoader的方式创建
        LoadingCache<String, Object> cache = CacheBuilder.newBuilder()
                /**
                 * 加附加的功能
                 */
                .maximumSize(3) // 最大缓存个数
                // 统计
                .recordStats()
                // 缓存中的数据, 如果3秒内没有访问, 则删除
                // .expireAfterAccess(3, TimeUnit.SECONDS)
                // 相当于 expire  ttl 缓存中对象的生命周期为3秒
                .expireAfterWrite(3, TimeUnit.SECONDS)
                // 监听缓存通知
                .removalListener(new RemovalListener<Object, Object>() {
                    @Override
                    public void onRemoval(RemovalNotification<Object, Object> removalNotification) {
                        System.out.println(removalNotification.getKey() + ":" + removalNotification.getCause());
                    }
                })
                .build(new CacheLoader<String, Object>() {
                    // 读取数据源
                    @Override
                    public Object load(String key) throws Exception {
                        return Constants.hm.get(key);
                    }
                });

        // 初始化缓存
        initCache(cache);
        System.out.println(cache.size());
        // 显示缓存数据
        displayCache(cache);
        Thread.sleep(1000);
        // 访问1   1被访问, 最终缓存中会留下1
        cache.getIfPresent("1");
        // 歇 2.1秒
        Thread.sleep(2100);
        System.out.println("==================================");
        displayCache(cache);
        System.out.println(cache.stats().toString()); // 打印输出统计
    }


}
