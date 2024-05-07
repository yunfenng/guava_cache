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
 * @Description: 基于数据大小的删除
 * @Date 2024/5/5
 */
public class Demo2 {

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
                .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                .refreshAfterWrite(1, TimeUnit.SECONDS)
                // 统计命中率
                .recordStats()
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
        // 读取缓存中2的数据 缓存有就读取,没有返回null
        System.out.println(cache.getIfPresent("1"));

        // 读取4 FIFO     都源并回写缓存 淘汰一个(LRU + FIFO)
        get("4", cache);
        System.out.println("==================================");
        displayCache(cache);
        System.out.println(cache.stats().toString()); // 打印输出统计
    }


}
