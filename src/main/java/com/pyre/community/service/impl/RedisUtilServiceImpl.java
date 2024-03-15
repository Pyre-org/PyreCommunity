package com.pyre.community.service.impl;

import com.pyre.community.service.RedisUtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisUtilServiceImpl implements RedisUtilService {
    private final StringRedisTemplate redisTemplate;//Redis에 접근하기 위한 Spring의 Redis 템플릿 클래스

    @Override
    @Transactional
    public String getData(String key){//지정된 키(key)에 해당하는 데이터를 Redis에서 가져오는 메서드
        ValueOperations<String,String> valueOperations = this.redisTemplate.opsForValue();
        return valueOperations.get(key);
    }
    @Override
    @Transactional
    public void setData(String key, String value){//지정된 키(key)에 값을 저장하는 메서드
        ValueOperations<String,String> valueOperations = this.redisTemplate.opsForValue();
        valueOperations.set(key,value);
    }
    @Override
    @Transactional
    public void setDataExpire(String key, String value, long duration){//지정된 키(key)에 값을 저장하고, 지정된 시간(duration) 후에 데이터가 만료되도록 설정하는 메서드
        ValueOperations<String,String> valueOperations = this.redisTemplate.opsForValue();
        Duration expireDuration=Duration.ofSeconds(duration);
        valueOperations.set(key,value,expireDuration);

    }
    @Override
    @Transactional
    public void deleteData(String key){//지정된 키(key)에 해당하는 데이터를 Redis에서 삭제하는 메서드
        this.redisTemplate.delete(key);
    }
}
