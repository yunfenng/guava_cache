package com.lg.guava.demo;

import java.util.HashMap;
import java.util.Map;

public final class Constants {

    /**
     * 模拟数据源
     */
    public static Map<String, String> hm = new HashMap();

    static {
        hm.put("1", "张飞");
        hm.put("2", "赵云");
        hm.put("3", "马超");
        hm.put("4", "关羽");
        hm.put("5", "黄忠");
    }

}
