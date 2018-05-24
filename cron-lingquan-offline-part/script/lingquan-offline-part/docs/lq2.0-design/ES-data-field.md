# ES 中商户和卡券的字段

Author | Version | Date
------- | ------- | -------
clb | 1.0 | 2017-12-28

## shop 

字段名 | 中文含义 | 描述
------- | ------- | -------
shop_id | 商户ID | 
shop_name | 商户名 |
shop_url | 商户URL | 详情链接
shop\_img\_url | 商户图片地址 | logo 图片地址
shop\_business\_center | 商圈名 | 大众点评 独有
shop_address | 商户地址
shop_phone | 商家联系电话 | 外部平台不一定具有
shop\_open\_hours | 商户营业时间 | 外部平台不一定具有
shop_position | 商户所在经纬度 |
shop\_power_count | 商户星级 | 大众点评 独有
shop\_review_count | 评论数 | 大众点评 独有
shop\_avg_price | 人均消费价格 | 大众点评 独有
coupon_count | 该商户拥有券的数量 | 
shop_source | 商户来源 | 标识商户属于哪个平台
level1_code | 商户一级分类 code | 数据组自定义的code标签
level2_code | 商户二级分类 code | 暂时没有存储
coupon_list | 卡券列表 | 
city_code | 城市编码 | 商户所属城市
status | 状态 | 标识是否有效


## coupon

字段名 | 中文含义 | 描述
------- | ------- | -------
coupon_id | 卡券ID |
coupon_name | 卡券名称 |
coupon_desc | 卡券描述 |
coupon\_promotional\_price | 现价 | 
coupon\_original\_price | 原价 | 大众独有
coupon_url | 卡券URL | 卡券链接详情
coupon_sold | 卡券已售数量 |
coupon\_img_url | 卡券图片地址 | 
coupon\_expire\_time_str | 过期时间 | 不一定有效
coupon_type | 卡券类型 | 独有
coupon\_label_list | - | 暂时存的是否为 通用券
coupon_store | 卡券库存 | 有效 (其他平台不一定)
coupon_condition | | 独有
coupon_source | 卡券来源 | 标识属于哪个平台
level1\_code\_list | 所属的一级分类编码列表
shop_count | 适用商户数量
shop_list | 适用的商户列表 |
city_code | 城市编码 | 所属商户的所属城市
status | 状态 | 标识是否有效

