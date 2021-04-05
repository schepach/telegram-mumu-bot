package ru.mumu.bot.entity;

import redis.clients.jedis.Jedis;

import java.util.List;

public class RedisEntity {

    private static final Jedis REDIS_STORE = new Jedis("localhost", 6379);
    private static final RedisEntity REDIS_ENTITY = new RedisEntity();

    public static RedisEntity getInstance() {
        return REDIS_ENTITY;
    }

    public List<String> getElements(String key) {
        return REDIS_STORE.lrange(key, 0, -1);
    }

    public String getElement(String key) {
        return REDIS_STORE.get(key);
    }

    public void setElement(String key, String value) {
        REDIS_STORE.set(key, value);
    }

    public void pushElement(String key, String value) {
        if (!this.getElements(key).contains(value))
            REDIS_STORE.rpush(key, value);
    }

    public void deleteElement(String key) {
        REDIS_STORE.del(key);
    }

}