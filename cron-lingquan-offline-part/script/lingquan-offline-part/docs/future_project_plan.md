# 后续项目规划

Date | Desc | Author
------- | ------- | -------
2017-10 | 后续项目的思考 | Blair

## 1. 领券平台 数据服务

Type | Skill
------- | -------
1. 数据接口服务 | Java dubbo、Spring、Restful、Elastic、...
2. 数据智能计算 | Python、Elastic、Jieba、Shell...
3. 数据抓取爬虫 | Python、Scrapy、Elastic、...
4. 数据抽取与录入 | Java、Spring、Python、Elastic、...

## 2. 领券平台 机器人

> 未来可类比 “盒马鲜生” 类似产品
> 
> 可结合 推荐系统服务 与 用户画像 的信息

Type | Desc
------- | -------
1. 搜索服务 | 搜索 卡券、商户、商圈地址 等
2. 推荐服务 | 见标题 3，结合推荐服务提供个性化结果
3. 用户画像 | 见标题 4，结合用户画像，并可根据聊天内容收集特征用于 user profile 的维度
4. 订单等业务信息查询 | ...

>  Python、NLP (Bayes、LDA、LR、HMM、Word2Vector...)

## 3. 推荐系统

> 推荐系统分为 Offline-recommender 与 Online-recommender 两套推荐服务         
>
> 结合 用户 的订单、浏览、位置 等信息 提供 商户、卡券、活动 等推荐服务, 提高转化率
> 
> 推荐过程可结合 用户画像 与 用户当前位置 等信息    
>    

Type | Desc | Skill | Implementation framework
------- | ------- | ------- | -------
推荐系统 | 推荐 商户、卡券、活动 等信息 | 统计分析、机器学习、LR、CF、ALS、DL等 | Python、Spark、Hive、Hdfs、Elastic

## 4. 用户画像

> 运用统计分析，数据挖掘和NLP相结合

Attribute | Type | 想法
------- | ------- | -------
兴趣 interest | 美食 <br> 购物 <br> 丽人.. | 根据订单等信息 统计标签概率分布 与 ALS扩展标签来实现
人群 crowd | 面食达人 <br> 日料达人.. | 根据 (订单、浏览记录 等行为) 制定 rule 实现, <br> 后不断收集准确信息并过滤(也可考虑结合其他 Attribute)，再用机器学习模型反作用于推大量用户
年龄 age | - | 10~20、20~25、25~35、35~45， > 45
性别 gender | - | male、female、-
体重 weight | - | 河马的体重
等级 level | - | 河马的星级
商圈 business_center | - | 概率统计 与 距离计算
地域 zone | - | 概率统计 与 距离计算
... | ... | ...
