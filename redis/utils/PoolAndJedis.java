package com.saad.dmp_engine.redis.utils;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

public class PoolAndJedis {
  private ShardedJedisPool shardedJedisPool;
  private ShardedJedis shardedJedis;

  public PoolAndJedis(ShardedJedisPool shardedJedisPool, ShardedJedis shardedJedis) {
    this.shardedJedisPool = shardedJedisPool;
    this.shardedJedis = shardedJedis;

  }

  public ShardedJedisPool getShardedJedisPool() {
    return shardedJedisPool;
  }

  public void setShardedJedisPool(ShardedJedisPool shardedJedisPool) {
    this.shardedJedisPool = shardedJedisPool;
  }

  public ShardedJedis getShardedJedis() {
    return shardedJedis;
  }

  public void setShardedJedis(ShardedJedis shardedJedis) {
    this.shardedJedis = shardedJedis;
  }
}
