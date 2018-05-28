package com.saad.dmp_engine.redis.utils;

import com.google.common.collect.Maps;
import com.saad.dmp_engine.redis.config.RedisConfig;
import com.saad.dmp_engine.redis.constant.FLAG;
import com.saad.dmp_engine.redis.exceptions.PoolJedisException;
import com.saad.dmp_engine.redis.exceptions.PrefixKeyException;
import com.saad.dmp_engine.redis.exceptions.RegionException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;
import redis.clients.jedis.ShardedJedisPool;

import java.util.*;

public class JedisUtils extends JedisStrategy implements Jedisable, BinaryJedisable {

  private static final Logger logger = Logger.getLogger(JedisUtils.class);

  private static final JedisUtils instance = new JedisUtils();

  public static final JedisUtils getInstance() {
    return instance;
  }

  /**
   * 在指定的 region 中, 设置 key 及其对应的 String Value 和 时间 ： 不过期， 成功返回 true，失败返回 false
   */
  public Boolean set(String region, String prefix, String key, String value) throws PoolJedisException, PrefixKeyException, RegionException {
    checkKeyValid(key);
    PoolAndJedis poolAndJedis = getWriteShardedPoolJedis(region); // write
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      String redisKey = getRedisKey(prefix, key);
      String resultFlag = sj.set(redisKey, value);
      return resultFlag.equals("OK");
    } finally {
      pool.returnResource(sj);
    }
  }

  /**
   * 在指定的 region 中, 设置 key 及其对应的 String Value 和 在 expireSec 过期， 成功返回 true，失败返回 false
   */
  public Boolean set(String region, String prefix, String key, String value, int expireSec) throws PoolJedisException, PrefixKeyException, RegionException {
    checkKeyValid(key);
    PoolAndJedis poolAndJedis = getWriteShardedPoolJedis(region); // write
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      String redisKey = getRedisKey(prefix, key);
      String resultFlag = sj.setex(redisKey, expireSec, value);
      return resultFlag.equals("OK");
    } finally {
      pool.returnResource(sj);
    }
  }


  /**
   * 在指定的 region 中, 取出 key 对应的 String Value
   */
  public String get(String region, String prefix, String key) throws PoolJedisException, PrefixKeyException, RegionException {
    checkKeyValid(key);
    PoolAndJedis poolAndJedis = getReadShardedPoolJedis(region); // read
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      String redisKey = getRedisKey(prefix, key);
      String result = sj.get(redisKey);
      return result;
    } finally {
      pool.returnResource(sj);
    }
  }

  /**
   * 在指定的 region 中, 取出 key 对应的 byte Value
   */
  public byte[] getByte(String region, String prefix, String key) throws RegionException, PoolJedisException, PrefixKeyException {
    checkKeyValid(key);
    PoolAndJedis poolAndJedis = getReadShardedPoolJedis(region); // read
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      String redisKey = getRedisKey(prefix, key);
      byte[] result = sj.get(redisKey.getBytes());
      return result;
    } finally {
      pool.returnResource(sj);
    }
  }

  /**
   * 在指定的 region 中, 设置 key 及其对应的 hash Value, 并不过期， 成功返回 true
   */
  public Boolean hmset(String region, String prefix, String key, Map<String, String> hash) throws PrefixKeyException, PoolJedisException, RegionException {
    checkKeyValid(key);
    PoolAndJedis poolAndJedis = getWriteShardedPoolJedis(region); // write
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      String redisKey = getRedisKey(prefix, key);
      String resultFlag = sj.hmset(redisKey, hash); // 成功返回 OK
      Boolean flag = (resultFlag.equals("OK"));
      return flag;
    } finally {
      pool.returnResource(sj);
    }
  }

  /**
   * 在指定的 region 中, 设置 key 及其对应的 hash Value， 成功返回 true，失败返回 false， 如果 map 为 empty，会爆 JedisDataException 异常
   */
  public Boolean hmset(String region, String prefix, String key, Map<String, String> hash, int expireSec) throws PrefixKeyException, PoolJedisException, RegionException {
    checkKeyValid(key);
    PoolAndJedis poolAndJedis = getWriteShardedPoolJedis(region); // write
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      String redisKey = getRedisKey(prefix, key);
      String resultFlag = sj.hmset(redisKey, hash); // 成功返回 OK
      Long resultExpire = sj.expire(redisKey, expireSec); // 成功返回 1
      Boolean flag = (resultFlag.equals("OK")) && (resultExpire.intValue() == FLAG.EXPIRE_SUCCESS);
      return flag;
    } finally {
      pool.returnResource(sj);
    }
  }


  /**
   * 在指定的 region 中, 取得 key 及其对应的 hash Value， 返回 Map
   */
  public Map<String, String> hgetAll(String region, String prefix, String key) throws PoolJedisException, PrefixKeyException, RegionException {
    checkKeyValid(key);
    PoolAndJedis poolAndJedis = getReadShardedPoolJedis(region); // write
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      String redisKey = getRedisKey(prefix, key);
      Map<String, String> resultMap = sj.hgetAll(redisKey);
      return resultMap;
    } finally {
      pool.returnResource(sj);
    }
  }

  /**
   * 在指定的 region 中, 取得 key 及其对应的 hash Value， 返回 Map
   */
  public Map<byte[], byte[]> hgetAllByte(String region, String prefix, String key) throws PoolJedisException, PrefixKeyException, RegionException {
    checkKeyValid(key);
    PoolAndJedis poolAndJedis = getReadShardedPoolJedis(region); // write
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      String redisKey = getRedisKey(prefix, key);
      Map<byte[], byte[]> resultMap = sj.hgetAll(redisKey.getBytes());
      return resultMap;
    } finally {
      pool.returnResource(sj);
    }
  }

  /**
   * 在指定的 region 中, 设置 key 及其对应的 hash Value 的 field 的 value， 如果 field 已经存在，返回 0，否则成功设置 返回 1.
   * <p/>
   * 注意此方法，如果key不存在，则field插入后，并没有设置expire, 如果key存在，则field插入，按照key的expire走
   */

  public Long hset(String region, String prefix, String key, String field, String value) throws PrefixKeyException, PoolJedisException, RegionException {
    checkKeyValid(key);
    PoolAndJedis poolAndJedis = getWriteShardedPoolJedis(region); // write
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      String redisKey = getRedisKey(prefix, key);
      Long result = sj.hset(redisKey, field, value);
      return result;
    } finally {
      pool.returnResource(sj);
    }
  }

  /**
   * 在指定的 region 中, 设置 key 及其对应的 hash Value 的 field 的 value， 如果 field 已经存在，返回 0，否则成功设置 返回 1.
   * <p/>
   * 设置过期时间
   */
  public Long hset(String region, String prefix, String key, String field, String value, int expireSec) throws PrefixKeyException, PoolJedisException, RegionException {
    checkKeyValid(key);
    PoolAndJedis poolAndJedis = getWriteShardedPoolJedis(region); // write
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      String redisKey = getRedisKey(prefix, key);
      Long result = sj.hset(redisKey, field, value);
      sj.expire(redisKey, expireSec);
      return result;
    } finally {
      pool.returnResource(sj);
    }
  }


  /**
   * 在指定的 region 中, 设置 key 的过期秒数, 设置成功返回 1,  如果 key 不存在，则设置失败，此时返回 0, 注意返回类型是 Long
   */

  public Long expire(String region, String prefix, String key, int seconds) throws PrefixKeyException, PoolJedisException, RegionException {
    checkKeyValid(key);
    PoolAndJedis poolAndJedis = getWriteShardedPoolJedis(region); // write
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      String redisKey = getRedisKey(prefix, key);
      Long result = sj.expire(redisKey, seconds);
      return result;
    } finally {
      pool.returnResource(sj);
    }
  }

  /**
   * 在指定的 region 中, 取得 key 的余下的生存时间，单位是 秒， 注意返回类型是 Long.(returns -1 if the key exists but has no associated expire.)
   */
  public Long ttl(String region, String prefix, String key) throws PrefixKeyException, PoolJedisException, RegionException {
    checkKeyValid(key);
    PoolAndJedis poolAndJedis = getWriteShardedPoolJedis(region); // write
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      String redisKey = getRedisKey(prefix, key);
      Long result = sj.ttl(redisKey);
      return result;
    } finally {
      pool.returnResource(sj);
    }
  }

  /**
   * 在指定的 region 中, 取得 key 及其对应的 hash Value 的 field 的 value, 查询不到（key或者field不存在） return null
   */
  public String hget(String region, String prefix, String key, String field) throws PrefixKeyException, PoolJedisException, RegionException {
    checkKeyValid(key);
    PoolAndJedis poolAndJedis = getReadShardedPoolJedis(region); // read
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      String redisKey = getRedisKey(prefix, key);
      String result = sj.hget(redisKey, field);
      return result;
    } finally {
      pool.returnResource(sj);
    }
  }

  /**
   * 在指定的 region 中, 取得 key 及其对应的 hash Value 的 field 的 value, 查询不到（key或者field不存在） return null
   */
  public byte[] hget(String region, String prefix, String key, byte[] field) throws PrefixKeyException, PoolJedisException, RegionException {
    checkKeyValid(key);
    PoolAndJedis poolAndJedis = getReadShardedPoolJedis(region); // read
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      String redisKey = getRedisKey(prefix, key);
      byte[] result = sj.hget(redisKey.getBytes(), field);
      return result;
    } finally {
      pool.returnResource(sj);
    }
  }

  /**
   * 在指定的 region 中, 删除 key 及其对应的 hash Value 的 fields， 返回成功删除的记录数
   */
  public Long hdel(String region, String prefix, String key, String... fields) throws PrefixKeyException, PoolJedisException, RegionException {
    checkKeyValid(key);
    PoolAndJedis poolAndJedis = getWriteShardedPoolJedis(region); // write
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      String redisKey = getRedisKey(prefix, key);
      Long result = sj.hdel(redisKey, fields);
      return result;
    } finally {
      pool.returnResource(sj);
    }
  }

  /**
   * 在指定的 region 中, 删除 key 及其对应的 Value, 返回成功删除的记录数
   */
  public Long del(String region, String prefix, String key) throws PrefixKeyException, PoolJedisException, RegionException {
    checkKeyValid(key);
    PoolAndJedis poolAndJedis = getWriteShardedPoolJedis(region); // write
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      String redisKey = getRedisKey(prefix, key);
      Long result = sj.del(redisKey);
      return result;
    } finally {
      pool.returnResource(sj);
    }
  }

  /**
   * 在指定的 region 中, 批量删除 key 及其对应的 Value, 返回成功删除的记录数
   */
  public Long delMulti(String region, String prefix, Collection<String> keys) throws PrefixKeyException, PoolJedisException, RegionException {
    checkKeysValid(keys);
    PoolAndJedis poolAndJedis = getWriteShardedPoolJedis(region); // read
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      ShardedJedisPipeline pip = sj.pipelined();

      for (String key : keys) {
        String redisKey = getRedisKey(prefix, key);
        pip.del(redisKey);
      }
      List<Object> valueList = pip.syncAndReturnAll();

      if (keys.size() != valueList.size()) {
        throw new PoolJedisException("Jedis pipeline error!");
      }
      long res = 0;
      int pos = -1;
      for (String key : keys) {
        pos++;
        if (valueList.get(pos) == null) {
          continue; // key no exists，not put resultMap
        }
        res += ((Long)(valueList.get(pos))).longValue();
      }
      return new Long(res);
    } finally {
      pool.returnResource(sj);
    }
  }
  /**
   * 在指定的 region 中, 判断 key 是否存在, 存在返回 true，返回 false 为不存在
   */

  public Boolean exists(String region, String prefix, String key) throws PoolJedisException, PrefixKeyException, RegionException {
    checkKeyValid(key);
    PoolAndJedis poolAndJedis = getReadShardedPoolJedis(region); // read
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      String redisKey = getRedisKey(prefix, key);
      Boolean result = sj.exists(redisKey);
      return result;
    } finally {
      pool.returnResource(sj);
    }
  }

  /**
   * 在指定的 region 中, 取得 key 对应的 hash 的 field 的个数，返回值类型 Long
   */

  public Long hlen(String region, String prefix, String key) throws PrefixKeyException, PoolJedisException, RegionException {
    checkKeyValid(key);
    PoolAndJedis poolAndJedis = getReadShardedPoolJedis(region); // read
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      String redisKey = getRedisKey(prefix, key);
      Long result = sj.hlen(redisKey);
      return result;
    } finally {
      pool.returnResource(sj);
    }
  }

  /**
   * 在指定的 region 中, 判断 key 对应的 hash 的 field 是否存在, 返true为存在，返回 false 为不存在
   */

  public Boolean hexists(String region, String prefix, String key, String field) throws PrefixKeyException, PoolJedisException, RegionException {
    checkKeyValid(key);
    PoolAndJedis poolAndJedis = getReadShardedPoolJedis(region); // read
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      String redisKey = getRedisKey(prefix, key);
      Boolean result = sj.hexists(redisKey, field);
      return result;
    } finally {
      pool.returnResource(sj);
    }
  }

  /**
   * 在指定的 region 中, 取得 key 及其对应的 hash Value 的 各个 field 的 value ，返回 Map<String, String> field, value . // ying
   */
  public Map<String, String> hmget(String region, String prefix, String key, String... fields) throws PrefixKeyException, PoolJedisException, RegionException {
    checkKeyValid(key);
    PoolAndJedis poolAndJedis = getReadShardedPoolJedis(region); // read
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      String redisKey = getRedisKey(prefix, key);

      List<String> fieldValueList = sj.hmget(redisKey, fields); //
      if (fields.length != fieldValueList.size()) {
        throw new PoolJedisException("Sharded Jedis error!");
      }

      Map<String, String> resultMap = Maps.newHashMap(); // field <-> value

      int len = fieldValueList.size();
      for (int i = 0; i < len; i++) {
        if (fieldValueList.get(i) == null) continue; // field not exists，not put resultMap
        resultMap.put(fields[i], fieldValueList.get(i));
      }
      return resultMap;
    } finally {
      pool.returnResource(sj);
    }
  }

  /**
   * 在指定的 region 中, 批量取出 key 对应的 Value (value 为 String 类型)， 返回 Map
   */
  public Map<String, String> getMulti(String region, String prefix, final Collection<String> keys) throws
          PrefixKeyException, RegionException, PoolJedisException {
    checkKeysValid(keys);
    PoolAndJedis poolAndJedis = getReadShardedPoolJedis(region); // read
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      ShardedJedisPipeline pip = sj.pipelined();

      for (String key : keys) {
        String redisKey = getRedisKey(prefix, key);
        pip.get(redisKey);
      }
      List<Object> valueList = pip.syncAndReturnAll();
      Map<String, String> resultMap = Maps.newHashMap();

      if (keys.size() != valueList.size()) {
        throw new PoolJedisException("Jedis pipeline error!"); // ying
      }
      int pos = -1;
      for (String key : keys) {
        pos++;
        if (valueList.get(pos) == null) {
          continue; // key no exists，not put resultMap
        }
        resultMap.put(key, (String) (valueList.get(pos)));
      }
      return resultMap;
    } finally {
      pool.returnResource(sj);
    }
  }

  /**
   * 在指定的 region 中, 批量取出 key 对应的 Value (value 为 map 类型),  返回 Map
   */
  @SuppressWarnings("unchecked")
  public Map<String, Map<String, String>> hgetMulti(String region, String prefix, Collection<String> keys) throws
          PrefixKeyException, RegionException, PoolJedisException {
    checkKeysValid(keys);
    PoolAndJedis poolAndJedis = getReadShardedPoolJedis(region); // read
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      ShardedJedisPipeline pip = sj.pipelined();

      for (String key : keys) {
        String redisKey = getRedisKey(prefix, key);
        pip.hgetAll(redisKey);
      }

      List<Object> valueList = pip.syncAndReturnAll();

      Map<String, Map<String, String>> resultMap = Maps.newHashMap();

      if (keys.size() != valueList.size()) {
        throw new PoolJedisException("Jedis pipeline error!");
      }
      int pos = -1;

      for (String key : keys) {
        pos++;
        if (((Map<String, String>) (valueList.get(pos))).isEmpty()) {
          continue; // key no exists，not put resultMap
        }
        resultMap.put(key, (Map<String, String>) (valueList.get(pos)));
      }
      return resultMap;
    } finally {
      pool.returnResource(sj);
    }
  }
  @SuppressWarnings("unchecked")
  public Map<String, Map<byte[], byte[]>> hgetMultiByte(String region, String prefix, Collection<String> keys) throws
      PrefixKeyException, RegionException, PoolJedisException {
    checkKeysValid(keys);
    PoolAndJedis poolAndJedis = getReadShardedPoolJedis(region); // read
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      ShardedJedisPipeline pip = sj.pipelined();

      for (String key : keys) {
        String redisKey = getRedisKey(prefix, key);
        pip.hgetAll(redisKey.getBytes());
      }

      List<Object> valueList = pip.syncAndReturnAll();

      Map<String, Map<byte[], byte[]>> resultMap = Maps.newHashMap();

      if (keys.size() != valueList.size()) {
        throw new PoolJedisException("Jedis pipeline error!");
      }
      int pos = -1;

      for (String key : keys) {
        pos++;
        if (((Map<byte[], byte[]>) (valueList.get(pos))).isEmpty()) {
          continue; // key no exists，not put resultMap
        }
        resultMap.put(key, (Map<byte[], byte[]>) (valueList.get(pos)));
      }
      return resultMap;
    } finally {
      pool.returnResource(sj);
    }
  }

  /**
   * 在指定的 region 中, 批量设置 key 对应的 Value， 返回成功 set 进入的个数, 不过期
   */

  public Long setMultiString(String region, String prefix, final Map<String, String> kvMap) throws PrefixKeyException, RegionException, PoolJedisException {
    checkKVMapValid(kvMap);
    PoolAndJedis poolAndJedis = getWriteShardedPoolJedis(region); // write
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      ShardedJedisPipeline pip = sj.pipelined();

      Iterator<Map.Entry<String, String>> it = kvMap.entrySet().iterator();

      int invalidKeyCount = 0;

      while (it.hasNext()) {
        Map.Entry<String, String> entry = it.next();
        if (StringUtils.isEmpty(entry.getKey())) {
          invalidKeyCount++;
          continue; // 合法性检查  // validKey
        }
        String redisKey = getRedisKey(prefix, entry.getKey());
        pip.set(redisKey, entry.getValue());
      }
      List<Object> returnList = pip.syncAndReturnAll();

      if (kvMap.size() - invalidKeyCount == returnList.size()) {
        return new Long(returnList.size());
      }
      int successNum = 0;
      for (Object o : returnList) {
        if (o.equals("OK")) { // 父类引用指向子类对象？
          successNum++;
        }
      }
      return new Long(successNum);
    } finally {
      pool.returnResource(sj);
    }
  }

  /**
   * 在指定的 region 中, 批量设置 key 对应的 Value， 返回成功 set 进入的个数, expireSec 秒后 过期
   */
  public Long setMultiString(String region, String prefix, final Map<String, String> kvMap, int expireSec) throws PrefixKeyException, RegionException, PoolJedisException {
    checkKVMapValid(kvMap);
    PoolAndJedis poolAndJedis = getWriteShardedPoolJedis(region); // write
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      ShardedJedisPipeline pip = sj.pipelined();

      Iterator<Map.Entry<String, String>> it = kvMap.entrySet().iterator();

      int invalidKeyCount = 0;

      while (it.hasNext()) {
        Map.Entry<String, String> entry = it.next();
        if (StringUtils.isEmpty(entry.getKey())) {
          invalidKeyCount++;
          continue; // 合法性检查  // validKey
        }
        String redisKey = getRedisKey(prefix, entry.getKey());
        pip.setex(redisKey, expireSec, entry.getValue());
      }
      List<Object> returnList = pip.syncAndReturnAll();

      if (kvMap.size() - invalidKeyCount == returnList.size()) {
        return new Long(returnList.size());
      }
      int successNum = 0;
      for (Object o : returnList) {
        if (o.equals("OK")) { // 父类引用指向子类对象？
          successNum++;
        }
      }
      return new Long(successNum);
    } finally {
      pool.returnResource(sj);
    }
  }


  /**
   * 在指定的 region 中, 批量设置 key 对应的 Value (value 为 Hash 类型)，  如果之前有key存在，则覆盖之前的值， 返回成功 set 进入的个数, 不过期
   */
  public Long setMultiHash(String region, String prefix, final Map<String, Map<String, String>> map) throws PrefixKeyException, RegionException, PoolJedisException {
    checkKVMapValidAndValueHash(map);
    PoolAndJedis poolAndJedis = getWriteShardedPoolJedis(region); // write
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      ShardedJedisPipeline pip = sj.pipelined();

      int invalidKeyCount = 0;

      Iterator<Map.Entry<String, Map<String, String>>> it = map.entrySet().iterator(); // validKeyMap
      while (it.hasNext()) {
        Map.Entry<String, Map<String, String>> entry = it.next();
        if (StringUtils.isEmpty(entry.getKey())) {
          invalidKeyCount++;
          continue;
        }
        String redisKey = getRedisKey(prefix, entry.getKey());
        pip.del(redisKey);
        pip.hmset(redisKey, entry.getValue());
      }
      List<Object> returnList = pip.syncAndReturnAll();

      int pipNumPer = 2;
      int pipSize = (map.size() - invalidKeyCount) * pipNumPer;
      if (pipSize == returnList.size()) { //
        return new Long(map.size() - invalidKeyCount);
      }
      int successNum = 0;
      for (int i = 0; i + 1 < returnList.size(); ) {
        Object del_res = returnList.get(i);
        Object hms_res = returnList.get(i + 1);
        if ((((Long) (del_res)).intValue() == 0 || ((Long) (del_res)).intValue() == 1)
                && (hms_res).equals("OK")) {
          successNum++;
        }
        i += pipNumPer;
      }
      return new Long(successNum);
    } finally {
      pool.returnResource(sj);
    }
  }


  /**
   * 在指定的 region 中, 批量设置 key 对应的 Value (value 为 Hash 类型)， 如果之前有key存在，则覆盖之前的值，返回成功 set 进入的个数, expireSec 秒后 过期
   */
  public Long setMultiHash(String region, String prefix, final Map<String, Map<String, String>> map, int expireSec) throws PrefixKeyException, RegionException, PoolJedisException {
    checkKVMapValidAndValueHash(map);
    PoolAndJedis poolAndJedis = getWriteShardedPoolJedis(region); // write
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      ShardedJedisPipeline pip = sj.pipelined();

      int invalidKeyCount = 0;

      Iterator<Map.Entry<String, Map<String, String>>> it = map.entrySet().iterator(); // validKeyMap
      while (it.hasNext()) {
        Map.Entry<String, Map<String, String>> entry = it.next();
        if (StringUtils.isEmpty(entry.getKey())) {
          invalidKeyCount++;
          continue;
        }
        String redisKey = getRedisKey(prefix, entry.getKey());
        pip.del(redisKey);
        pip.hmset(redisKey, entry.getValue());
        pip.expire(redisKey, expireSec);
      }
      List<Object> returnList = pip.syncAndReturnAll();

      int pipNumPer = 3;
      int pipSize = (map.size() - invalidKeyCount) * pipNumPer;
      if (pipSize == returnList.size()) { //
        return new Long(map.size() - invalidKeyCount);
      }
      int successNum = 0;
      for (int i = 0; i + 2 < returnList.size(); ) {
        Object del_res = returnList.get(i);
        Object hms_res = returnList.get(i + 1);
        Object exp_res = returnList.get(i + 2);

        if ((((Long) (del_res)).intValue() == 0 || ((Long) (del_res)).intValue() == 1)
                && (hms_res).equals("OK") && ((Long) exp_res).intValue() == FLAG.EXPIRE_SUCCESS) {
          successNum++;
        }
        i += pipNumPer;
      }
      return new Long(successNum);
    } finally {
      pool.returnResource(sj);
    }
  }

  /**
   * 在指定的 region 中, 批量更新 key 对应的 Value (value 为 Hash 类型, 内为 String)，
   * 如果之前有key存在，则不覆盖之前的值， 只是增加field，返回成功 update 进入的个数。
   * 如果之前的key不存在，则新插入的记录不过期。如果之前的key存在，则新插入的key也还是按照之前的key的过期时间走
   */
  public Long updateMultiHash(String region, String prefix, final Map<String, Map<String, String>> map) throws PrefixKeyException, RegionException, PoolJedisException {
    checkKVMapValidAndValueHash(map);
    PoolAndJedis poolAndJedis = getWriteShardedPoolJedis(region); // write
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      ShardedJedisPipeline pip = sj.pipelined();

      int invalidKeyCount = 0;

      Iterator<Map.Entry<String, Map<String, String>>> it = map.entrySet().iterator(); // validKeyMap
      while (it.hasNext()) {
        Map.Entry<String, Map<String, String>> entry = it.next();
        if (StringUtils.isEmpty(entry.getKey())) {
          invalidKeyCount++;
          continue;
        }
        String redisKey = getRedisKey(prefix, entry.getKey());
        pip.hmset(redisKey, entry.getValue()); // core
      }
      List<Object> returnList = pip.syncAndReturnAll();

      int pipSize = (map.size() - invalidKeyCount);
      if (pipSize == returnList.size()) {
        return new Long(pipSize);
      }
      int successNum = 0;
      for (int i = 0; i < returnList.size(); i++) {
        Object hms_res = returnList.get(i);
        if ((hms_res).equals("OK")) {
          successNum++;
        }
      }
      return new Long(successNum);
    } finally {
      pool.returnResource(sj);
    }
  }


  /**
   * 在指定的 region 中, 批量更新 key 对应的 Value (value 为 Hash 类型)， 如果之前有key存在，则不覆盖之前的值， 只是增加field，返回成功 update 进入的个数, expireSec 秒后 过期
   */
  public Long updateMultiHash(String region, String prefix, final Map<String, Map<String, String>> map, int expireSec) throws PrefixKeyException, RegionException, PoolJedisException {
    checkKVMapValidAndValueHash(map);
    PoolAndJedis poolAndJedis = getWriteShardedPoolJedis(region); // write
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      ShardedJedisPipeline pip = sj.pipelined();

      int invalidKeyCount = 0;

      Iterator<Map.Entry<String, Map<String, String>>> it = map.entrySet().iterator(); // validKeyMap
      while (it.hasNext()) {
        Map.Entry<String, Map<String, String>> entry = it.next();
        if (StringUtils.isEmpty(entry.getKey())) {
          invalidKeyCount++;
          continue;
        }
        String redisKey = getRedisKey(prefix, entry.getKey());
        pip.hmset(redisKey, entry.getValue()); // core
        pip.expire(redisKey, expireSec);
      }
      List<Object> returnList = pip.syncAndReturnAll();

      int pipNumPer = 2;
      int pipSize = (map.size() - invalidKeyCount) * pipNumPer;
      if (pipSize == returnList.size()) {
        return new Long(map.size() - invalidKeyCount);
      }
      int successNum = 0;
      for (int i = 0; i + 1 < returnList.size(); ) {
        Object hms_res = returnList.get(i);
        Object exp_res = returnList.get(i + 1);
        if ((hms_res).equals("OK") && ((Long) exp_res).intValue() == FLAG.EXPIRE_SUCCESS) {
          successNum++;
        }
        i += pipNumPer;
      }
      return new Long(successNum);
    } finally {
      pool.returnResource(sj);
    }
  }


  /**
   * 在指定的 region 中, 设置 key 及其对应的 byte[] Value 和  不过期， 成功返回 true，失败返回 false
   */
  public Boolean set(String region, String prefix, String key, byte[] value) throws PoolJedisException, PrefixKeyException, RegionException {
    checkKeyValid(key);
    PoolAndJedis poolAndJedis = getWriteShardedPoolJedis(region); // write
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      String redisKey = getRedisKey(prefix, key);
      String resultFlag = sj.set(redisKey.getBytes(), value);
      return resultFlag.equals("OK");
    } finally {
      pool.returnResource(sj);
    }
  }

  /**
   * 在指定的 region 中, 设置 key 及其对应的 bytes[] Value 和 在 expireSec 过期， 成功返回 true，失败返回 false
   */
  public Boolean set(String region, String prefix, String key, byte[] value, int expireSec) throws PoolJedisException, PrefixKeyException, RegionException {
    checkKeyValid(key);
    PoolAndJedis poolAndJedis = getWriteShardedPoolJedis(region); // write
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      String redisKey = getRedisKey(prefix, key);
      String resultFlag = sj.setex(redisKey.getBytes(), expireSec, value);
      return resultFlag.equals("OK");
    } finally {
      pool.returnResource(sj);
    }
  }

  /**
   * 在指定的 region 中, 设置 key 及其对应的 hash Value, 并 不过期， 成功返回 true
   */
  public Boolean hmsetByte(String region, String prefix, String key, Map<byte[], byte[]> hash) throws PrefixKeyException, PoolJedisException, RegionException {
    checkKeyValid(key);
    PoolAndJedis poolAndJedis = getWriteShardedPoolJedis(region); // write
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      String redisKey = getRedisKey(prefix, key);
      String resultFlag = sj.hmset(redisKey.getBytes(), hash); // 成功返回 OK
      Boolean flag = (resultFlag.equals("OK"));
      return flag;
    } finally {
      pool.returnResource(sj);
    }
  }
//

  /**
   * 在指定的 region 中, 设置 key 及其对应的 hash Value， 成功返回 true，失败返回 false， 如果 map 为 empty，会爆 JedisDataException 异常
   */
  public Boolean hmsetByte(String region, String prefix, String key, Map<byte[], byte[]> hash, int expireSec) throws PrefixKeyException, PoolJedisException, RegionException {
    checkKeyValid(key);
    PoolAndJedis poolAndJedis = getWriteShardedPoolJedis(region); // write
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      String redisKey = getRedisKey(prefix, key);
      String resultFlag = sj.hmset(redisKey.getBytes(), hash); // 成功返回 OK
      Long resultExpire = sj.expire(redisKey.getBytes(), expireSec); // 成功返回 1
      Boolean flag = (resultFlag.equals("OK")) && (resultExpire.intValue() == FLAG.EXPIRE_SUCCESS);
      return flag;
    } finally {
      pool.returnResource(sj);
    }
  }

  /**
   * 在指定的 region 中, 设置 key 及其对应的 hash Value 的 field 的 value， 如果 field 已经存在，返回 0，否则成功设置 返回 1.
   * <p/>
   * 注意此方法，如果key不存在，则field插入后，并没有设置expire, 如果key存在，则field插入，按照key的expire走
   */
  public Long hset(String region, String prefix, String key, byte[] field, byte[] value) throws PrefixKeyException, PoolJedisException, RegionException {
    checkKeyValid(key);
    PoolAndJedis poolAndJedis = getWriteShardedPoolJedis(region); // write
    ShardedJedisPool pool = null;

    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      String redisKey = getRedisKey(prefix, key);
      Long result = sj.hset(redisKey.getBytes(), field, value);
      return result;
    } finally {
      pool.returnResource(sj);
    }
  }

  /**
   * 在指定的 region 中, 批量设置 key 对应的 Value 为 byte[]， 返回成功 set 进入的个数, 不过期
   */

  public Long setMultiStringByte(String region, String prefix, final Map<String, byte[]> kvMap) throws PrefixKeyException, RegionException, PoolJedisException {
    checkKVMapByteValid(kvMap);
    PoolAndJedis poolAndJedis = getWriteShardedPoolJedis(region); // write
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      ShardedJedisPipeline pip = sj.pipelined();

      Iterator<Map.Entry<String, byte[]>> it = kvMap.entrySet().iterator();

      int invalidKeyCount = 0;

      while (it.hasNext()) {
        Map.Entry<String, byte[]> entry = it.next();
        if (StringUtils.isEmpty(entry.getKey())) {
          invalidKeyCount++;
          continue; // 合法性检查  // validKey
        }
        String redisKey = getRedisKey(prefix, entry.getKey());
        pip.set(redisKey.getBytes(), entry.getValue());
      }
      List<Object> returnList = pip.syncAndReturnAll();

      if (kvMap.size() - invalidKeyCount == returnList.size()) {
        return new Long(returnList.size());
      }
      int successNum = 0;
      for (Object o : returnList) {
        if (o.equals("OK")) { // 父类引用指向子类对象？
          successNum++;
        }
      }
      return new Long(successNum);
    } finally {
      pool.returnResource(sj);
    }
  }

  /**
   * 在指定的 region 中, 批量设置 key 对应的 Value (value 为 byte[] )， 返回成功 set 进入的个数, expireSec 秒后 过期
   */
  public Long setMultiStringByte(String region, String prefix, final Map<String, byte[]> kvMap, int expireSec) throws PrefixKeyException, RegionException, PoolJedisException {
    checkKVMapByteValid(kvMap);
    PoolAndJedis poolAndJedis = getWriteShardedPoolJedis(region); // write
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      ShardedJedisPipeline pip = sj.pipelined();

      Iterator<Map.Entry<String, byte[]>> it = kvMap.entrySet().iterator();

      int invalidKeyCount = 0;

      while (it.hasNext()) {
        Map.Entry<String, byte[]> entry = it.next();
        if (StringUtils.isEmpty(entry.getKey())) {
          invalidKeyCount++;
          continue; // 合法性检查  // validKey
        }
        String redisKey = getRedisKey(prefix, entry.getKey());
        pip.setex(redisKey.getBytes(), expireSec, entry.getValue());
      }
      List<Object> returnList = pip.syncAndReturnAll();

      if (kvMap.size() - invalidKeyCount == returnList.size()) {
        return new Long(returnList.size());
      }
      int successNum = 0;
      for (Object o : returnList) {
        if (o.equals("OK")) { // 父类引用指向子类对象？
          successNum++;
        }
      }
      return new Long(successNum);
    } finally {
      pool.returnResource(sj);
    }
  }

  /**
   * 在指定的 region 中, 批量设置 key 对应的 Value (value 为 Hash 类型, Hash 中为 byte[] )，
   * 如果之前有key存在，则覆盖之前的值， 返回成功 set 进入的个数, 不过期
   */
  public Long setMultiHashByte(String region, String prefix, final Map<String, Map<byte[], byte[]>> map) throws PrefixKeyException, RegionException, PoolJedisException {
    checkKVMapValidAndValueHashByte(map);
    PoolAndJedis poolAndJedis = getWriteShardedPoolJedis(region); // write
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      ShardedJedisPipeline pip = sj.pipelined();

      int invalidKeyCount = 0;

      Iterator<Map.Entry<String, Map<byte[], byte[]>>> it = map.entrySet().iterator(); // validKeyMap
      while (it.hasNext()) {
        Map.Entry<String, Map<byte[], byte[]>> entry = it.next();
        if (StringUtils.isEmpty(entry.getKey())) {
          invalidKeyCount++;
          continue;
        }
        String redisKey = getRedisKey(prefix, entry.getKey());
        pip.del(redisKey);
        pip.hmset(redisKey.getBytes(), entry.getValue());
      }
      List<Object> returnList = pip.syncAndReturnAll();

      int pipNumPer = 2;
      int pipSize = (map.size() - invalidKeyCount) * pipNumPer;
      if (pipSize == returnList.size()) { //
        return new Long(map.size() - invalidKeyCount);
      }
      int successNum = 0;
      for (int i = 0; i + 1 < returnList.size(); ) {
        Object del_res = returnList.get(i);
        Object hms_res = returnList.get(i + 1);
        if ((((Long) (del_res)).intValue() == 0 || ((Long) (del_res)).intValue() == 1)
                && (hms_res).equals("OK")) {
          successNum++;
        }
        i += pipNumPer;
      }
      return new Long(successNum);
    } finally {
      pool.returnResource(sj);
    }
  }

  /**
   * 在指定的 region 中, 批量设置 key 对应的 Value (value 为 Hash 类型, Hash中为 bytes[])， 如果之前有key存在，则覆盖之前的值，返回成功 set 进入的个数, expireSec 秒后 过期
   */
  public Long setMultiHashByte(String region, String prefix, final Map<String, Map<byte[], byte[]>> map, int expireSec) throws PrefixKeyException, RegionException, PoolJedisException {
    checkKVMapValidAndValueHashByte(map);
    PoolAndJedis poolAndJedis = getWriteShardedPoolJedis(region); // write
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      ShardedJedisPipeline pip = sj.pipelined();

      int invalidKeyCount = 0;

      Iterator<Map.Entry<String, Map<byte[], byte[]>>> it = map.entrySet().iterator(); // validKeyMap
      while (it.hasNext()) {
        Map.Entry<String, Map<byte[], byte[]>> entry = it.next();
        if (StringUtils.isEmpty(entry.getKey())) {
          invalidKeyCount++;
          continue;
        }
        String redisKey = getRedisKey(prefix, entry.getKey());
        pip.del(redisKey);
        pip.hmset(redisKey.getBytes(), entry.getValue());
        pip.expire(redisKey, expireSec);
      }
      List<Object> returnList = pip.syncAndReturnAll();

      int pipNumPer = 3;
      int pipSize = (map.size() - invalidKeyCount) * pipNumPer;
      if (pipSize == returnList.size()) { //
        return new Long(map.size() - invalidKeyCount);
      }
      int successNum = 0;
      for (int i = 0; i + 2 < returnList.size(); ) {
        Object del_res = returnList.get(i);
        Object hms_res = returnList.get(i + 1);
        Object exp_res = returnList.get(i + 2);

        if ((((Long) (del_res)).intValue() == 0 || ((Long) (del_res)).intValue() == 1)
                && (hms_res).equals("OK") && ((Long) exp_res).intValue() == FLAG.EXPIRE_SUCCESS) {
          successNum++;
        }
        i += pipNumPer;
      }
      return new Long(successNum);
    } finally {
      pool.returnResource(sj);
    }
  }

  /**
   * 在指定的 region 中, 批量更新 key 对应的 Value (value 为 Hash 类型, 内为 byte[])，
   * 如果之前有key存在，则不覆盖之前的值， 只是增加field，返回成功 update 进入的个数。
   * 如果之前的key不存在，则新插入的记录不过期。如果之前的key存在，则新插入的key也还是按照之前的key的过期时间走
   */
  public Long updateMultiHashByte(String region, String prefix, final Map<String, Map<byte[], byte[]>> map) throws PrefixKeyException, RegionException, PoolJedisException {
    checkKVMapValidAndValueHashByte(map);
    PoolAndJedis poolAndJedis = getWriteShardedPoolJedis(region); // write
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      ShardedJedisPipeline pip = sj.pipelined();

      int invalidKeyCount = 0;

      Iterator<Map.Entry<String, Map<byte[], byte[]>>> it = map.entrySet().iterator(); // validKeyMap
      while (it.hasNext()) {
        Map.Entry<String, Map<byte[], byte[]>> entry = it.next();
        if (StringUtils.isEmpty(entry.getKey())) {
          invalidKeyCount++;
          continue;
        }
        String redisKey = getRedisKey(prefix, entry.getKey());
        pip.hmset(redisKey.getBytes(), entry.getValue()); // core
      }
      List<Object> returnList = pip.syncAndReturnAll();

      int pipSize = (map.size() - invalidKeyCount);
      if (pipSize == returnList.size()) {
        return new Long(pipSize);
      }
      int successNum = 0;
      for (int i = 0; i < returnList.size(); i++) {
        Object hms_res = returnList.get(i);
        if ((hms_res).equals("OK")) {
          successNum++;
        }
      }
      return new Long(successNum);
    } finally {
      pool.returnResource(sj);
    }
  }

  /**
   * 在指定的 region 中, 批量更新 key 对应的 Value (value 为 Hash 类型, Hash 内为 byte[])， 如果之前有key存在，则不覆盖之前的值， 只是增加field，返回成功 update 进入的个数, expireSec 秒后 过期
   */
  public Long updateMultiHashByte(String region, String prefix, final Map<String, Map<byte[], byte[]>> map, int expireSec) throws PrefixKeyException, RegionException, PoolJedisException {
    checkKVMapValidAndValueHashByte(map);
    PoolAndJedis poolAndJedis = getWriteShardedPoolJedis(region); // write
    ShardedJedisPool pool = null;
    ShardedJedis sj = null;
    try {
      pool = poolAndJedis.getShardedJedisPool();
      sj = poolAndJedis.getShardedJedis();
      ShardedJedisPipeline pip = sj.pipelined();

      int invalidKeyCount = 0;

      Iterator<Map.Entry<String, Map<byte[], byte[]>>> it = map.entrySet().iterator(); // validKeyMap
      while (it.hasNext()) {
        Map.Entry<String, Map<byte[], byte[]>> entry = it.next();
        if (StringUtils.isEmpty(entry.getKey())) {
          invalidKeyCount++;
          continue;
        }
        String redisKey = getRedisKey(prefix, entry.getKey());
        pip.hmset(redisKey.getBytes(), entry.getValue()); // core
        pip.expire(redisKey, expireSec);
      }
      List<Object> returnList = pip.syncAndReturnAll();

      int pipNumPer = 2;
      int pipSize = (map.size() - invalidKeyCount) * pipNumPer;
      if (pipSize == returnList.size()) {
        return new Long(map.size() - invalidKeyCount);
      }
      int successNum = 0;
      for (int i = 0; i + 1 < returnList.size(); ) {
        Object hms_res = returnList.get(i);
        Object exp_res = returnList.get(i + 1);
        if ((hms_res).equals("OK") && ((Long) exp_res).intValue() == FLAG.EXPIRE_SUCCESS) {
          successNum++;
        }
        i += pipNumPer;
      }
      return new Long(successNum);
    } finally {
      pool.returnResource(sj);
    }
  }
  /**
   * 返回 该 region 链接不上的REDIS的别名列表,如果返回空列表则表示所有的REDIS都是好的
   */
  public List<String> getBadRedisServer(String region) throws RegionException, PoolJedisException {

    String redisName = RedisConfig.getInstance().getRedisName(region);

    Map<String, ShardedJedisPool> namePoolsMap = (RedisConfig.getInstance().getRedisNameToRedisPoolMap()).get(redisName);

    List<String> resList = new ArrayList<String>();

    for (Map.Entry<String, ShardedJedisPool> entry : namePoolsMap.entrySet()) {
      ShardedJedisPool pool = entry.getValue();
      ShardedJedis sj = pool.getResource();
      try {
        if (!validateObject(sj)) {
          resList.add(entry.getKey());
        }
      } finally {
        pool.returnResource(sj);
      }
    }
    return resList;
  }

  /**
   * 验证REDIS集群中的所有REDIS是否都可以连接上,如果有一个链接不上则返回false
   *
   * @param jedis
   * @return
   */
  public boolean validateObject(final ShardedJedis jedis) {
    for (Jedis shard : jedis.getAllShards()) {
      if (!shard.ping().equals("PONG")) {
        return false;
      }
    }
    return true;
  }
}
