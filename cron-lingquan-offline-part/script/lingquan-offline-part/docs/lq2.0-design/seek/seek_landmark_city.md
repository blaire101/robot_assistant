# landmark city

Date | Author | version
------- | ------- | -------
2017-10-18 | clb | 6.0

## 1. coupon index name

city | index_name
------- | ------- 
杭州 | seekx\_landmark\_hangzhou
上海 | seekx\_landmark\_shanghai
青岛 | seekx\_landmark\_qingdao
厦门 | seekx\_landmark\_xiamen
武汉 | seekx\_landmark\_wuhan
成都 | seekx\_landmark\_chengdu

> x 代表某数字

## 2. 字段说明

```python
# encoding: utf-8
'''
Created on 2017年9月4日

@author: Blair Chan
'''

from elasticsearch import Elasticsearch

import constant

es = Elasticsearch([constant.ES_URL])

def get_mapping(index_type_name):

    mapping = {
        index_type_name: {
            "properties": {
                "landmark_id": {
                    "type": "integer"
                },
                "landmark_name": {
                    "type": "string"
                },
                "landmark_position": {
                    "geohash": True,
                    "geohash_precision": 7,
                    "type": "geo_point",
                    "geohash_prefix": True
                },
                "landmark_type": {
                    "type": "integer"
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

doc1 = {
    "landmark_id": 1,
    "landmark_name": "万象城",
    "landmark_position": [120.222562, 30.256514],
    "landmark_type": 1,

    "city_code": "hangzhou",

    "create_time_str": '2017-08-22 15:57:01',
    "modify_time_str": '2017-08-22 15:57:01',
    "status": 0
}

for index_name in constant.INDEX_SEEK_LANDMARK_LIST:

    my_type_name = index_name

    my_mapping = get_mapping(my_type_name)

    # es.indices.delete(index=index_name, ignore=400)

    es.indices.create(index=index_name, ignore=400)

    es.indices.put_mapping(doc_type=my_type_name, body=my_mapping, index=index_name)

    # es.index(index=index_name, doc_type=my_type_name, id="m1", body=doc1)


    print 'create and insert one item to index success!', index_name
```

## 字段说明 

 字段 | datatype | desc
------- | ------- | -------
_id | string | m1
city_code | string | hangzhou / shanghai / ...


