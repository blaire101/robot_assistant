# encoding: utf-8
'''
Created on 2017年11月28日

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

        es.indices.delete(index=index_name, ignore=400)

        es.indices.create(index=index_name, ignore=400)
        es.indices.put_mapping(doc_type=doc_type, body=my_mapping, index=index_name)

        print 'create and insert one item to index success!', index_name
