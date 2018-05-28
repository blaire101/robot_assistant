package com.saad.dmp_engine.redis.utils;

import com.saad.dmp_engine.redis.config.RedisConfig;
import com.saad.dmp_engine.redis.config.Strategy;
import com.saad.dmp_engine.redis.constant.FLAG;
import com.saad.dmp_engine.redis.exceptions.PoolJedisException;
import com.saad.dmp_engine.redis.exceptions.PrefixKeyException;
import com.saad.dmp_engine.redis.exceptions.RegionException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class JedisStrategy {

  private static final Logger logger = Logger.getLogger(JedisStrategy.class);

  public static final RedisConfig redisConfig = RedisConfig.getInstance();

  public JedisStrategy() {
  }

  public static JedisStrategy createJedisStrategy() {
    return new JedisStrategy();
  }

  protected final String getRedisKey(String prefix, String key) {
    if (StringUtils.isEmpty(prefix)) { // null or ""
      return key;
    }
    StringBuilder redisKey = new StringBuilder();
    redisKey.append(prefix).append(FLAG.PRE_KEY_SEP).append(key);
    return redisKey.toString();
  }
  protected void checkKeyValid(String key) throws PrefixKeyException {
    if (StringUtils.isEmpty(key)) {
      throw new PrefixKeyException("key is invalid !");
    }
  }
  protected void checkKeysValid(final Collection<String> keys) throws PrefixKeyException {
    if (keys == null || keys.isEmpty()) {
      throw new PrefixKeyException("keys is invalid !");
    }
  }

  protected void checkKVMapValid(final Map<String, String> kvMap) throws PrefixKeyException {
    if (kvMap == null || kvMap.isEmpty()) {
      throw new PrefixKeyException("kvMap args is invalid !");
    }
  }
  protected void checkKVMapByteValid(final Map<String, byte[]> kvMap) throws PrefixKeyException {
    if (kvMap == null || kvMap.isEmpty()) {
      throw new PrefixKeyException("kvMap args is invalid !");
    }
  }

  protected void checkKVMapValidAndValueHash(final Map<String, Map<String, String>> kvMap) throws PrefixKeyException {
    if (kvMap == null || kvMap.isEmpty()) {
      throw new PrefixKeyException("kvMap args is invalid !");
    }
  }
  protected void checkKVMapValidAndValueHashByte(final Map<String, Map<byte[], byte[]>> kvMap) throws PrefixKeyException {
    if (kvMap == null || kvMap.isEmpty()) {
      throw new PrefixKeyException("kvMap args is invalid !");
    }
  }

  protected PoolAndJedis getWriteShardedPoolJedis(String region) throws RegionException, PoolJedisException {

    if (StringUtils.isEmpty(region)) {
      throw new RegionException("region invalid");
    }
    String redisName = redisConfig.getRedisName(region);
    if (StringUtils.isEmpty(redisName)) {
      logger.info("redisName : " + redisName + " By " + region);
      throw new RegionException("region invalid");
    }
    Strategy strategy = redisConfig.getStrategy(redisName);
    if (strategy == null) {
      logger.error("getStrategy Failed by redisName  : " + redisName);
      throw new PoolJedisException("Program error");
    }

    String writePoolName = strategy.getWritePoolName();
    if (StringUtils.isEmpty(writePoolName)) {
      logger.error("get writePoolName Failed " + writePoolName);
      throw new PoolJedisException("Program error");
    }
    Map<String, ShardedJedisPool> poolMap = redisConfig.getRedisPoolMap(redisName);
    if (poolMap == null) {
      logger.error("get poolMap Failed By redisName : " + redisName);
      throw new PoolJedisException("Program error");
    }

    ShardedJedisPool writePool = poolMap.get(writePoolName); // writePool must be not null
    ShardedJedis writeJedis = null;

    try {
      writeJedis = writePool.getResource();
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("writePoolJedis: " + "getCurrent PoolJedis Failed for Exception"); // Cluster is down
    }
    if (writeJedis != null) { // 貌似不需要判断，防御型编程
      PoolAndJedis poolAndJedis = new PoolAndJedis(writePool, writeJedis);
      return poolAndJedis;
    }
    throw new PoolJedisException("write cluster is down"); // 防御型编程
  }


  protected PoolAndJedis getReadShardedPoolJedis(String region) throws RegionException, PoolJedisException { // 获取 pool 与其 对应的 jedis
    if (StringUtils.isEmpty(region)) {
      throw new RegionException("region invalid");
    }
    String redisName = redisConfig.getRedisName(region);
    if (StringUtils.isEmpty(redisName)) {
      throw new RegionException("region invalid");
    }
    Map<String, ShardedJedisPool> poolMap = redisConfig.getRedisPoolMap(redisName);
    if (poolMap == null) {
      logger.error("get poolMap Failed By redisName : " + redisName);
      throw new PoolJedisException("Program error");
    }
    Strategy strategy = redisConfig.getStrategy(redisName);
    if (strategy == null) {
      logger.error("getStrategy Failed by redisName  : " + redisName);
      throw new PoolJedisException("Program error");
    }
    String readStrategy = strategy.getReadStrategy();

    List<String> readPools = strategy.getReadPools();
    if (readPools == null || readPools.isEmpty()) {
      logger.error("getStrategy's List<String> readPools Failed");
      throw new PoolJedisException("Program error");
    }

    PoolAndJedis poolAndJedis = getReadShardedPoolJedis(poolMap, readStrategy, readPools);
    if (poolAndJedis == null) {
      throw new PoolJedisException("read cluster is down or configuration error");
    }
    return poolAndJedis;
  }

  private PoolAndJedis getReadShardedPoolJedis(Map<String, ShardedJedisPool> poolMap, String readStrategy, List<String> readPools) {
    if (readStrategy.equals("order")) {
      return getReadShardedPoolJedisByOrderStrategy(poolMap, readPools);
    }
    if (readStrategy.equals("random")) {
      return getReadShardedPoolJedisByRandomStrategy(poolMap, readPools);
    }
    logger.error("readStrategy error!");
    return null;
  }

  private PoolAndJedis getReadShardedPoolJedisByOrderStrategy(Map<String, ShardedJedisPool> poolMap, List<String> readPools) {
    for (String poolName : readPools) {
      ShardedJedisPool readPool = poolMap.get(poolName); // realPool must be not null
      ShardedJedis jedis = null;
      try {
        jedis = readPool.getResource();
      } catch (Exception e) {
        e.printStackTrace();
        logger.error("readPoolJedis: " + "getCurrent PoolJedis Failed for Exception"); // Cluster is down
        continue;
      }
      if (jedis != null) {
        PoolAndJedis poolAndJedis = new PoolAndJedis(readPool, jedis);
        return poolAndJedis;
      }
    }
    return null; // null
  }

  private PoolAndJedis getReadShardedPoolJedisByRandomStrategy(Map<String, ShardedJedisPool> poolMap, List<String> readPools) {
    int targetPos = new Random().nextInt(readPools.size());

    String targetPoolName = readPools.get(targetPos);

    ShardedJedisPool readPool = poolMap.get(targetPoolName); // realPool must be not null
    ShardedJedis jedis = null;
    try {
      jedis = readPool.getResource();
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("readPoolJedis: " + "getCurrent PoolJedis Failed for Exception"); // Cluster is down
    }
    PoolAndJedis poolAndJedis = null;

    if (jedis != null) { // Cluster is well
      poolAndJedis = new PoolAndJedis(readPool, jedis);
      return poolAndJedis;
    }
    logger.error("readPoolJedis : get " + targetPoolName + " Pool's Jedis is Failed.");
    int failPoolNum = 1;

    for (int i = (targetPos + 1) % readPools.size(); true; i++) {
      String poolName = readPools.get(i);
      readPool = poolMap.get(poolName);

      try {
        jedis = readPool.getResource();
      } catch (Exception e) {
        e.printStackTrace();
        logger.error("readPoolJedis: " + "getCurrent PoolJedis Failed for Exception"); // Cluster is down
      }

      if (jedis != null) { // the Cluster is down
        poolAndJedis = new PoolAndJedis(readPool, jedis);
        return poolAndJedis;
      }
      logger.error("readPoolJedis : get " + poolName + " Pool's Jedis is Failed.");
      failPoolNum++;
      if (failPoolNum == readPools.size()) {
        return poolAndJedis; // null, read cluster is down
      }
    }
  }
}
