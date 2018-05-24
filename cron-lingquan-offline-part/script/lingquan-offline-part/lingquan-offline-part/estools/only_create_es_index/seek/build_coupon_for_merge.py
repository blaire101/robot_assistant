# encoding: utf-8
'''
Created on 2017年10月23日

@author: Blair Chan
'''

from elasticsearch import Elasticsearch

import constant


doc_dp1 = {
    "coupon_id": "8092171",
    "coupon_name_show": "玲珑小镇 dp 8092171 代金券",
    "coupon_name": "玲珑小镇 dp 8092171 代金券",
    "coupon_desc": "仅售92元！价值100元的",
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
    "level1_code_list": [701],
    "coupon_label_list": [],
    "shop_count": 1,
    "shop_list": [
        {
            "unique_shop_id": "442_dp",
            "shop_address": "西湖文化广场18号银泰百货5层",
            "shop_position": [120.162918,30.277799]
        }
    ],
    "city_code": "hangzhou",
    "create_time_str": "2017-08-22 15:57:01",
    "modify_time_str": "2017-08-22 15:57:01",
    "status": 0
}

doc_dp2 = {
    "coupon_id": "8092172",
    "coupon_name_show": "玲珑小镇 dp 8092172 代金券",
    "coupon_name": "玲珑小镇 dp 8092172 代金券",
    "coupon_desc": "仅售92元！价值100元的（预售）。",
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
    "shop_count": 1,
    "shop_list": [
        {
            "unique_shop_id": "442_dp",
            "shop_address": "西湖文化广场18号银泰百货5层",
            "shop_position": [120.162918,30.277799]
        }
    ],
    "city_code": "hangzhou",
    "create_time_str": "2017-08-22 15:57:01",
    "modify_time_str": "2017-08-22 15:57:01",
    "status": 0
}


doc_x1 = {
    "coupon_id": "8092171",
    "coupon_name_show": "玲珑大镇 x 8092171 代金券",
    "coupon_name": "玲珑大镇 x 8092171 代金券",
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
    "coupon_source": 1,
    "level1_code_list": [701, 702],
    "coupon_label_list": [],
    "shop_count": 2,
    "shop_list": [
        {
            "unique_shop_id": "442_x",
            "shop_address": "西湖文化广场18号银泰百货5-4层",
            "shop_position": [
                 121.44836495, 
                 31.15997394
            ]
        },
        {
            "unique_shop_id": "443_x",
            "shop_address": "西湖文化广场-2号",
            "shop_position": [
                120.109219,
                30.178311
            ]
        }
    ],
    "city_code": "hangzhou",
    "create_time_str": "2017-08-22 15:57:01",
    "modify_time_str": "2017-08-22 15:57:01",
    "status": 0
}

doc_x2 = {
    "coupon_id": "8092172",
    "coupon_name_show": "玲珑大镇 x 8092172 代金券",
    "coupon_name": "玲珑大镇 x 8092172 代金券",
    "coupon_desc": "仅售92元！价值100元的（预售）代金券（5月23日盛大开业）。",
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
    "coupon_source": 1,
    "level1_code_list": [701, 702],
    "coupon_label_list": [],
    "shop_count": 1,
    "shop_list": [
        {
            "unique_shop_id": "442_x",
            "shop_address": "西湖文化广场18号银泰百货5-4层",
            "shop_position": [
                 121.44836495, 
                 31.15997394
            ]
        }
    ],
    "city_code": "hangzhou",
    "create_time_str": "2017-08-22 15:57:01",
    "modify_time_str": "2017-08-22 15:57:01",
    "status": 0
}

if __name__ == '__main__':

    es = Elasticsearch([constant.ES_URL])

    index_name = 'ptest_seek0_coupon_hangzhou'

    coupon_doc = es.index(index_name, index_name, doc_dp1, '8092171_dp')
    coupon_doc = es.index(index_name, index_name, doc_dp2, '8092172_dp')
    coupon_doc = es.index(index_name, index_name, doc_x1, '8092171_x')
    coupon_doc = es.index(index_name, index_name, doc_x2, '8092172_x')

