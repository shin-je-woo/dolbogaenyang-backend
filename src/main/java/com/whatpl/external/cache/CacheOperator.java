package com.whatpl.external.cache;

public interface CacheOperator {
    void put(String key, Object value, long expirationTime);
    String get(String key);
    boolean exists(String key);
    void delete(String key);
}