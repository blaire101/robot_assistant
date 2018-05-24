# encoding: utf-8
'''
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
                "shop_id": {
                    'index': 'not_analyzed',
                    "type": "string"
                },
                "shop_name_show": {
                    'index': 'not_analyzed',
                    "type": "string"
                },
                "shop_name": {
                    "type": "string",
                    "fields": {
                        "raw": {
                            "type": "string",
                            "index": "not_analyzed"
                        }
                    }
                },
                "shop_url": {
                    'index': 'not_analyzed',
                    "type": "string"
                },
                "shop_img_url": {
                    'index': 'not_analyzed',
                    "type": "string"
                },
                "shop_business_center": {
                    "type": "string"
                },
                "shop_address": {
                    "type": "string"
                },
                "shop_phone": {
                    'index': 'not_analyzed',
                    "type": "string"
                },
                "shop_open_hours": {
                    'index': 'not_analyzed',
                    "type": "string"
                },
                "shop_position": {
                    "geohash": True,
                    "geohash_precision": 7,
                    "type": "geo_point",
                    "geohash_prefix": True
                },
                "shop_power_count": {
                    "type": "double"
                },
                "shop_review_count": {
                    "type": "integer"
                },
                "shop_avg_price": {
                    "type": "double"
                },
                "coupon_count": {
                    "type": "integer"
                },
                "shop_source": {
                    "type": "integer"
                },
                "level1_code": {
                    'index': 'not_analyzed',
                    "type": "string"
                },
                "level2_code": {
                    'index': 'not_analyzed',
                    "type": "string"
                },
                "coupon_list": {
                    "type": "nested",
                    "properties": {
                        "unique_coupon_id": {
                            "index": "not_analyzed",
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
                        "coupon_source": {
                            "type": "integer"
                        },
                        "status": {
                            "type": "integer"
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

for index_name in constant.INDEX_SEEK_SHOP_LIST:

    my_type_name = index_name

    my_mapping = get_mapping(my_type_name)

    es.indices.delete(index=index_name, ignore=400)

    es.indices.create(index=index_name, ignore=400)
    #
    es.indices.put_mapping(doc_type=my_type_name, body=my_mapping, index=index_name)

    print 'create and insert one item to index success!', index_name
