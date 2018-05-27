package com.sinaad.dmp_engine.redis.exceptions;

public class PrefixKeyException extends Exception {
  public PrefixKeyException(String message) {
    super(message);
  }
}
