package com.saad.dmp_engine.redis.exceptions;

public class PoolJedisException extends Exception {
  public PoolJedisException() {
  }
  public PoolJedisException(String message) {
    super(message);
  }
}
