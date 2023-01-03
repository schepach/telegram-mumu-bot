package ru.mumu.bot.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RedisEntity {

    private static JedisPool jedisPool;
    private static RedisEntity REDIS_ENTITY;
    private static final Logger logger = Logger.getLogger(RedisEntity.class.getSimpleName());

    public static RedisEntity getInstance() {
        if (REDIS_ENTITY == null) {
            try {
                logger.log(Level.SEVERE, "REDIS_ENTITY is null...");
                logger.log(Level.SEVERE, "Initialize JedisPool...");
                jedisPool = new JedisPool(buildJedisPoolConfig(), "localhost", 6379);
                logger.log(Level.SEVERE, "Initialize JedisPool done\nReturn new RedisEntity...");
                REDIS_ENTITY = new RedisEntity();
            } catch (Exception ex) {
                if (jedisPool != null) {
                    logger.log(Level.SEVERE, "Destroy jedisPool ...");
                    jedisPool.close();
                }
            }
        }
        return REDIS_ENTITY;
    }

    private static JedisPoolConfig buildJedisPoolConfig() {
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(40);
        poolConfig.setMaxIdle(20);
        poolConfig.setMinIdle(20);
        return poolConfig;
    }

    public List<String> getElements(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lrange(key, 0, -1);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getElement(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void setElement(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, value);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void pushElement(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            if (!this.getElements(key).contains(value)) {
                jedis.rpush(key, value);
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void lPushElement(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            if (!this.getElements(key).contains(value)) {
                jedis.lpush(key, value);
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void remElement(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            if (this.getElements(key).contains(value)) {
                jedis.lrem(key, 0, value);
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void hashSet(String key, String filed, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hset(key, filed, value);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public String hashGet(String key, String field) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hget(key, field);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Map<String, String> hashGetAll(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hgetAll(key);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void deleteElement(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public Set<String> getMembers(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.smembers(key);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void addMembers(String key, String... members) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.sadd(key, members);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void removeMembers(String key, String... members) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.srem(key, members);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void saveUserToRedis(String chatId) {

        List<String> redisList = RedisEntity.getInstance().getElements("MUMU_CHATID");

        boolean isContains = false;

        if (redisList == null || redisList.isEmpty()) {
            logger.log(Level.INFO, "Redis list MUMU_CHATID is empty, put first element");
            RedisEntity.getInstance().pushElement("MUMU_CHATID", chatId);
            return;
        }

        for (String elemOfRedis : redisList) {
            if (chatId.equals(elemOfRedis)) {
                isContains = true;
                break;
            }
        }

        if (!isContains) {
            logger.log(Level.INFO, "chatId - {0} does not exist in redis, put it", chatId);
            RedisEntity.getInstance().pushElement("MUMU_CHATID", chatId);
        } else {
            logger.log(Level.INFO, "chatId - {0} already exist in redis, go on...", chatId);
        }
    }
}