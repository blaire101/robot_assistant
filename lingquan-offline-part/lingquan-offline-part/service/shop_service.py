# encoding: utf-8
'''
@author: Blair Chan
'''

import constant
import com_service

## all shop count  ##
def get_all_shop_count_core_by_0_distance_tag(start_num, size, range_distance, lk_geo_point, shop_index, esHelper):

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
                            "shop_position": {
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
                "shop_id"
            ],
            "excludes": []
        },
        "sort": [
            {
                "_geo_distance": {
                    "shop_position": {
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
    res = esHelper.search_hits(index=shop_index, doc_type=shop_index, body=body)['total']

    return res

## all shop count ##
def get_all_shop_count_by_0_distance_tag(distance_tag, lk_geo_point, shop_index, esHelper):

    start_num = constant.MIN_START
    size = constant.MAX_SIZE

    range_distance = com_service.get_0_range_distance(distance_tag)

    all_shop_count = 0

    all_shop_count = get_all_shop_count_core_by_0_distance_tag(start_num=start_num, size=size,
                                                                    range_distance=range_distance,
                                                                    lk_geo_point=lk_geo_point,
                                                                    shop_index=shop_index,
                                                                    esHelper=esHelper)

    return all_shop_count

## shop list core ##
def get_shop_list_core_by_distance_tag(start_num, size, range_distance, lk_geo_point, shop_index, esHelper):

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
                            "shop_position": {
                                "lon": lon,
                                "lat": lat
                            }
                        }
                    }
                ],
                "query": {
                    "bool": {
                        "must": [
                            {
                                "range": {
                                    "coupon_count": {
                                        'gte': 0
                                    }
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
                "level1_code",
                "shop_source"
            ],
            "excludes": []
        },
        "sort": [
            {
                "_geo_distance": {
                    "shop_position": {
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
    res = esHelper.search_hits_hits(index=shop_index, doc_type=shop_index, body=body)
    return res

def get_shop_list_by_distance_tag(distance_tag, lk_geo_point, shop_index, esHelper):

    start_num = constant.MIN_START
    size = constant.SHOP_SIZE_LIMIT
    if distance_tag >= 4:
        size = constant.SHOP_SIZE_4_LIMIT

    range_distance = com_service.get_range_distance(distance_tag)

    shop_docs = get_shop_list_core_by_distance_tag(start_num=start_num, size=size,
                                                   range_distance=range_distance,
                                                   lk_geo_point=lk_geo_point,
                                                   shop_index=shop_index,
                                                   esHelper=esHelper)

    return shop_docs

## unique_coupon_id_list by shop_docs
def get_shop_docs_for_unique_coupon_id_list_core_by_distance_tag(start_num, size, range_distance, lk_geo_point, shop_index, esHelper):

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
                            "shop_position": {
                                "lon": lon,
                                "lat": lat
                            }
                        }
                    }
                ],
                "query": {
                    "bool": {
                        "must": [
                            {
                                "range": {
                                    "coupon_count": {
                                        'gte': 1
                                    }
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
                "coupon_list.unique_coupon_id"
            ],
            "excludes": []
        },
        "sort": [
            {
                "_geo_distance": {
                    "shop_position": {
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
    res = esHelper.search_hits_hits(index=shop_index, doc_type=shop_index, body=body)
    return res

def get_shop_docs_for_unique_coupon_id_list_by_distance_tag(start_num, size, distance_tag, lk_geo_point, shop_index, esHelper):

    range_distance = com_service.get_range_distance(distance_tag)

    shop_docs_unique_coupon_id_list = get_shop_docs_for_unique_coupon_id_list_core_by_distance_tag(start_num=start_num, size=size,
                                                                                                   range_distance=range_distance,
                                                                                                   lk_geo_point=lk_geo_point,
                                                                                                   shop_index=shop_index,
                                                                                                   esHelper=esHelper)

    return shop_docs_unique_coupon_id_list

# all coupon count
def get_shop_docs_for_coupon_count_core_by_0_distance_tag(start_num, size, range_distance, lk_geo_point, shop_index, esHelper):
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
                            "shop_position": {
                                "lon": lon,
                                "lat": lat
                            }
                        }
                    }
                ],
                "query": {
                    "bool": {
                        "must": [
                            {
                                "range": {
                                    "coupon_count": {
                                        'gte': 1
                                    }
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
                "coupon_list.unique_coupon_id"
            ],
            "excludes": []
        },
        "sort": [
            {
                "_geo_distance": {
                    "shop_position": {
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
    res = esHelper.search_hits_hits(index=shop_index, doc_type=shop_index, body=body)
    return res

## all coupon count ##
def get_shop_docs_for_coupon_count_by_0_distance_tag(start_num, size, distance_tag, lk_geo_point, shop_index, esHelper):

    range_distance = com_service.get_0_range_distance(distance_tag)

    shop_docs = get_shop_docs_for_coupon_count_core_by_0_distance_tag(start_num=start_num, size=size,
                                                                       range_distance=range_distance,
                                                                       lk_geo_point=lk_geo_point,
                                                                       shop_index=shop_index,
                                                                       esHelper=esHelper)

    return shop_docs

## merge_shop
def get_shop_docs_core_by_distance(start_num, size, range_distance, lk_geo_point, shop_index, esHelper):

    shop_doc_type = shop_index

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
                            "shop_position": {
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
                "shop_id",
                "shop_name",
                "shop_address",
                "shop_position",
                "level1_code",
                "shop_source"
            ],
            "excludes": []
        },
        "sort": [
            {
                "_geo_distance": {
                    "shop_position": {
                        "lon": lon,
                        "lat": lat
                    },
                    "unit": "km",
                    "distance_type": "plane",
                    "order": "asc"
                }
            }
        ]
    }
    res = esHelper.search_hits_hits(index=shop_index, doc_type=shop_doc_type, body=body)
    return res
