# coupon

Date | Author | version
------- | ------- | -------
2017-10-18 | clb | 4.0
2017-11-28 | clb | 5.0 deldel shop_list.shop_id

## 1. coupon index name

city | index_name
------- | ------- 
杭州 | seekx\_coupon\_hangzhou
上海 | seekx\_coupon\_shanghai
青岛 | seekx\_coupon\_qingdao
厦门 | seekx\_coupon\_xiamen
武汉 | seekx\_coupon\_wuhan
成都 | seekx\_coupon\_chengdu
台州 | seekx\_coupon\_taizhou

> x 代表某数字 

```python
# encoding: utf-8
'''
Created on 2017年10月23日
@author: Blair Chan
'''
from elasticsearch import Elasticsearch
import constant
import sys
es = Elasticsearch([constant.ES_URL])
def get_mapping(index_type_name):
    mapping = {
        index_type_name: {
            "properties": {
                "coupon_id": {
                    'index': 'not_analyzed',
                    "type": "string"
                },
                "coupon_name_show": {
                    'index': 'not_analyzed',
                    "type": "string"
                },
                "coupon_name": {
                    "type": "string",
                    "fields": {
                        "raw": {
                            "type": "string",
                            "index": "not_analyzed"
                        }
                    }
                },
                "coupon_desc": {
                    "type": "string"
                },
                "coupon_promotional_price": {
                    "type": "double"
                },
                "coupon_original_price": {
                    "type": "double"
                },
                "coupon_url": {
                    'index': 'not_analyzed',
                    "type": "string"
                },
                "coupon_sold": {
                    "type": "integer"
                },
                "coupon_img_url": {
                    'index': 'not_analyzed',
                    "type": "string"
                },
                "coupon_wemall_id": {
                    'index': 'not_analyzed',
                    "type": "string"
                },
                "coupon_channel_detail_id": {
                    'index': 'not_analyzed',
                    "type": "string"
                },
                "coupon_expire_time_str": {
                    'index': 'not_analyzed',
                    "type": "string"
                },
                "coupon_type": {
                    "type": "integer"
                },
                "coupon_label_list": {
                    'index': 'not_analyzed',
                    "type": "string"
                },
                "coupon_store": {
                    "type": "integer"
                },
                "coupon_condition": {
                    "type": "string"
                },
                "coupon_source": {
                    "type": "integer"
                },
                "level1_code_list": {
                    "type": "string"
                },
                "shop_count": {
                    "type": "integer"
                },
                "shop_list": {
                    "type": "nested",
                    "properties":
                        {
                            "unique_shop_id": {'index': 'not_analyzed', "type": "string"},
                            "shop_address": {"type": "string"},
                            "shop_position":
                                {
                                    "geohash": True,
                                    "geohash_precision": 7,
                                    "type": "geo_point",
                                    "geohash_prefix": True
                                }
                        }
                },
                "city_code": {
                    'index': 'not_analyzed',
                    "type": "string"
                },
                "create_time_str": {
                    'index': 'not_analyzed',
                    "type": "string"
                },
                "modify_time_str": {
                    'index': 'not_analyzed',
                    "type": "string"
                },
                "status": {
                    "type": "integer"
                }
            }
        }
    }
    return mapping
if __name__ == '__main__':
    for index_name in constant.INDEX_SEEK_COUPON_LIST:
        doc_type = index_name
        my_mapping = get_mapping(index_name)
        #es.indices.delete(index=index_name, ignore=400)
        es.indices.create(index=index_name, ignore=400)
        es.indices.put_mapping(doc_type=doc_type, body=my_mapping, index=index_name)
        print 'create and insert one item to index success!', index_name
```

## 字段说明 

 字段 | datatype | desc
------- | ------- | -------
_id | string | 8092170_dp ( unique_coupon_id )
coupon_name_show | string | 不分词、原样存储，用于展示、上游限制该字段长度不超过100
coupon_name | string | raw 存储， 全部大写，用于搜索， 上游限制该字段长度不超过100
coupon_desc | string | 上游限制该字段长度不超过100
city_code | string | hangzhou / shanghai / ...
coupon_wemall_id | string | "0"
coupon_channel_detail_id | string | "0"
shop_list | nested | 店铺列表的嵌套对象，包含店铺id，名称，位置，类别信息
