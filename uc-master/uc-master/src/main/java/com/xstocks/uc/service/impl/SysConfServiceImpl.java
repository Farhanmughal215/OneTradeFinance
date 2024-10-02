package com.xstocks.uc.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xstocks.uc.mapper.SysConfMapper;
import com.xstocks.uc.pojo.po.SysConfPO;
import com.xstocks.uc.pojo.po.UserPO;
import com.xstocks.uc.service.SysConfService;
import com.xstocks.uc.pojo.constants.CommonConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.redisson.api.MapOptions;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.api.map.MapLoader;
import org.redisson.api.map.MapWriter;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author firtuss
 * @description 针对表【sys_conf(system configuration)】的数据库操作Service实现
 * @createDate 2023-09-05 17:07:07
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysConfServiceImpl extends ServiceImpl<SysConfMapper, SysConfPO>
        implements SysConfService {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private SysConfMapper sysConfMapper;

    private RMap<String, String> configCache;

    @PostConstruct
    void init() {

        MapOptions<String, String> options = MapOptions.<String, String>defaults()
                .loader(new MapLoader<String, String>() {
                    @Override
                    public String load(String key) {
                        SysConfPO sysConfPO =
                                getOne(Wrappers.<SysConfPO>lambdaQuery().eq(SysConfPO::getName, key), false);
                        return Optional.ofNullable(sysConfPO).map(SysConfPO::getValue).orElse(StringUtils.EMPTY);
                    }

                    @Override
                    public Iterable<String> loadAllKeys() {
                        return list().stream().map(SysConfPO::getName).collect(Collectors.toList());
                    }
                })
                .writer(new MapWriter<String, String>() {
                    @Override
                    public void write(Map<String, String> map) {
                        if (!map.containsKey("updateBy")) {
                            return;
                        }
                        Long updateBy = NumberUtils.toLong(map.get("updateBy"));

                        map.forEach((k, v) -> {
                            SysConfPO sysConfPO =
                                    getOne(Wrappers.<SysConfPO>lambdaQuery().eq(SysConfPO::getName, k).last("LIMIT 1"));
                            if (Objects.nonNull(sysConfPO)) {
                                if (!v.equals(sysConfPO.getValue())) {
                                    sysConfPO.setValue(v);
                                    sysConfPO.setUpdateTime(new Date());
                                    sysConfPO.setUpdateBy(updateBy);
                                    updateById(sysConfPO);
                                }
                            } else {
                                sysConfPO = new SysConfPO();
                                sysConfPO.setName(k)
                                        .setValue(v)
                                        .setDescription(StringUtils.EMPTY)
                                        .setCreateTime(new Date())
                                        .setUpdateTime(new Date());
                                save(sysConfPO);
                            }
                        });
                    }

                    @Override
                    public void delete(Collection<String> keys) {
                        remove(Wrappers.<SysConfPO>lambdaQuery().in(SysConfPO::getName, keys));
                    }
                })
                .writeMode(MapOptions.WriteMode.WRITE_BEHIND)
                .writeBehindDelay(1000)
                .writeBehindBatchSize(100);

        configCache = redissonClient.getMap(CommonConstant.SYS_CONF, StringCodec.INSTANCE, options);

        Set<String> configKeys = sysConfMapper.selectList(Wrappers.emptyWrapper()).stream().map(SysConfPO::getName).collect(Collectors.toSet());
        configCache.loadAll(configKeys, true, 1);
    }

    @Override
    public Map<String, String> getAllBizConfig() {
        return configCache.readAllMap();
    }

    @Override
    public void updateBizConfig(Map<String, String> map, UserPO updateBy) {
        map.put("updateBy", updateBy.getId().toString());
        configCache.putAll(map);
    }

    @Override
    public void loadBizConfig(Set<String> keys) {
        configCache.loadAll(keys, true, 1);
    }

    @Override
    public void delBizConfig(Set<String> keys) {
        keys.forEach(key -> {
            configCache.remove(key);
        });
    }

    @Override
    public String getBizConfig(String key) {
        return configCache.get(key);
    }
}




