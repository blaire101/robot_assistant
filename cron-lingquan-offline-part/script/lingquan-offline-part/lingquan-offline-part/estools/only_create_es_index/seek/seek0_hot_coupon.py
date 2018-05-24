# encoding: utf-8
'''
Created on 2017年9月22日

@author: Blair Chan
'''

from elasticsearch import Elasticsearch

import constant

es = Elasticsearch([constant.ES_URL])


def get_mapping(index_type_name):
    mapping = {
        index_type_name: {
            "properties": {

                "coupon_id": {
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
                "level1_code_list": {"type": "string"},

                "shop_id": {'index': 'not_analyzed', "type": "string"},
                "shop_position":
                    {
                        "geohash": True,
                        "geohash_precision": 7,
                        "type": "geo_point",
                        "geohash_prefix": True
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


doc = {
    "coupon_id": "8092170",
    "coupon_name": "牛很鲜潮汕牛肉火锅_仅售92元！价值100元的（预售）代金券",
    "coupon_desc": "仅售92元！价值100元的（预售）代金券（5月23日盛大开业）1张，除酒水、锅底、调料外全场通用，可叠加使用，提供免费WiFi。",
    "coupon_promotional_price": 92,
    "coupon_original_price": 100,
    "coupon_url": "http://t.data_process.com/deal/24998799",
    "coupon_sold": 1138,
    "coupon_img_url": "http://p0.meituan.net/deal/b43eaca19801dedd220fa773c95bdd4337419.jpg",
    "coupon_wemall_id": "0",
    "coupon_channel_detail_id": "0",
    "coupon_expire_time_str": "2017-06-30 15:57:01",
    "coupon_type": 0,
    "coupon_store": 0,
    "coupon_condition": "满88包邮",
    "coupon_source": 2,
    "level1_code_list": [701, 702],
    "coupon_label_list": [],
    "unique_shop_id": "6129548_dp",
    "shop_position": [
        120.104219,
        30.176301
    ],

    "city_code": "hangzhou",
    "create_time_str": "2017-08-22 15:57:01",
    "modify_time_str": "2017-08-22 15:57:01",
    "status": 0
}

if __name__ == '__main__':

    index_name = constant.INDEX_SEEK_HOT_COUPON

    doc_type = index_name

    my_mapping = get_mapping(index_name)

    #es.indices.delete(index=index_name, ignore=400)

    es.indices.create(index=index_name, ignore=400)

    es.indices.put_mapping(doc_type=doc_type, body=my_mapping, index=index_name)

    # es.index(index=index_name, doc_type=doc_type, id="8092170_dp", body=doc)

    print 'create and insert one item to index success!', index_name
