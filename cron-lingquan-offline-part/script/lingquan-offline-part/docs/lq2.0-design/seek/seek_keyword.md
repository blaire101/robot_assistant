# keyword

Date | Author | version
------- | ------- | -------
2017-09-25 | jiaojinglong | 1.0

## 1. keyword index name

> x 代表某数字 

```python
# encoding: utf-8
'''
Created on 2017年9月25日

@author: jiaojinglong
'''

from elasticsearch import Elasticsearch

import constant

es = Elasticsearch([constant.ES_URL])


def get_mapping(index_type_name):
    mapping = {
        index_type_name: {
            "properties": {

                "keyword": {
                    "type": "string"
                },
                "city_code": {
                    'index': 'not_analyzed',
                    "type": "string"
                }
            }
        }
    }

    return mapping


doc = [
    {
        "keyword": "来福士新发现",
        "city_code": "hangzhou"
    },
    {
        "keyword": "来福士go辣",
        "city_code": "hangzhou"
    },
    {
        "keyword": "来福士侠饭",
        "city_code": "hangzhou"
    },
    {
        "keyword": "来福士绿茶",
        "city_code": "hangzhou"
    },
    {
        "keyword": "万象城外婆家",
        "city_code": "hangzhou"
    }
]

if __name__ == '__main__':

    index_name = constant.INDEX_SEEK_KEYWORD

    doc_type = index_name

    my_mapping = get_mapping(index_name)

    # es.indices.delete(index=index_name, ignore=400)

    es.indices.create(index=index_name, ignore=400)

    es.indices.put_mapping(doc_type=doc_type, body=my_mapping, index=index_name)

    # es.index(index=index_name, doc_type=doc_type, id="8092170_dp", body=doc)
    for i in range(0, 5):
        es.index(index=index_name, doc_type=doc_type, id=str(i), body=doc[i])
        print 'i is ' + str(i) + "doc is " + str(doc[i])
    print 'create and insert one item to index success!', index_name

```

## 字段说明 

 字段 | datatype | desc
------- | ------- | -------
_id | string | 123 
keyword | string | 支持分词、上游限制该字段长度不超过100
city_code | string | hangzhou / shanghai / ...
