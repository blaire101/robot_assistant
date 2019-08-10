# encoding: utf-8
'''
@author: Blair Chan
'''

import constant

## all_shopping_count
def get_all_shopping_count(shopping_index, esHelper):

    start_num = 0
    size = constant.SHOPPING_SIZE_LIMIT

    all_shopping_count = 0

    all_shopping_count = get_all_shopping_count_core(start_num=start_num, size=size, shopping_index=shopping_index, esHelper=esHelper)

    return all_shopping_count


## all_shopping_count
def get_all_shopping_count_core(start_num, size, shopping_index, esHelper):

    body = {
        "from": start_num,
        "size": size,
        "query": {
            "filtered": {
                "query": {
                    "bool": {
                        "must": [
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
                "COUNT"
            ],
            "excludes": []
        }
    }

    res = esHelper.search_hits(index=shopping_index, doc_type=shopping_index, body=body)['total']

    return res


## shop list core ##
def get_shopping_list_core(start_num, size, range_distance, lk_geo_point, shopping_index, esHelper):

    lon = lk_geo_point[0]
    lat = lk_geo_point[1]

    start_distance = range_distance[0]
    end_distance = range_distance[1]

    body = {
        "from": start_num,
        "size": size,
        "query": {
            "filtered": {
                "filter": [
                    {
                        "geo_distance_range": {
                            "from": start_distance,
                            "to": end_distance,
                            "distance_type": "plane",
                            "unit": "km",
                            "shopping_position": {
                                "lon": lon,
                                "lat": lat
                            }
                        }
                    }
                ],
                "query": {
                    "bool": {
                        "must": [
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
                "district_code"
            ],
            "excludes": []
        },
        "sort": [
            {
                "_geo_distance": {
                    "shopping_position": {
                        "lon": lon,
                        "lat": lat
                    },
                    "distance_type": "plane",
                    "unit": "km",
                    "order": "asc"
                }
            }
        ]
    }

    res = esHelper.search_hits_hits(index=shopping_index, doc_type=shopping_index, body=body)

    return res

def get_shopping_list(lk_geo_point, shopping_index, esHelper):

    start_num = constant.MIN_START
    size = constant.SHOPPING_SIZE_LIMIT

    range_distance = ["0km", None]

    shop_docs = get_shopping_list_core(start_num=start_num, size=size,
                                                   range_distance=range_distance,
                                                   lk_geo_point=lk_geo_point,
                                                   shopping_index=shopping_index,
                                                   esHelper=esHelper)

    return shop_docs