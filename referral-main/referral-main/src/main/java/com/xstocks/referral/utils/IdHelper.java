package com.xstocks.referral.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class IdHelper {

    // 使用AtomicLong来确保线程安全
    private static AtomicLong counter = new AtomicLong(0);

    public static synchronized String generateOrderId() {
        // 获取当前时间戳
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = sdf.format(new Date());

        // 生成一个1到999之间的随机数

        int randomNum = ThreadLocalRandom.current().nextInt(1, 1000);

        // 使用AtomicLong生成一个递增的计数器值
        long count = counter.incrementAndGet();

        // 拼接订单号
        return timestamp + randomNum + count;
    }

}
