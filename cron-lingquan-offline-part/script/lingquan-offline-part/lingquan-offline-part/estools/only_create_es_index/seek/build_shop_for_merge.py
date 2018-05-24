# encoding: utf-8
'''
@author: Blair Chan
'''

from elasticsearch import Elasticsearch

import constant

doc1 = {
    "shop_id": "442",
    "shop_name_show": "八面玲珑小镇(银泰百货店)",
    "shop_name": "八面玲珑小镇(银泰百货店)",
    "shop_url": "http://www.dianping.com/shop/6129548",
    "shop_img_url": "http://p1.meituan.net/mogu/60ac464ebf579ddf999858ac0f248ac961237.jpg",
    "shop_business_center": "西湖广场",
    "shop_address": "西湖文化广场18号银泰百货5层",

    "shop_phone": "",
    "shop_open_hours": "",

    "shop_position": [120.162918,30.277799],
    "shop_power_count": 3.5,
    "shop_review_count": 6391,
    "shop_avg_price": 78,
    "coupon_count": 2,
    "shop_source": 2,

    "level1_code": "701",
    "level2_code": "701001",

    "coupon_list": [
        {
            "unique_coupon_id": "8092172_dp",
            "coupon_name": "玲珑小镇 dp 8092172 代金券",
            "coupon_source": 2,
            "status": 0
        },
        {
            "unique_coupon_id": "8092171_dp",
            "coupon_name": "玲珑小镇 dp 8092171 代金券",
            "coupon_source": 2,
            "status": 0
        }
    ],
    "city_code": "hangzhou",
    "create_time_str": '2017-07-22 15:57:01',
    "modify_time_str": '2017-07-22 15:57:01',
    "status": 0
}


doc2 = {
    "shop_id": "442",
    "shop_name_show": "八面玲珑大镇(银泰百货店)",
    "shop_name": "八面玲珑大镇(银泰百货店)",
    "shop_url": "http://www.dianping.com/shop/6129548",
    "shop_img_url": "http://p1.meituan.net/mogu/60ac464ebf579ddf999858ac0f248ac961237.jpg",
    "shop_business_center": "西湖文化广场",
    "shop_address": "西湖文化广场18号银泰百货5-4层",

    "shop_phone": "",
    "shop_open_hours": "",

    "shop_position": [120.162928,30.277709],
    "shop_power_count": 3.5,
    "shop_review_count": 6391,
    "shop_avg_price": 78,
    "coupon_count": 2,
    "shop_source": 1,

    "level1_code": "701",
    "level2_code": "701001",

    "coupon_list": [
        {
            "unique_coupon_id": "8092171_x",
            "coupon_name": "玲珑大镇 x 8092171 代金券",
            "coupon_source": 1,
            "status": 0
        },
        {
            "unique_coupon_id": "8092172_x",
            "coupon_name": "玲珑大镇 x 8092172 代金券",
            "coupon_source": 1,
            "status": 0
        }
    ],
    "city_code": "hangzhou",
    "create_time_str": '2017-10-22 15:57:01',
    "modify_time_str": '2017-10-22 15:57:01',
    "status": 0
}

if __name__ == '__main__':

    es = Elasticsearch([constant.ES_URL])

    index_name = 'ptest_seek0_shop_hangzhou'

    es.index(index_name, index_name, doc1, '442_dp')
    es.index(index_name, index_name, doc2, '442_x')

