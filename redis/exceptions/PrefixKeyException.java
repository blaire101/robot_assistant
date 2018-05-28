package com.saad.dmp_engine.redis.exceptions;

public class PrefixKeyException extends Exception {
  public PrefixKeyException(String message) {
    super(message);
  }
}
