package com.saad.dmp_engine.redis.utils;

import com.saad.dmp_engine.redis.exceptions.PoolJedisException;
import com.saad.dmp_engine.redis.exceptions.PrefixKeyException;
import com.saad.dmp_engine.redis.exceptions.RegionException;

import java.util.Collection;
import java.util.Map;

interface Jedisable {

  /**
   * 在指定的 region 中, 设置 key 及其对应的 String Value 和 在 不过期， 成功返回 true，失败返回 false
   */
  public Boolean set(String region, String prefix, String key, String value) throws PoolJedisException, PrefixKeyException, RegionException;

  /**
   * 在指定的 region 中, 设置 key 及其对应的 String Value 和 在 expireSec 过期， 成功返回 true，失败返回 false
   */
  public Boolean set(String region, String prefix, String key, String value, int expireSec) throws PoolJedisException, PrefixKeyException, RegionException;

  /**
   * 在指定的 region 中, 取出 key 对应的 String Value
   */
  public String get(String region, String prefix, String key) throws PoolJedisException, PrefixKeyException, RegionException;

  /**
   * 在指定的 region 中, 设置 key 及其对应的 hash Value, 不过期， 成功返回 true
   */
  public Boolean hmset(String region, String prefix, String key, Map<String, String> hash) throws PrefixKeyException, PoolJedisException, RegionException;

  /**
   * 在指定的 region 中, 设置 key 及其对应的 hash Value， 成功返回 true，失败返回 false， 如果 map 为 empty，会爆 JedisDataException 异常
   */
  public Boolean hmset(String region, String prefix, String key, Map<String, String> hash, int expireSec) throws PrefixKeyException, PoolJedisException, RegionException;

  /**
   * 在指定的 region 中, 取得 key 及其对应的 hash Value， 返回 Map
   */
  public Map<String, String> hgetAll(String region, String prefix, String key) throws PoolJedisException, PrefixKeyException, RegionException;

  /**
   * 在指定的 region 中, 设置 key 及其对应的 hash Value 的 field 的 value， 如果 field 已经存在，返回 0，否则成功设置 返回 1.
   * <p/>
   * 注意此方法，如果key不存在，则field插入后，并没有设置expire, 如果key存在，则field插入，按照key的expire走
   */
  public Long hset(String region, String prefix, String key, String field, String value) throws PrefixKeyException, PoolJedisException, RegionException;

  /**
   * 在指定的 region 中, 设置 key 及其对应的 hash Value 的 field 的 value， 如果 field 已经存在，返回 0，否则成功设置 返回 1.
   * <p/>
   * 设置过期时间
   */
  public Long hset(String region, String prefix, String key, String field, String value, int expireSec) throws PrefixKeyException, PoolJedisException, RegionException;


  /**
   * 在指定的 region 中, 设置 key 的过期秒数, 设置成功返回 1,  如果 key 不存在，则设置失败，此时返回 0, 注意返回类型是 Long
   */
  public Long expire(String region, String prefix, String key, int seconds) throws PrefixKeyException, PoolJedisException, RegionException;

  /**
   * 在指定的 region 中, 取得 key 的余下的生存时间，单位是 秒， 注意返回类型是 Long.(returns -1 if the key exists but has no associated expire.)
   */
  public Long ttl(String region, String prefix, String key) throws PrefixKeyException, PoolJedisException, RegionException;

  /**
   * 在指定的 region 中, 取得 key 及其对应的 hash Value 的 field 的 value, 查询不到（key或者field不存在） return null
   */
  public String hget(String region, String prefix, String key, String field) throws PrefixKeyException, PoolJedisException, RegionException;

  /**
   * 在指定的 region 中, 删除 key 及其对应的 hash Value 的 fields， 返回成功删除的记录数
   */
  public Long hdel(String region, String prefix, String key, String... fields) throws PrefixKeyException, PoolJedisException, RegionException;

  /**
   * 在指定的 region 中, 删除 key 及其对应的 Value, 返回成功删除的记录数
   */
  public Long del(String region, String prefix, String key) throws PrefixKeyException, PoolJedisException, RegionException;

  /**
   * 在指定的 region 中, 删除 key 及其对应的 Value, 返回成功删除的记录数
   */
  public Long delMulti(String region, String prefix, Collection<String> keys) throws PrefixKeyException, PoolJedisException, RegionException;

  /**
   * 在指定的 region 中, 判断 key 是否存在, 存在返回 true，返回 false 为不存在
   */
  public Boolean exists(String region, String prefix, String key) throws PoolJedisException, PrefixKeyException, RegionException;

  /**
   * 在指定的 region 中, 取得 key 对应的 hash 的 field 的个数，返回值类型 Long
   */
  public Long hlen(String region, String prefix, String key) throws PrefixKeyException, PoolJedisException, RegionException;

  /**
   * 在指定的 region 中, 判断 key 对应的 hash 的 field 是否存在, 返true为存在，返回 false 为不存在
   */
  public Boolean hexists(String region, String prefix, String key, String field) throws PrefixKeyException, PoolJedisException, RegionException;

  /**
   * 在指定的 region 中, 取得 key 及其对应的 hash Value 的 各个 field 的 value ，返回 Map<String, String> field, value .
   */
  public Map<String, String> hmget(String region, String prefix, String key, String... fields) throws PrefixKeyException, PoolJedisException, RegionException;

  /**
   * 在指定的 region 中, 批量取出 key 对应的 Value (value 为 String 类型)， 返回 Map
   */
  public Map<String, String> getMulti(String region, String prefix, final Collection<String> keys) throws PrefixKeyException, RegionException, PoolJedisException;

  /**
   * 在指定的 region 中, 批量取出 key 对应的 Value (value 为 map 类型),  返回 Map
   */
  @SuppressWarnings("unchecked")
  public Map<String, Map<String, String>> hgetMulti(String region, String prefix, final Collection<String> keyList) throws PrefixKeyException, RegionException, PoolJedisException;

  /**
   * 在指定的 region 中, 批量设置 key 对应的 Value， 返回成功 set 进入的个数, 不过期
   */

  public Long setMultiString(String region, String prefix, final Map<String, String> map) throws PrefixKeyException, RegionException, PoolJedisException;

  /**
   * 在指定的 region 中, 批量设置 key 对应的 Value， 返回成功 set 进入的个数, expireSec 秒后 过期
   */
  public Long setMultiString(String region, String prefix, final Map<String, String> kvMap, int expireSec) throws PrefixKeyException, RegionException, PoolJedisException;

  /**
   * 在指定的 region 中, 批量设置 key 对应的 Value (value 为 Hash 类型)，  如果之前有key存在，则覆盖之前的值， 返回成功 set 进入的个数, 不过期
   */
  public Long setMultiHash(String region, String prefix, final Map<String, Map<String, String>> map) throws PrefixKeyException, RegionException, PoolJedisException;

  /**
   * 在指定的 region 中, 批量设置 key 对应的 Value (value 为 Hash 类型)， 如果之前有key存在，则覆盖之前的值，返回成功 set 进入的个数, expireSec 秒后 过期
   */
  public Long setMultiHash(String region, String prefix, final Map<String, Map<String, String>> map, int expireSec) throws PrefixKeyException, RegionException, PoolJedisException;

  /**
   * 在指定的 region 中, 批量更新 key 对应的 Value (value 为 Hash 类型)， 如果之前有key存在，则不覆盖之前的值， 只是增加field，返回成功 update 进入的个数
   */
  public Long updateMultiHash(String region, String prefix, final Map<String, Map<String, String>> map) throws PrefixKeyException, RegionException, PoolJedisException;

  /**
   * 在指定的 region 中, 批量更新 key 对应的 Value (value 为 Hash 类型)， 如果之前有key存在，则不覆盖之前的值， 只是增加field，返回成功 update 进入的个数, expireSec 秒后 过期
   */
  public Long updateMultiHash(String region, String prefix, final Map<String, Map<String, String>> map, int expireSec) throws PrefixKeyException, RegionException, PoolJedisException;

}
