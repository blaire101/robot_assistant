# seekx_landmark_shopping_city

Date | Author | version
------- | ------- | -------
2017-11-25 | Navy | 1.0
2017-11-27 | Martis | 2.0
2017-12-19 | lena | 3.0

## 1. landmark_shopping index name

city | index_name
------- | ------- 
杭州 | seekx_landmark_shopping_hangzhou


> x 代表某数字

## 2. 字段说明

```python
# encoding: utf-8
'''
Created on 2017年11月25日 下午2:36:13

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
                "unique_landmark_id": {
                    'index': 'not_analyzed',
                    "type": "string"
                },
                "landmark_type": {
                    "type": "integer"
                },
                "all_shopping_count": {
                    "type": "integer"
                },
                "shopping_count": {
                    "type": "integer"
                },
                "shopping_list": {
                    "type": "nested",
                    "properties":
                        {
                            "unique_shopping_id": {
                                "index": "not_analyzed",
                                "type": "string"
                            },
                            "distance": {
                                "type": "double"
                           },
                           "district_code": {
                                'index': 'not_analyzed',
                                "type": "string"
                            }
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
    "all_shopping_count": 3,
    "shopping_count": 3,
    "shopping_list": [
        {
            "unique_shopping_id": "6129548",
            "distance": 1.911,
            "district_code":"330104"
        },
        {
            "unique_shopping_id": "6129549",
            "distance": 2.911,
            "district_code":"330103"          
        },
        {
            "unique_shopping_id": "6129550",
            "distance": 3.911,
            "district_code":"330102"
        }
    ],
    "distance_tag": 0,
    "create_time_str": "2017-11-28 15:57:01",
    "modify_time_str": "2017-11-28 15:57:01",
    "status": 0
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
