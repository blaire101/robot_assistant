# encoding: utf-8
'''
Created on 2017年7月28日

@author: Blair Chan
'''

from datetime import datetime

import constant

def insert_lankmark_shop_coupon(lk_doc, dis_tag, all_shop_count, shop_count, shop_list, all_coupon_count, coupon_count, coupon_list, lk_shop_coupon_index, esHelper):


    doc = get_lk_shop_coupon_doc(lk_doc, dis_tag, all_shop_count, shop_count, shop_list, all_coupon_count, coupon_count, coupon_list)

    if doc is None:
        return

    _id = lk_doc['_id'] + '_' + str(dis_tag)

    esHelper.index(index=lk_shop_coupon_index, doc_type=lk_shop_coupon_index, id=_id, data=doc)


def get_lk_shop_coupon_doc(lk_doc, dis_tag, all_shop_count, shop_count, shop_list, all_coupon_count, coupon_count, coupon_list):

    doc = None

    create_time = datetime.now()
    create_time_str = create_time.strftime(constant.YMD_HMS)

    try:
        doc = {
            "unique_landmark_id": lk_doc['_id'],
            "landmark_type": lk_doc['_source']['landmark_type'],

            "all_shop_count": all_shop_count,
            "shop_count": shop_count,
            "shop_list": shop_list,

            "all_coupon_count": all_coupon_count,
            "coupon_count": coupon_count,
            "coupon_list": coupon_list,

            "distance_tag": dis_tag,

            "create_time_str": create_time_str,
            "modify_time_str": create_time_str,
            "status": 0
        }
    except BaseException, e:
        print "Exist Exception : %s About lk_doc['_id'] : %s dis_tag: %s" % (str(e), lk_doc['_id'], str(dis_tag))
    finally:
        pass

    return doc


