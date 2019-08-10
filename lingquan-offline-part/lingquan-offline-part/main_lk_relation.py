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
import service.shop_service as shop_service
import service.coupon_service as coupon_service

import service.landmark_shop_coupon_service as landmark_shop_coupon_service

import service.shopping_service as shopping_service
import service.landmark_shopping_service as landmark_shopping_service


## all shop_count
def get_all_shop_count(shop_docs_for_0_count):
    return len(shop_docs_for_0_count)


## shop_list
def get_shop_list(distance_tag, lk_geo_point, shop_index, esHelper):
    shop_list = []

    shop_docs = shop_service.get_shop_list_by_distance_tag(distance_tag=distance_tag, lk_geo_point=lk_geo_point,
                                                           shop_index=shop_index,
                                                           esHelper=esHelper)

    for shop_doc in shop_docs:

        distance = round(float(shop_doc['sort'][0]), 3)

        if (distance <= 0.3):
            distance = distance + 0.1

        shop_item = {
            "unique_shop_id": shop_doc['_id'],
            "distance": distance,
            "level1_code": shop_doc['_source']['level1_code'],
            "shop_source": shop_doc['_source']['shop_source']
        }
        shop_list.append(shop_item)

    shop_list = sorted(shop_list, key=lambda f: f['distance'], reverse=False)

    return shop_list


# coupon_list #
def get_coupon_list(distance_tag, lk_geo_point, shop_index, coupon_index, esHelper):
    start_num = 0
    size = constant.SHOP_SIZE_PER_SEARCH_LIMIT
    max_value = 100000

    id_dis_map = {}

    for i in range(start_num, max_value, size):

        shop_docs_for_unique_coupon_id_list = shop_service.get_shop_docs_for_unique_coupon_id_list_by_distance_tag(
            start_num, size, distance_tag=distance_tag, lk_geo_point=lk_geo_point,
            shop_index=shop_index, esHelper=esHelper)

        id_dis_map = get_unique_coupon_id_dis_map(id_dis_map, shop_docs_for_unique_coupon_id_list, distance_tag)

        if shop_docs_for_unique_coupon_id_list is None or len(shop_docs_for_unique_coupon_id_list) == 0 or len(
                shop_docs_for_unique_coupon_id_list) < size:
            break

        start_num += size

    if len(id_dis_map) == 0:
        return []

    id_list = []
    for id in id_dis_map:
        id_list.append(id)

    coupon_docs = coupon_service.get_coupon_docs_by_ids(id_list, coupon_index, esHelper)

    if coupon_docs is None:
        logger.warning("coupon_docs is None")
        return []

    if len(id_dis_map) != len(coupon_docs):
        logger.warning("len(coupon_docs) != len(id_dis_map), coupon_docs len : %s, id_dis_map len : %s" % (len(coupon_docs), len(id_dis_map)))

    return build_coupon_list(coupon_docs, id_dis_map)


## id_dis_map for coupon_list ##
def get_unique_coupon_id_dis_map(id_dis_map, shop_docs_for_unique_coupon_id_list, distance_tag):

    SIZE_LIMIT = constant.COUPON_SIZE_LIMIT

    if (distance_tag >= 4):
        SIZE_LIMIT = constant.COUPON_SIZE_4_LIMIT

    if (id_dis_map is not None) and len(id_dis_map) >= SIZE_LIMIT:
        return id_dis_map    

    if shop_docs_for_unique_coupon_id_list is None or len(shop_docs_for_unique_coupon_id_list) == 0:
        return id_dis_map

    flag = False

    for shop_doc in shop_docs_for_unique_coupon_id_list:

        if flag == True:
            break

        coupon_list = shop_doc['_source']['coupon_list']

        distance = float(shop_doc['sort'][0])

        for unique_coupon_id_dict in coupon_list:

            unique_coupon_id = unique_coupon_id_dict['unique_coupon_id']

            if unique_coupon_id in id_dis_map:
                continue

            id_dis_map[unique_coupon_id] = distance

            if len(id_dis_map) == SIZE_LIMIT:
                flag = True
                break
    return id_dis_map


## build_coupon_list for coupon_list ##
def build_coupon_list(coupon_docs, id_dis_map):
    coupon_list = []
    for coupon_doc in coupon_docs:
        unique_coupon_id = coupon_doc['_id']
        coupon_source = coupon_doc['_source']['coupon_source']

        distance = round(float(id_dis_map[unique_coupon_id]), 3)
        if (distance <= 0.3):
            distance = distance + 0.1

        level1_code_list = coupon_doc['_source']['level1_code_list']

        coupon_item = {
            "unique_coupon_id": unique_coupon_id,
            "coupon_source": coupon_source,
            "distance": distance,
            "level1_code_list": level1_code_list
        }
        coupon_list.append(coupon_item)

    coupon_list = sorted(coupon_list, key=lambda f: f['distance'], reverse=False)

    return coupon_list


# get_all_coupon_count #

def get_all_coupon_count(distance_tag, lk_geo_point, shop_index, esHelper):
    start_num = 0
    size = constant.SHOP_SIZE_PER_SEARCH_LIMIT
    max_value = 100000

    id_set = set()

    for i in range(start_num, max_value, size):

        shop_docs = shop_service.get_shop_docs_for_coupon_count_by_0_distance_tag(start_num, size,
                                                                                  distance_tag=distance_tag,
                                                                                  lk_geo_point=lk_geo_point,
                                                                                  shop_index=shop_index,
                                                                                  esHelper=esHelper)

        id_set = get_id_set(id_set, shop_docs)

        if shop_docs is None or len(shop_docs) == 0 or len(shop_docs) < size:
            break

        start_num += size

    return len(id_set)


## id_set
def get_id_set(id_set, shop_docs):

    if len(id_set) >= constant.MAX_SIZE:
        return id_set

    if shop_docs is None or len(shop_docs) == 0:
        return id_set

    flag = False

    for shop_doc in shop_docs:

        if flag == True:
            break

        coupon_list = shop_doc['_source']['coupon_list']

        for unique_coupon_id_dict in coupon_list:

            unique_coupon_id = unique_coupon_id_dict['unique_coupon_id']

            if unique_coupon_id in id_set:
                continue
            id_set.add(unique_coupon_id)

            if len(id_set) >= constant.MAX_SIZE:
                flag = True
                break

    return id_set


def deal_landmark_shop_coupon_for_all_distance(lk_doc, shop_index, coupon_index, landmark_shop_coupon_index, esHelper):
    try:

        lk_geo_point = lk_doc['_source']['landmark_position']

        if (not com_service.is_valid_geo_point(lk_geo_point)):
            return

        for dis_tag in constant.DIS_TAG_LIST:
            all_shop_count = shop_service.get_all_shop_count_by_0_distance_tag(distance_tag=dis_tag,
                                                                               lk_geo_point=lk_geo_point,
                                                                               shop_index=shop_index,
                                                                               esHelper=esHelper)

            shop_list = get_shop_list(distance_tag=dis_tag, lk_geo_point=lk_geo_point, shop_index=shop_index,
                                      esHelper=esHelper)

            shop_count = len(shop_list)

            all_coupon_count = get_all_coupon_count(distance_tag=dis_tag, lk_geo_point=lk_geo_point,
                                                    shop_index=shop_index, esHelper=esHelper)
            coupon_list = get_coupon_list(distance_tag=dis_tag, lk_geo_point=lk_geo_point, shop_index=shop_index,
                                          coupon_index=coupon_index, esHelper=esHelper)
            coupon_count = len(coupon_list)

            landmark_shop_coupon_service.insert_lankmark_shop_coupon(lk_doc, dis_tag, all_shop_count, shop_count,
                                                                     shop_list, all_coupon_count, coupon_count,
                                                                     coupon_list, landmark_shop_coupon_index, esHelper)



    except BaseException, e:
        logger.warning("deal_landmark_shop_coupon_for_all_distance( %s , shop_index, esHelper) : %s." % (lk_doc['_id'], str(e)))
    finally:
        pass


############  shopping list ###########
# def get_shopping_list(lk_geo_point, shopping_index, esHelper):
#     shopping_list = []
#
#     shopping_docs = shopping_service.get_shopping_list(lk_geo_point=lk_geo_point, shopping_index=shopping_index, esHelper=esHelper)
#
#     for shopping_doc in shopping_docs:
#
#         distance = round(float(shopping_doc['sort'][0]), 3)
#
#         if (distance <= 0.3):
#             distance = distance + 0.1
#
#         shopping_item = {
#             "unique_shopping_id": shopping_doc['_id'],
#             "distance": distance,
#             "district_code": shopping_doc['_source']['district_code']
#         }
#         shopping_list.append(shopping_item)
#
#     shopping_list = sorted(shopping_list, key=lambda f: f['distance'], reverse=False)
#
#     return shopping_list

# def deal_landmark_shopping(lk_doc, shopping_index, landmark_shopping_index, esHelper):
#     try:
#
#         lk_geo_point = lk_doc['_source']['landmark_position']
#
#         if (not com_service.is_valid_geo_point(lk_geo_point)):
#             return
#
#         all_shopping_count = shopping_service.get_all_shopping_count(shopping_index=shopping_index, esHelper=esHelper)
#
#         shopping_list = get_shopping_list(lk_geo_point=lk_geo_point, shopping_index=shopping_index, esHelper=esHelper)
#
#         shopping_count = len(shopping_list)
#
#         dis_tag = 0
#
#         landmark_shopping_service.insert_lankmark_shopping(lk_doc, dis_tag, all_shopping_count, shopping_count,
#                                                                shopping_list, landmark_shopping_index, esHelper)
#
#
#     except BaseException, e:
#         logger.warning("deal_landmark_shopping( %s , shopping_index, esHelper) : %s." % (lk_doc['_id'], str(e)))
#     finally:
#         pass


def deal_lk_relation(landmark_index, coupon_index, shop_index, landmark_shop_coupon_index, esHelper):
                     # shopping_index, landmark_shopping_index
    landmark_docs = landmark_service.get_landmark_hits(landmark_index=landmark_index,
                                                       landmark_doc_type=landmark_index, esHelper=esHelper)

    cnt = 1
    for lk_doc in landmark_docs:
        logger.warning(str(datetime.now().strftime(constant.YMD_HMS)) + " cnt: " + str(cnt) + ' ' + lk_doc['_id'] + ' ' + landmark_index)

        cnt += 1

        # if (lk_doc['_source']['landmark_id'] < 1000):
        #     continue

        # deal_landmark_shopping(lk_doc=lk_doc, shopping_index=shopping_index, landmark_shopping_index=landmark_shopping_index, esHelper=esHelper)
        
        deal_landmark_shop_coupon_for_all_distance(lk_doc=lk_doc, shop_index=shop_index, coupon_index=coupon_index,
                                                    landmark_shop_coupon_index=landmark_shop_coupon_index,
                                                    esHelper=esHelper)
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

    city_center_pos = constant.CITY_CENTRE_POS_DICT[city_code]

    esHelper = EsHelper(ES_URL)

    logger.warning('this city for merge_shop : %s' % city_code)

    try:
        main_merge_shop.merge_shop(city_center_pos, city_code, esHelper)
        pass
    except BaseException, e:
        logger.warning("This city %s , merge_shop Exist Exception : %s" % (city_code, str(e)))

    landmark_index = com_service.get_landmark_index_by_city_code(city_code)
    coupon_index = com_service.get_coupon_index_by_city_code(city_code)
    shop_index = com_service.get_shop_index_by_city_code(city_code)

    # shopping_index = com_service.get_shopping_index_by_city_code(city_code)


    landmark_shop_coupon_index = com_service.get_landmark_shop_coupon_index_by_city_code(city_code)  # main logic tmp table

    # landmark_shopping_index = com_service.get_landmark_shopping_index_by_city_code(city_code)  # main logic tmp table

    try:
        deal_lk_relation(landmark_index=landmark_index, coupon_index=coupon_index, shop_index=shop_index,
                         landmark_shop_coupon_index=landmark_shop_coupon_index,
                         esHelper=esHelper)
        # shopping_index = shopping_index, landmark_shopping_index = landmark_shopping_index,
    except BaseException, e:
        logger.warning("This city landmark_index : %s, Exist Exception - deal_lk_relation : %s" % (landmark_index, str(e)))
    finally:
        pass
