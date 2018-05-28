package com.saad.dmp_engine.redis.utils;

import com.saad.dmp_engine.redis.exceptions.PoolJedisException;
import com.saad.dmp_engine.redis.exceptions.PrefixKeyException;
import com.saad.dmp_engine.redis.exceptions.RegionException;

import java.util.Collection;
import java.util.Map;

public interface BinaryJedisable {
  /**
   * 在指定的 region 中, 设置 key 及其对应的 byte[] Value 和 在 一天后 过期， 成功返回 true，失败返回 false
   */
  public Boolean set(String region, String prefix, String key, byte[] value) throws PoolJedisException, PrefixKeyException, RegionException;

  /**
   * 在指定的 region 中, 设置 key 及其对应的 bytes[] Value 和 在 expireSec 过期， 成功返回 true，失败返回 false
   */
  public Boolean set(String region, String prefix, String key, byte[] value, int expireSec) throws PoolJedisException, PrefixKeyException, RegionException;

  /**
   * 在指定的 region 中, 设置 key 及其对应的 hash Value, 并在 1 天 后过期， 成功返回 true
   */
  public Boolean hmsetByte(String region, String prefix, String key, Map<byte[], byte[]> hash) throws PrefixKeyException, PoolJedisException, RegionException;

  /**
   * 在指定的 region 中, 设置 key 及其对应的 hash Value， 成功返回 true，失败返回 false， 如果 map 为 empty，会爆 JedisDataException 异常
   */
  public Boolean hmsetByte(String region, String prefix, String key, Map<byte[], byte[]> hash, int expireSec) throws PrefixKeyException, PoolJedisException, RegionException;

  /**
   * 在指定的 region 中, 设置 key 及其对应的 hash Value 的 field 的 value， 如果 field 已经存在，返回 0，否则成功设置 返回 1.
   *
   * 注意此方法，如果key不存在，则field插入后，并没有设置expire, 如果key存在，则field插入，按照key的expire走
   */
  public Long hset(String region, String prefix, String key, byte[] field, byte[] value) throws PrefixKeyException, PoolJedisException, RegionException;

  /**
   * 在指定的 region 中, 批量设置 key 对应的 Value 为 byte[]， 返回成功 set 进入的个数, 1天后 过期
   */

  public Long setMultiStringByte(String region, String prefix, final Map<String, byte[]> map) throws PrefixKeyException, RegionException, PoolJedisException;

  /**
   * 在指定的 region 中, 批量设置 key 对应的 Value (value 为 byte[] )， 返回成功 set 进入的个数, expireSec 秒后 过期
   */
  public Long setMultiStringByte(String region, String prefix, final Map<String, byte[]> kvMap, int expireSec) throws PrefixKeyException, RegionException, PoolJedisException;

  /**
   * 在指定的 region 中, 批量设置 key 对应的 Value (value 为 Hash 类型, Hash 中为 byte[] )，  如果之前有key存在，则覆盖之前的值， 返回成功 set 进入的个数, 1天后 过期
   */
  public Long setMultiHashByte(String region, String prefix, final Map<String, Map<byte[], byte[]>> map) throws PrefixKeyException, RegionException, PoolJedisException;

  /**
   * 在指定的 region 中, 批量设置 key 对应的 Value (value 为 Hash 类型, Hash中为 bytes[])， 如果之前有key存在，则覆盖之前的值，返回成功 set 进入的个数, expireSec 秒后 过期
   */
  public Long setMultiHashByte(String region, String prefix, final Map<String, Map<byte[], byte[]>> map, int expireSec) throws PrefixKeyException, RegionException, PoolJedisException;

  /**
   * 在指定的 region 中, 批量更新 key 对应的 Value (value 为 Hash 类型, 内为 byte[])， 如果之前有key存在，则不覆盖之前的值， 只是增加field，返回成功 update 进入的个数,  一天 后 过期
   */
  public Long updateMultiHashByte(String region, String prefix, final Map<String, Map<byte[], byte[]>> map) throws PrefixKeyException, RegionException, PoolJedisException;

  /**
   * 在指定的 region 中, 批量更新 key 对应的 Value (value 为 Hash 类型, Hash 内为 byte[])， 如果之前有key存在，则不覆盖之前的值， 只是增加field，返回成功 update 进入的个数, expireSec 秒后 过期
   */
  public Long updateMultiHashByte(String region, String prefix, final Map<String, Map<byte[], byte[]>> map, int expireSec) throws PrefixKeyException, RegionException, PoolJedisException;

  public byte[] getByte(String region, String prefix, String key) throws RegionException, PoolJedisException, PrefixKeyException;

  public byte[] hget(String region, String prefix, String key, byte[] field) throws PrefixKeyException, PoolJedisException, RegionException;

  public Map<byte[], byte[]> hgetAllByte(String region, String prefix, String key) throws PoolJedisException, PrefixKeyException, RegionException;

  public Map<String, Map<byte[], byte[]>> hgetMultiByte(String region, String prefix, Collection<String> keys) throws
      PrefixKeyException, RegionException, PoolJedisException;
}
