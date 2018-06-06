# shopping data type define

Date | Author | version
------- | ------- | -------
2017-11-24 | Navy | 1.0
2017-11-27 | Martis | 2.0

## 1. shopping index name

city | index_name
------- | ------- 
杭州 | seekx\_shopping\_hangzhou
上海 | seekx\_shopping\_shanghai
青岛 | seekx\_shopping\_qingdao
厦门 | seekx\_shopping\_xiamen
武汉 | seekx\_shopping\_wuhan
成都 | seekx\_shopping\_chengdu
台州 | seekx\_shopping\_taizhou

## 2. 字段说明

```python
# encoding: utf-8
'''
@author: Navy
'''
from elasticsearch import Elasticsearch
from datetime import datetime
import constant
es = Elasticsearch([constant.ES_URL])
def get_mapping(index_type_name):
    mapping = {
        index_type_name: {
            "properties": {
                "shopping_id": {
                    'index': 'not_analyzed',
                    "type": "string"
                },
                "shopping_group_id": {
                    "index": "not_analyzed",
                    "type": "string"
                },
                "shopping_name_show": {
                    'index': 'not_analyzed',
                    "type": "string"
                },
                "shopping_headlines": {
                    'index': 'not_analyzed',
                    "type": "string"
                },
                "shopping_name": {
                    "type": "string",
                    "fields": {
                        "raw": {
                            "type": "string",
                            "index": "not_analyzed"
                        }
                    }
                },
                "shopping_surface_url": {
                    'index': 'not_analyzed',
                    "type": "string"
                },
                "shopping_url": {
                    'index': 'not_analyzed',
                    "type": "string"
                },
                "shopping_address": {
                    "type": "string"
                },
                "shopping_position": {
                    "geohash": True,
                    "geohash_precision": 7,
                    "type": "geo_point",
                    "geohash_prefix": True
                },
                "shopping_activity_count":{
                    "type": "integer"
                },
                "shop_list": {
                    "type": "nested",
                    "properties": {
                         "shop_id": {
                              "index": "not_analyzed",
                              "type": "string"
                         },
                        "shop_name": {
                            "type": "string"
                        },
                        "shop_url": {
                            "index": "not_analyzed",
                            "type": "string"
                        },
                        "shop_img_url": {
                            "index": "not_analyzed",
                            "type": "string"
                        },
                        "shop_is_recommend": {
                            "type": "integer"
                        }
                    }
                },
                 "shop_count":{
                    "type": "integer"
                },
               "city_code": {
                    'index': 'not_analyzed',
                    "type": "string"
                },
                "district_code": {
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
  
doc = {
         "shopping_id":"2",
         "shopping_group_id": "11",
         "shopping_name_show": "湖滨银泰c",
         "shopping_name": "湖滨银泰C",
         "shopping_headlines":"商场头条",
         "shopping_surface_url": "http://www.qq.com",
         "shopping_url": "",
         "shopping_address":"下城区延安路",
         "shopping_position":[120.162458,30.253159],
         "shopping_activity_count":4,
         "shop_list": [
             {
               "shop_id":"13",
               "shop_name": "新白鹿",
               "shop_url": "",
               "shop_img_url": "http://www.qq.com",
               "shop_is_recommend":1
               
             },
             {
               "shop_id":"14",
               "shop_name": "弄堂里",
               "shop_url": "http://www.qq.com/",
               "shop_img_url": "http://www.qq.com",
               "shop_is_recommend":0
             }              
          ],
          "shop_count":2,
          "city_code": "330100",
          "district_code": "330103",
          "create_time_str": "2017-11-28 15:57:01",
          "modify_time_str": "2017-11-28 15:57:01",
          "status":0
}
    
    
for index_name in constant.INDEX_SEEK_SHOP_LIST:
    my_type_name = index_name
    my_mapping = get_mapping(my_type_name)
    #es.indices.delete(index=index_name, ignore=400)
    es.indices.create(index=index_name, ignore=400)
    #
    es.indices.put_mapping(doc_type=my_type_name, body=my_mapping, index=index_name)
    print 'create and insert one item to index success!', index_name
```

