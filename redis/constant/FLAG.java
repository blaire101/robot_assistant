package com.saad.dmp_engine.redis.constant;

public interface FLAG {

  /**
   * region to redisName config file
   */
  String REGION_REDIS_SEP = "=";

  /**
   * strategy config file
   */
  String REDIS_STRATEGY_SEP = "=";
  String READSOLUTION_POOLS_SEP = "\\|";
  String POOLNAME_SEP = ",";
  String READ_WRITE_SEP = "#";

  /**
   * host config file
   */
  String REDIS_HOSTS_SEP = "#";
  String HOST_SEP = "\\|";
  String POOLNAMEIPPORT_SHARDNAME = "\\$";
  String POOLNAME_IP_PORT = ":";

  String USELESS = "#";
  /**
   *
   */
//  int DAY_SECONDS = 86400;

  /**
   * prefix_sep
   */
  String PRE_KEY_SEP = "_";

  /**
   * redis return value
   */
  int EXPIRE_SUCCESS = 1;

}
