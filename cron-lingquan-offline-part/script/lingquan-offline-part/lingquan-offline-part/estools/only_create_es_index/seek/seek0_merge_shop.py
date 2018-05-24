# encoding: utf-8

import constant
from elasticsearch import Elasticsearch

es = Elasticsearch(constant.ES_URL)


def get_mapping(index_type_name):
    mapping = {
        index_type_name: {
            'properties': {
                'old_shop_id': {
                    'index': 'not_analyzed',
                    'type': 'string'
                },
                'old_shop_name': {
                    'type': 'string'
                },
                'old_shop_address': {
                    'type': 'string'
                },
                'new_shop_id': {
                    'type': 'string'
                },
                'new_shop_name': {
                    'type': 'string'
                },
                'new_shop_address': {
                    'type': 'string'
                },
                'merge_shop_coupon_level_count': {
                    'type': 'integer'
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


for index_name in constant.INDEX_SEEK_MERGE_SHOP_LIST:

    doc_type = index_name

    my_mapping = get_mapping(index_name)

    # es.indices.delete(index=index_name, ignore=400)
    es.indices.create(index=index_name, ignore=400)

    es.indices.put_mapping(doc_type=doc_type, body=my_mapping, index=index_name)

    print 'create and insert one item to index success!', index_name



