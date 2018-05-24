# -*- coding: UTF-8 -*-
'''
Created on 2017年10月27日

@author: Blair Chan
'''
import logging
import os.path
import sys

'''estools'''
from estools.constant import ES_URL
from estools.EsHelper import EsHelper

import service.constant as constant

import service.shop_service as ods_shop_service

import service.com_service as com_service

import service.merge_shop_service as merge_shop_service


def merge_shop(center_pos, city_code, esHelper):
    shop_index = com_service.get_shop_index_by_city_code(city_code)
    coupon_index = com_service.get_coupon_index_by_city_code(city_code)

    if (not com_service.is_valid_shop_index(shop_index)):
        print "is_valid_shop_index : %s ." % shop_index
        return
    if (not com_service.is_valid_coupon_index(coupon_index)):
        print "is_valid_coupon_index : %s ." % coupon_index
        return

    start_num = 0
    size = 2000
    max_value = 100000

    for dis_range in constant.DIS_RANGE_MERGE_SHOP:
        range_distance = dis_range

        for i in range(start_num, max_value, size):

            shop_docs = ods_shop_service.get_shop_docs_core_by_distance(start_num, size, range_distance, center_pos,
                                                                        shop_index, esHelper)

            start_num += size

            merge_shop_service.merge_shop_for_docs(shop_docs, city_code, shop_index, esHelper)

            if (shop_docs is None or (len(shop_docs) >= 0 and len(shop_docs) < size)):
                print "shop_docs size : %d" % len(shop_docs)
                break

    return 0

if __name__ == '__main__':

    program = os.path.basename(sys.argv[0])
    logger = logging.getLogger(program)
    logging.basicConfig(format='%(asctime)s: %(levelname)s: %(message)s')
    logging.root.setLevel(level=logging.INFO)
    logger.info("running %s" % ' '.join(sys.argv))

    if (len(sys.argv) < 2):
        logger.info("len(sys.argv) < 2")
        sys.exit(1)

    city_code = sys.argv[1]

    logger.info("This city : %s" % (city_code))

    city_center_pos = constant.CITY_CENTRE_POS_DICT[city_code]

    esHelper = EsHelper(ES_URL)

    logger.info('this city for merge_shop : %s' % city_code)

    try:
        merge_shop(city_center_pos, city_code, esHelper)
    except BaseException, e:
        logger.info("This city %s , merge_shop Exist Exception : %s" % (city_code, str(e)))
