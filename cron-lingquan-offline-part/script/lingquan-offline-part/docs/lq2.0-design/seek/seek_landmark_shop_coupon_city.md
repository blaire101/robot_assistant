# seekx_landmark_shop_coupon_city

Date | Author | version
------- | ------- | -------
2017-10-18 | clb | 6.0

## 1. landmark_shop_coupon index name

city | index_name
------- | ------- 
杭州 | seekx_landmark_shop_coupon_hangzhou


> x 代表某数字

## 2. 字段说明

```python
# encoding: utf-8
'''
Created on 2017年10月18日 下午2:36:13

@author: Blair Chan
'''

from elasticsearch import Elasticsearch
from datetime import datetime

import constant

es = Elasticsearch([constant.ES_URL])


def get_mapping(index_type_name):
    mapping = {
        index_type_name: {
            "properties": {

                "unique_landmark_id": {
                    'index': 'not_analyzed',
                    "type": "string"
                },
                "landmark_type": {
                    "type": "integer"
                },
                "all_coupon_count": {
                    "type": "integer"
                },
                "coupon_count": {
                    "type": "integer"
                },
                "coupon_list": {
                    "type": "nested",
                    "properties":
                        {
                            "unique_coupon_id": {
                                "index": "not_analyzed",
                                "type": "string"
                            },
                            "distance": {
                                "type": "double"
                            },
                            "coupon_source": {
                                "type": "integer"
                            },
                            "level1_code_list": {
                                "type": "string"
                            }
                        },
                },
                "all_shop_count": {
                    "type": "integer"
                },
                "shop_count": {
                    "type": "integer"
                },

                "shop_list": {
                    "type": "nested",
                    "properties":
                        {
                            "unique_shop_id": {'index': 'not_analyzed', "type": "string"},
                            "distance": {"type": "double"},
                            "level1_code": {'index': 'not_analyzed', "type": "string"},
                            "shop_source": {"type": "integer"}

                        }
                },

                "distance_tag": {
                    "type": "integer"
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
    "unique_landmark_id": "m1",
    "landmark_type": 0,

    "all_coupon_count": 520,

    "coupon_count": 300,
    "coupon_list": [
        {
            "unique_coupon_id": "8092172_dp_1",
            "coupon_source": 2,
            "distance": 1.250,
            "level1_code_list": [
                701, 702
            ]
        },
        {
            "unique_coupon_id": "8092172_dp_2",
            "coupon_source": 2,
            "distance": 1.616,
            "level1_code_list": [
                701, 702
            ]
        },
        {
            "unique_coupon_id": "8092172_dp_3",
            "coupon_source": 2,
            "distance": 1.911,
            "level1_code_list": [
                701,
                702
            ]
        }
    ],
    "all_shop_count": 520,
    "shop_count": 100,
    "shop_list": [
        {
            "unique_shop_id": "6129548_dp_1",
            "distance": 1.911,
            "level1_code": "701",
            "shop_source": 2
        },
        {
            "unique_shop_id": "6129548_dp_2",
            "distance": 2.911,
            "level1_code": "701",
            "shop_source": 2
        },
        {
            "unique_shop_id": "6129548_dp_3",
            "distance": 3.911,
            "level1_code": "701",
            "shop_source": 2

        }
    ],

    "distance_tag": 3,

    "create_time_str": '2017-08-22 15:57:01',
    "modify_time_str": '2017-08-22 15:57:01',
    "status": 0,
}

for index_name in constant.INDEX_SEEK_LANDMARK_SHOP_COUPON_LIST:

    doc_type = index_name

    my_mapping = get_mapping(index_name)

    # es.indices.delete(index=index_name, ignore=400)

    es.indices.create(index=index_name, ignore=400)

    es.indices.put_mapping(doc_type=doc_type, body=my_mapping, index=index_name)

    # es.index(index=index_name, doc_type=doc_type, id="m1_1", body=doc)
    # for i in range(1, 50):
    #     es.index(index=index_name, doc_type=doc_type, id="m" + str(i) + "_1", body=doc)
    #     print 'i is ' + str(i)

    print 'create and insert one item to index success!', index_name
```


## 字段说明 

 字段 | datatype | desc
------- | ------- | -------
- | - | -
counpon_list | nested  | 券列表
shop_list | nested  | 店列表