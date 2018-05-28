package com.saad.dmp_engine.redis.config;

import com.google.common.collect.Lists;
import com.saad.dmp_engine.redis.constant.FLAG;

import java.util.Arrays;
import java.util.List;

public class Strategy {

  private String readStrategy;

  private List<String> readPools = Lists.newArrayList();

  private String writePoolName;

  public Strategy(String readStrategy, List<String> readPools, String writePoolName) {
    this.readStrategy = readStrategy;
    this.readPools = readPools;
    this.writePoolName = writePoolName;
  }

  public Strategy(String readStrategy, String readPools, String writePoolName) { // read:order|pool1#write:pool1
    // order   pool2,pool3   pool1

    this.readStrategy = readStrategy;

    String[] shardPools = readPools.split(FLAG.POOLNAME_SEP);

    this.readPools = Arrays.asList(shardPools);

    this.writePoolName = writePoolName;

  }

  public String getReadStrategy() {
    return readStrategy;
  }

  public void setReadStrategy(String readStrategy) {
    this.readStrategy = readStrategy;
  }

  public List<String> getReadPools() {
    return readPools;
  }

  public void setReadPools(List<String> readPools) {
    this.readPools = readPools;
  }

  public String getWritePoolName() {

    return writePoolName;
  }

  public void setWritePoolName(String writePoolName) {
    this.writePoolName = writePoolName;
  }
}
