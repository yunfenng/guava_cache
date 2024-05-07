package com.lg.guava.demo.guava.lru;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author: Jaa
 * @Description:
 * @Date 2024/5/7
 */
public class LRUCache<k, v> extends LinkedHashMap<k, v> {

    private final int limit;

    public LRUCache(int limit) {
        // 初始化 accessOrder ： true 改变尾结点
        super(16, 0.75f, true);
        this.limit = limit;
    }

    //是否删除最老的数据
    @Override
    protected boolean removeEldestEntry(Map.Entry<k, v> eldest) {
        return size() > limit;
    }

}
