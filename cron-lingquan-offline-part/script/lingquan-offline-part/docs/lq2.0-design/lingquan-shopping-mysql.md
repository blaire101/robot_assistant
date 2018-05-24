# 商场表结构


## wemall_app_shopping_mall

```
CREATE TABLE `wemall_app_shopping_mall` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT,
  `nid` varchar(50) DEFAULT NULL COMMENT '自定义ID',
  `shopping_mall_name` varchar(64) DEFAULT NULL COMMENT '商场名称',
  `address` varchar(255) DEFAULT NULL COMMENT '商场地址',
  `surface_plot` varchar(255) DEFAULT NULL COMMENT '封面图',
  `longitude` varchar(64) DEFAULT NULL COMMENT '经度',
  `latitude` varchar(64) DEFAULT NULL COMMENT '纬度',
  `province_code` varchar(20) DEFAULT NULL COMMENT '省编码',
  `city_code` varchar(20) DEFAULT NULL COMMENT '市编码',
  `district_code` varchar(20) DEFAULT NULL COMMENT '区编码',
  `disabled` tinyint(4) DEFAULT '0' COMMENT '0:可用 1：不可用',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='wemall-领券商场表';
```

## wemall_app_shopping_mall_activity

```
CREATE TABLE `wemall_app_shopping_mall_activity` (
  `id` bigint(18) NOT NULL,
  `shopping_mall_nid` varchar(50) NOT NULL COMMENT '商场NID',
  `shopping_mall_headlines` varchar(255) DEFAULT NULL COMMENT '商场头条',
  `image_url` varchar(255) DEFAULT NULL COMMENT '图片地址',
  `custom_url` varchar(255) DEFAULT NULL COMMENT '自定义地址(1:无跳转，2:链接，3:活动，4:自定义列表)',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='wemall商场-领券-活动配置表';
```

## wemall_app_shopping_mall_coupon

```
CREATE TABLE `wemall_app_shopping_mall_coupon` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT,
  `shopping_mall_nid` varchar(50) NOT NULL COMMENT '商场NID',
  `coupon_id` bigint(18) NOT NULL COMMENT '电子券ID',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='wemall商场-领券-电子券关联表';
```

## wemall_app_shopping_mall_merchant

```
CREATE TABLE `wemall_app_shopping_mall_merchant` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT,
  `shopping_mall_nid` varchar(50) NOT NULL COMMENT '商场NID',
  `merchant_id` bigint(18) NOT NULL COMMENT '商户ID',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='wemall商场-领券-商户关联表';

```