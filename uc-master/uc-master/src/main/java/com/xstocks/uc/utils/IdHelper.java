package com.xstocks.uc.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class IdHelper {

    private static final DateTimeFormatter FORMATTER_MS = DateTimeFormatter.ofPattern("mmSSS");

//    private final static String IP_SEGMENT = formatFromIp();

    /**
     * 时间+序列+内网机器ip段后面3段（12+4+9） 25位
     */
    private final static String id_template = "u%s%03d";

    private static final String ID_GENERATE_GROUP_DEFAULT = "default";

    private static final Cache<String, AtomicInteger> cache = CacheBuilder.newBuilder()
            .maximumSize(3000)
            .expireAfterWrite(3000, TimeUnit.SECONDS)
            .build();

    private IdHelper() {
    }

    /**
     * 生成id序列
     *
     * @param num 生成id的数量
     */
    public static List<String> generateId(int num) {
        String time = LocalDateTime.now().format(FORMATTER_MS);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            list.add(generateFromTime(time));
        }
        return list;
    }

    /**
     * 生成id序列
     */
    public static String generateId() {
        String time = LocalDateTime.now().format(FORMATTER_MS);
        return generateFromTime(time);
    }

    private synchronized static String generateFromTime(String time) {
        AtomicInteger atom = cache.getIfPresent(time);
        if (atom == null) {
            atom = new AtomicInteger();
            cache.put(time, atom);
        }

        int seq = atom.addAndGet(1);

        if (seq > 888) {
            return generateFromTime(Long.toString(Long.parseLong(time) + 1));
        }

        return String.format(id_template, time, seq);
    }

    private static String formatFromIp() {
        byte[] bytes = IpHelper.getLocalAddress().getAddress();
        Assert.isTrue(bytes.length == 4, "无效的ip地址");
        return String.format("%03d%03d%03d", Byte.toUnsignedInt(bytes[1]), Byte.toUnsignedInt(bytes[2]),
                Byte.toUnsignedInt(bytes[3]));
    }

}
