package com.pyre.community.service;

import org.springframework.transaction.annotation.Transactional;

public interface RedisUtilService {

    @Transactional
    String getData(String key);

    @Transactional
    void setData(String key, String value);

    @Transactional
    void setDataExpire(String key, String value, long duration);

    @Transactional
    void deleteData(String key);
}
