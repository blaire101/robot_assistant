package com.saad.dmp_engine.redis.config;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.saad.dmp_engine.redis.constant.FLAG;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedisConfig {

  private static final Logger logger = Logger.getLogger(RedisConfig.class);
  /**
   * 单例
   */
  private static final RedisConfig instance = new RedisConfig(); // 单例

  public static RedisConfig getInstance() {
    return instance;
  }

  /**
   * 连接时间
   */
  private static final int REDIS_CONNECTION_TIMEOUT = 4000;

  /**
   * 类成员变量  three String and Three map
   */
  private String hostFile;

  private String regionToRedisNameFile;

  private String redisNameToStrategyFile;

  /**
   * region -> redisName
   */
  private Map<String, String> regionToRedisNameMap = Maps.newHashMap();

  /**
   * redisName -> Strategy
   */
  private Map<String, Strategy> redisNameToStrategyMap = Maps.newHashMap();

  /**
   * redisName -> RedisPoolMap
   */
  private Map<String, Map<String, ShardedJedisPool>> redisNameToRedisPoolMap = Maps.newHashMap();

  private RedisConfig() {
  }

  private RedisConfig(String regionToRedisNameFile, String redisNameToStrategyFile, String hostFile) {
    this.regionToRedisNameFile = regionToRedisNameFile;
    this.redisNameToStrategyFile = redisNameToStrategyFile;
    this.hostFile = hostFile;
  }

  /**
   * getter and setter about three string (configFiles)
   */
  public String getHostFile() {
    return hostFile;
  }

  public void setHostFile(String hostFile) {
    this.hostFile = hostFile;
  }

  public String getRegionToRedisNameFile() {
    return regionToRedisNameFile;
  }

  public void setRegionToRedisNameFile(String regionToRedisNameFile) {
    this.regionToRedisNameFile = regionToRedisNameFile;
  }

  public String getRedisNameToStrategyFile() {
    return redisNameToStrategyFile;
  }

  public void setRedisNameToStrategyFile(String redisNameToStrategyFile) {
    this.redisNameToStrategyFile = redisNameToStrategyFile;
  }

  /**
   * getter and setter about three map
   */
  public Map<String, String> getRegionToRedisNameMap() {
    return regionToRedisNameMap;
  }

  public void setRegionToRedisNameMap(Map<String, String> regionToRedisNameMap) {
    this.regionToRedisNameMap = regionToRedisNameMap;
  }

  public Map<String, Strategy> getRedisNameToStrategyMap() {
    return redisNameToStrategyMap;
  }

  public void setRedisNameToStrategyMap(Map<String, Strategy> redisNameToStrategyMap) {
    this.redisNameToStrategyMap = redisNameToStrategyMap;
  }

  public Map<String, Map<String, ShardedJedisPool>> getRedisNameToRedisPoolMap() {
    return redisNameToRedisPoolMap;
  }

  public void setRedisNameToRedisPoolMap(Map<String, Map<String, ShardedJedisPool>> redisNameToRedisPoolMap) {
    this.redisNameToRedisPoolMap = redisNameToRedisPoolMap;
  }

  /**
   * Get RedisName by Region
   */
  public String getRedisName(String region) {
    logger.debug("region: " + region);
    String redisName = this.regionToRedisNameMap.get(region);
    return redisName;
  }

  /**
   * Get Strategy by RedisName
   */
  public Strategy getStrategy(String redisName) {
    logger.debug("redisName: " + redisName);
    Strategy strategy = this.redisNameToStrategyMap.get(redisName);
    return strategy;
  }

  /**
   * Get redisPoolMap by RedisName
   */
  public Map<String, ShardedJedisPool> getRedisPoolMap(String redisName) {
    logger.debug("redisName: " + redisName);
    Map<String, ShardedJedisPool> map = this.redisNameToRedisPoolMap.get(redisName);
    return map;
  }

  /**
   * load regionToRedisNameMap、redisNameToStrategyMap、redisNameToRedisPoolMap
   *
   * @throws java.io.IOException
   */
  public void load() throws IOException {

    logger.info("RedisConfig Load Start...");

    this.regionToRedisNameMap = loadRegionToRedisMap();
    this.redisNameToStrategyMap = loadRedisNameToStrategyMap();
    this.redisNameToRedisPoolMap = loadRedisNameToRedisPoolMap();

    logger.info("RedisConfig Load End...");
  }

  public void loadDefaultConfigFiles() throws IOException {

    logger.info("RedisConfig Load Start...");

    this.regionToRedisNameFile = "configFiles/regionToRedis.properties";
    this.redisNameToStrategyFile = "configFiles/strategy.properties";
    this.hostFile = "configFiles/host.txt";

    /*************************************************/
    this.regionToRedisNameMap = loadRegionToRedisMap();
    this.redisNameToStrategyMap = loadRedisNameToStrategyMap();
    this.redisNameToRedisPoolMap = loadRedisNameToRedisPoolMap();

    logger.info("RedisConfig Load End...");
  }

  public void loadDefaultConfigFiles(String regionToRedisNameFile, String redisNameToStrategyFile, String hostFile) throws IOException {

    logger.info("RedisConfig Load Start...");

    this.regionToRedisNameFile = regionToRedisNameFile;
    this.redisNameToStrategyFile = redisNameToStrategyFile;
    this.hostFile = hostFile;

    /*************************************************/
    this.regionToRedisNameMap = loadRegionToRedisMap();
    this.redisNameToStrategyMap = loadRedisNameToStrategyMap();
    this.redisNameToRedisPoolMap = loadRedisNameToRedisPoolMap();

    logger.info("RedisConfig Load End...");
  }

  /**
   * (1) load regionToRedisNameMap
   */
  private Map<String, String> loadRegionToRedisMap() throws IOException {
    Preconditions.checkArgument(StringUtils.isNotEmpty(this.regionToRedisNameFile));

    File file = new ClassPathResource(this.regionToRedisNameFile).getFile();

    Preconditions.checkArgument(file.exists());

    Map<String, String> regionMap = Maps.newHashMap();

    LineIterator it = FileUtils.lineIterator(file, "UTF-8");

    try {
      while (it.hasNext()) {
        String line = it.nextLine().trim();
        if (line.isEmpty()) {
          continue;
        }
        loadRegionToRedisMapByLine(line, regionMap);
      }
    } finally {
      LineIterator.closeQuietly(it);
    }

    return regionMap;
  }

  private void loadRegionToRedisMapByLine(String line, Map<String, String> regionMap) { // 传递进来的是引用
    // region=name
    String[] regionRedis = line.split(FLAG.REGION_REDIS_SEP);

    int sepSize = 2;

    if (regionRedis.length != sepSize) {
      logger.info("region To Redis config file exist the line format is wrong");
      return;
    }

    regionMap.put(regionRedis[0].trim(), regionRedis[1].trim());
  }

  /**
   * (2) load redisNameToStrategyMap
   */
  private Map<String, Strategy> loadRedisNameToStrategyMap() throws IOException {
    Preconditions.checkArgument(StringUtils.isNotEmpty(this.redisNameToStrategyFile));

    File file = new ClassPathResource(this.redisNameToStrategyFile).getFile();

    Preconditions.checkArgument(file.exists());

    Map<String, Strategy> resultMap = Maps.newHashMap();

    LineIterator it = FileUtils.lineIterator(file, "UTF-8");

    try {
      while (it.hasNext()) {
        String line = it.nextLine().trim();
        if (line.isEmpty()) {
          continue;
        }
        loadRedisNameToStrategyMapByLine(line, resultMap);
      }
    } finally {
      LineIterator.closeQuietly(it);
    }
    return resultMap;
  }

  private void loadRedisNameToStrategyMapByLine(String line, Map<String, Strategy> resultMap) { // 传递进来的是引用
    String[] redisStrategy = line.split(FLAG.REDIS_STRATEGY_SEP);
    int redisSepStrategySize = 2;
    if (redisStrategy.length != redisSepStrategySize) {
      logger.info("RedisName Strategy config file exist the line format is wrong");
      return;
    }

    String redisName = redisStrategy[0];
    String strategyLine = redisStrategy[1];
    // REDIS1=read:order|pool2,pool3#write:pool1
    String[] solutions = strategyLine.split(FLAG.READ_WRITE_SEP);
    int solutionsSize = 2;
    if (solutions.length != solutionsSize) {
      logger.info("Strategy config file exist the line format is wrong");
      return;
    }

    String readSolution = new String();
    String writeSolution = new String();

    if (solutions[0].indexOf("read:") != -1 && solutions[1].indexOf("write:") != -1) {
      readSolution = solutions[0];
      writeSolution = solutions[1];
    } else if (solutions[1].indexOf("read:") != -1 && solutions[0].indexOf("write:") != -1) {
      readSolution = solutions[1];
      writeSolution = solutions[0];
    } else {
      return;
    }
    readSolution = readSolution.trim().substring("read:".length()).trim();
    writeSolution = writeSolution.trim().substring("write:".length()).trim();

    String[] readSolutionPools = readSolution.split(FLAG.READSOLUTION_POOLS_SEP);
    int size = 2;
    if (readSolutionPools.length != size) {
      logger.info("readsolution pools Strategy config file exist the line format is wrong");
      return;
    }
    String readStrategy = readSolutionPools[0].trim();
    String readPools = readSolutionPools[1].trim();

    if (StringUtils.isEmpty(readStrategy) || StringUtils.isEmpty(readPools)) {
      logger.info("readStrategy or readPools is null or empty. error");
      return;
    }
    Strategy strategy = new Strategy(readStrategy, readPools, writeSolution);

    resultMap.put(redisName, strategy);
  }

  /**
   * (3) load redisNameToRedisPoolMap
   */
  private Map<String, Map<String, ShardedJedisPool>> loadRedisNameToRedisPoolMap() throws IOException {
    Preconditions.checkArgument(StringUtils.isNotEmpty(this.hostFile));

    File file = new ClassPathResource(this.hostFile).getFile();

    Preconditions.checkArgument(file.exists());

    Map<String, Map<String, ShardedJedisPool>> resultMap = Maps.newHashMap();
    Map<String, Map<String, List<JedisShardInfo>>> resultTempMap = Maps.newHashMap(); // core

    LineIterator it = FileUtils.lineIterator(file, "UTF-8");

    try {
      while (it.hasNext()) {
        String line = it.nextLine().trim();
        if (line.startsWith(FLAG.USELESS)) continue;
        if (line.isEmpty()) {
          continue;
        }
        String[] redisSepHost = line.split(FLAG.REDIS_HOSTS_SEP);
        int redisSepHostSize = 2;
        if (redisSepHost.length != redisSepHostSize) {
          continue;
        }
        String redisName = redisSepHost[0].trim();
        String nameIpPortHosts = redisSepHost[1].trim();

        loadRedisNameToRedisPoolMapByLine(redisName, nameIpPortHosts, resultTempMap);
      }
    } finally {
      LineIterator.closeQuietly(it);
    }

    GenericObjectPoolConfig poolConfig = getSharededJedisPoolConfig();

    /**
     * 根据 resultTempMap 构造 resultMap
     */
    for (Map.Entry<String, Map<String, List<JedisShardInfo>>> entry : resultTempMap.entrySet()) {

      Map<String, ShardedJedisPool> namePoolsMap = Maps.newHashMap();

      for (Map.Entry<String, List<JedisShardInfo>> poolNameToListEntry : entry.getValue().entrySet()) {
        namePoolsMap.put(poolNameToListEntry.getKey(), new ShardedJedisPool(poolConfig, poolNameToListEntry.getValue()));
      }

      if (!namePoolsMap.isEmpty()) {
        resultMap.put(entry.getKey(), namePoolsMap);
      }
    }
    return resultMap;
  }

  private void loadRedisNameToRedisPoolMapByLine(String redisName, String nameIpPortHosts, Map<String, Map<String, List<JedisShardInfo>>> resultTempMap) { // 传递进来的是引用
    // REDIS1#pool1:10.13.88.140:6591$shardname|pool2:10.13.88.140:6593$shardname|pool3:10.13.88.140:6593$shardname
    Map<String, List<JedisShardInfo>> poolNameMap = resultTempMap.get(redisName);
    if (poolNameMap == null) {
      poolNameMap = new HashMap<String, List<JedisShardInfo>>();
    }
    createPoolNameMapByLine(nameIpPortHosts, poolNameMap);
    resultTempMap.put(redisName, poolNameMap);
  }

  private void createPoolNameMapByLine(String nameIpPortHostsLine, Map<String, List<JedisShardInfo>> poolNameMap) { // 记住 poolNameMap 是引用
    // name1:ip:port$shardname|name2:ip:port$shardname|name3:ip:port$shardname
    String[] nameIpPortHosts = nameIpPortHostsLine.split(FLAG.HOST_SEP); // \\|
    if (nameIpPortHosts.length <= 0) {
      return;
    }
    for (int i = 0; i < nameIpPortHosts.length; i++) {

      String[] str = nameIpPortHosts[i].split(FLAG.POOLNAMEIPPORT_SHARDNAME); // $
      int poolShardNameBySepSize = 2;
      if (str.length != poolShardNameBySepSize) {
        logger.error("host.txt config file exists error str!");
        continue;
      }

      String poolnameIpPort = str[0];
      String shardName = str[1];

      String[] pipStr = poolnameIpPort.split(FLAG.POOLNAME_IP_PORT);

      int poolNameIpPortBySepSize = 3;

      if (pipStr.length != poolNameIpPortBySepSize) {
        logger.error("host.txt config file exists error! pipStr");
        continue;
      }

      String poolName = pipStr[0];
      String iP = pipStr[1];
      String port = pipStr[2];

      JedisShardInfo jedisShardInfo = new JedisShardInfo(iP, Integer.parseInt(port), REDIS_CONNECTION_TIMEOUT, shardName);

      List<JedisShardInfo> lst = poolNameMap.get(poolName);
      if (lst == null) {
        lst = new ArrayList<JedisShardInfo>();
      }
      lst.add(jedisShardInfo);
      poolNameMap.put(poolName, lst);
    }
  }

  /**
   * 构建 PoolConfig
   */
  public GenericObjectPoolConfig getSharededJedisPoolConfig() {
    // redis池中的待分配redis链接
    GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();

    /**
     * 说明一个pool可以有多少个Jedis实例, 默认为 8
     */
    poolConfig.setMaxTotal(128); //    poolConfig.maxActive = 128;

    /**
     * 最大的空闲连接数,  默认为 8
     */
    poolConfig.setMaxIdle(80);
    /**
     * 最小的空闲连接数,  默认为 0
     */
    poolConfig.setMinIdle(8);

    /**
     * 获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
     */
    poolConfig.setMaxWaitMillis(180000);

    /**
     * 逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
     *
     * test idle 线程的时间间隔
     */
    poolConfig.setTimeBetweenEvictionRunsMillis(7000);

    /**
     * 对象空闲多少毫秒后逐出池。
     *
     * 当空闲时间>该值 且 空闲连接>最大空闲数 时直接逐出,不再根据MinEvictableIdleTimeMillis判断
     * (默认逐出策略)
     */
    poolConfig.setMinEvictableIdleTimeMillis(14000);
    // determines the number of objects examined in each run of the idle
    // object evictor.
    /**
     * 在每次运行检查的对象数的驱逐者线程空闲对象(如果有的话)
     *
     * 一次最多evict的pool里的jedis实例个数
     */
    poolConfig.setNumTestsPerEvictionRun(8);
    // indicates whether or not idle objects should be validated using the

    /**
     * idle状态监测用异步线程evict进行检查
     */
    poolConfig.setTestWhileIdle(true);

    /**
     * 获得一个jedis实例的时候是否检查连接可用性 ( ping() )
     */
    poolConfig.setTestOnBorrow(false);

    /**
     * return 一个jedis实例给pool时，是否检查连接可用性 ( ping() )
     */
    poolConfig.setTestOnReturn(false);

    return poolConfig;
  }
}
