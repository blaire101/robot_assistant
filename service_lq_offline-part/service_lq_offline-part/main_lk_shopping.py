# -*- coding: UTF-8 -*-
'''
@author: Blair
'''

import logging
import os.path
import sys

from datetime import datetime
import main_merge_shop

'''estools'''
from estools.constant import ES_URL
from estools.EsHelper import EsHelper

import service.constant as constant

'''com'''
import service.com_service as com_service

'''landmark, shop, coupon, landmark_shop_coupon'''
import service.landmark_service as landmark_service

import service.shopping_service as shopping_service
import service.landmark_shopping_service as landmark_shopping_service


############  shopping list ###########
def get_shopping_list(lk_geo_point, shopping_index, esHelper):
    shopping_list = []

    shopping_docs = shopping_service.get_shopping_list(lk_geo_point=lk_geo_point, shopping_index=shopping_index, esHelper=esHelper)

    for shopping_doc in shopping_docs:

        distance = round(float(shopping_doc['sort'][0]), 3)

        if (distance <= 0.3):
            distance = distance + 0.1

        shopping_item = {
            "unique_shopping_id": shopping_doc['_id'],
            "distance": distance,
            "district_code": shopping_doc['_source']['district_code']
        }
        shopping_list.append(shopping_item)

    shopping_list = sorted(shopping_list, key=lambda f: f['distance'], reverse=False)

    return shopping_list

def deal_landmark_shopping(lk_doc, shopping_index, landmark_shopping_index, esHelper):
    try:

        lk_geo_point = lk_doc['_source']['landmark_position']

        if (not com_service.is_valid_geo_point(lk_geo_point)):
            return

        all_shopping_count = shopping_service.get_all_shopping_count(shopping_index=shopping_index, esHelper=esHelper)

        shopping_list = get_shopping_list(lk_geo_point=lk_geo_point, shopping_index=shopping_index, esHelper=esHelper)

        shopping_count = len(shopping_list)

        dis_tag = 0

        landmark_shopping_service.insert_lankmark_shopping(lk_doc, dis_tag, all_shopping_count, shopping_count,
                                                               shopping_list, landmark_shopping_index, esHelper)


    except BaseException, e:
        logger.warning("deal_landmark_shopping( %s , shopping_index, esHelper) : %s." % (lk_doc['_id'], str(e)))
    finally:
        pass


def deal_lk_shopping(landmark_index, shopping_index, landmark_shopping_index, esHelper):
    landmark_docs = landmark_service.get_landmark_hits(landmark_index=landmark_index,
                                                       landmark_doc_type=landmark_index, esHelper=esHelper)

    cnt = 1
    for lk_doc in landmark_docs:
        logger.warning(str(datetime.now().strftime(constant.YMD_HMS)) + " cnt: " + str(cnt) + ' ' + lk_doc['_id'] + ' ' + landmark_index)

        cnt += 1

        # if (lk_doc['_source']['landmark_id'] < 1000):
        #     continue

        deal_landmark_shopping(lk_doc=lk_doc, shopping_index=shopping_index, landmark_shopping_index=landmark_shopping_index, esHelper=esHelper)

        #break

if __name__ == '__main__':

    program = os.path.basename(sys.argv[0])
    logger = logging.getLogger(program)
    logging.basicConfig(format='%(asctime)s: %(levelname)s: %(message)s')
    logging.root.setLevel(level=logging.WARNING)
    logger.warning("running %s" % ' '.join(sys.argv))

    if (len(sys.argv) < 2):
        logger.warning("len(sys.argv) < 2")
        sys.exit(1)

    city_code = sys.argv[1]

    logger.warning("This city : %s" % (city_code))

    esHelper = EsHelper(ES_URL)

    landmark_index = com_service.get_landmark_index_by_city_code(city_code)

    shopping_index = com_service.get_shopping_index_by_city_code(city_code)

    landmark_shopping_index = com_service.get_landmark_shopping_index_by_city_code(city_code)  # main logic tmp table

    try:
        deal_lk_shopping(landmark_index=landmark_index, shopping_index=shopping_index, landmark_shopping_index=landmark_shopping_index, esHelper=esHelper)
    except BaseException, e:
        logger.warning("This city landmark_index : %s, Exist Exception - deal_lk_shopping : %s" % (landmark_index, str(e)))
    finally:
        pass
