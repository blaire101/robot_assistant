# encoding: utf-8
'''
@author: Blair Chan
'''

## coupon_list core ##
def get_coupon_docs_by_ids(ids, coupon_index, esHelper):

    if (ids is None or len(ids) == 0):
        return None

    start_num = 0
    size = len(ids)

    body = {
        "from": start_num,
        "size": size,
        "query": {
            "filtered": {
                "query": {
                    "bool": {
                        "must": [
                            {
                                "ids": {
                                    "values": ids
                                }
                            }
                        ],
                        "must_not": [
                            {
                                "term": {
                                    "status": 1
                                }
                            }
                        ],
                        "should": [
                        ]
                    }
                }
            }
        },
        "_source": {
            "includes": [
                "level1_code_list",
                "coupon_source"
            ],
            "excludes": []
        }

    }
    res = esHelper.search_hits_hits(index=coupon_index, doc_type=coupon_index, body=body)

    return res

## coupon_list core ##
def get_coupon_docs_for_all_field_by_ids(ids, coupon_index, esHelper):

    if (ids is None or len(ids) == 0):
        return None

    start_num = 0
    size = len(ids)

    if (size > 10000):
        size = 10000

    body = {
        "from": start_num,
        "size": size,
        "query": {
            "filtered": {
                "query": {
                    "bool": {
                        "must": [
                            {
                                "ids": {
                                    "values": ids
                                }
                            }
                        ],
                        "must_not": [
                            {
                                "term": {
                                    "status": 1
                                }
                            }
                        ],
                        "should": [
                        ]
                    }
                }
            }
        },
        "_source": {
            "includes": [
            ],
            "excludes": []
        }

    }
    res = esHelper.search_hits_hits(index=coupon_index, doc_type=coupon_index, body=body)

    return res
