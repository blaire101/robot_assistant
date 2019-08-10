# encoding: utf-8
'''
@author: Blair Chan
'''

import sys
import jieba

reload(sys)
sys.setdefaultencoding('utf-8')

jieba.initialize()

from seg_word import STOP_WORD_LIST

import coupon_service

import com_service


def is_same_shop(shop_doc_1, shop_doc_2):

    if (shop_doc_1['_source']['shop_source'] == shop_doc_2['_source']['shop_source']):
        return False

    level1_code_1 = shop_doc_1['_source']['level1_code']
    level1_code_2 = shop_doc_2['_source']['level1_code']

    if (level1_code_1 != level1_code_2):
        return False

    geo1 = shop_doc_1['_source']['shop_position']
    geo2 = shop_doc_2['_source']['shop_position']

    distance = com_service.get_distance(geo1, geo2)

    if (distance > 0.2 or distance < 0):
        return False

    shop_name1 = com_service.get_valid_shop_name(shop_doc_1['_source']['shop_name'])
    shop_name2 = com_service.get_valid_shop_name(shop_doc_2['_source']['shop_name'])
    
    if (shop_name1 == '' or shop_name2 == ''):
        return False

    if (shop_name1 == shop_name2):
        return True

    seg1_list = jieba.cut(shop_name1)
    seg2_list = jieba.cut(shop_name2)

    same_count = 0

    for seg1 in seg1_list:

        if seg1 in STOP_WORD_LIST:
            continue
        # 2017.11.27
        if len(seg1) <= 3:
            continue

        for seg2 in seg2_list:
            if seg2 in STOP_WORD_LIST:
                continue

            if (seg1 == seg2):
                same_count = same_count + 1

    if same_count > 0:
        return True

    return False


def merge_2_shop_for_coupon(ids, old_shop_id, new_shop_item_at_coupon, coupon_index, esHelper):

    coupon_docs_for_all_field = coupon_service.get_coupon_docs_for_all_field_by_ids(ids, coupon_index, esHelper)

    if coupon_docs_for_all_field is None or coupon_docs_for_all_field == []:
        print "coupon_docs_for_all_field : " + str(coupon_docs_for_all_field)
        return

    for coupon_doc in coupon_docs_for_all_field:
        if coupon_doc['_source']['shop_count'] <= 0:
            continue

        shop_list_at_coupon = coupon_doc['_source']['shop_list']

        for i in range(len(shop_list_at_coupon)):
            shop_item = shop_list_at_coupon[i]
            if shop_item['unique_shop_id'] == old_shop_id:
                shop_list_at_coupon[i] = new_shop_item_at_coupon

    esHelper.insertBatch(coupon_docs_for_all_field)


def merge_2_shop(shop_doc1_all_field, shop_doc2_all_field, shop_index, esHelper):
    if shop_doc2_all_field['_source']['coupon_count'] == 0:
        esHelper.delete(shop_index, shop_index, shop_doc2_all_field['_id'])
        return

    coupon_list1 = shop_doc1_all_field['_source']['coupon_list']

    coupon_list2 = shop_doc2_all_field['_source']['coupon_list']

    deal_ids = []

    for coupon_item in coupon_list2:
        if coupon_item not in coupon_list1:
            coupon_list1.append(coupon_item)
            deal_ids.append(coupon_item['unique_coupon_id'])

    shop_doc1_all_field['_source']['coupon_count'] = len(coupon_list1)
    shop_doc1_all_field['_source']['coupon_list'] = coupon_list1

    esHelper.index(shop_index, shop_index, shop_doc1_all_field['_id'], shop_doc1_all_field['_source'])

    esHelper.delete(shop_index, shop_index, shop_doc2_all_field['_id'])

    return deal_ids


def merge_shop_for_docs(shop_docs, city_code, shop_index, esHelper):
    if shop_docs is None:
        return None

    coupon_index = com_service.get_coupon_index_by_city_code(city_code)

    del_shop_ids = []

    shop_docs_size = len(shop_docs)

    for i in range(shop_docs_size):

        if (i + 1 >= shop_docs_size):
            break

        shop_doc1 = shop_docs[i]

        if shop_doc1['_id'] in del_shop_ids:
            continue

        for j in range(i + 1, shop_docs_size, 1):

            shop_doc2 = shop_docs[j]

            if shop_doc2['_id'] in del_shop_ids:
                continue

            if (is_same_shop(shop_doc1, shop_doc2)):

                #print "----same shop----"

                unique_shop_id_1 = shop_doc1['_id']

                unique_shop_id_2 = shop_doc2['_id']

                shop_doc1_all_field = esHelper.get(shop_index, shop_index, unique_shop_id_1)
                shop_doc2_all_field = esHelper.get(shop_index, shop_index, unique_shop_id_2)

                if (shop_doc2_all_field['_source']['shop_source'] < shop_doc1_all_field['_source']['shop_source']):
                    tmp = shop_doc1_all_field
                    shop_doc1_all_field = shop_doc2_all_field
                    shop_doc2_all_field = tmp

                deal_coupon_ids = merge_2_shop(shop_doc1_all_field=shop_doc1_all_field,
                                               shop_doc2_all_field=shop_doc2_all_field, shop_index=shop_index,
                                               esHelper=esHelper)

                old_shop_id = shop_doc2_all_field['_id']

                del_shop_ids.append(old_shop_id) ## add it list

                new_shop_id = shop_doc1_all_field['_id']

                new_shop_item_at_coupon = {
                    "unique_shop_id": new_shop_id,
                    "shop_address": shop_doc1_all_field['_source']['shop_address'],
                    "shop_position": shop_doc1_all_field['_source']['shop_position']
                }

                print "old_shop_id : %s, old_shop_name: %s, old_shop_address: %s, new_shop_id : %s, new_shop_name: %s, new_shop_address: %s" % (old_shop_id, shop_doc2_all_field['_source']['shop_name'], shop_doc2_all_field['_source']['shop_address'],  new_shop_id, shop_doc1_all_field['_source']['shop_name'], shop_doc1_all_field['_source']['shop_address'])

                merge_2_shop_for_coupon(deal_coupon_ids, old_shop_id, new_shop_item_at_coupon, coupon_index, esHelper)

                try:
                    get_merge_shop_doc(old_shop_id, shop_doc2_all_field['_source']['shop_name'],shop_doc2_all_field['_source']['shop_address'], new_shop_id,shop_doc1_all_field['_source']['shop_name'],shop_doc1_all_field['_source']['shop_address'])
                except BaseException, e:
                    print 'Merge city errors', e

                if (shop_doc1['_id'] in del_shop_ids):

                    break




from datetime import datetime
import constant


def insert_merge_shop(old_shop_id, old_shop_name, old_shop_address,new_shop_id, new_shop_name, new_shop_address,merge_shop_record_index, EsHelper):
    create_time = datetime.now()
    create_time_str = create_time.strftime(constant.YMD_HMS)


    doc = get_merge_shop_doc(old_shop_id, old_shop_name, old_shop_address,new_shop_id, new_shop_name, new_shop_address)

    if doc is None:
        return

    _id = str(new_shop_id) + '_' + str(old_shop_id) + '_' + create_time_str

    EsHelper.index(index=merge_shop_record_index, doc_type=merge_shop_record_index, id=_id, data=doc)


def get_merge_shop_doc(old_shop_id, old_shop_name, old_shop_address,new_shop_id, new_shop_name, new_shop_address):
    doc = None
    create_time = datetime.now()
    create_time_str = create_time.strftime(constant.YMD_HMS)

    try:
        doc = {

            "old_shop_id": old_shop_id,
            "old_shop_name": old_shop_name,
            "old_shop_address": old_shop_address,

            "new_shop_id": new_shop_id,
            "new_shop_name": new_shop_name,
            "new_shop_ address": new_shop_address,

            "new_shop_for_coupon_platform_count": 0,

            "create_time_str": create_time_str,
            "modify_time_str": create_time_str,
            "status": 0
        }

    except BaseException, e:
        print 'merger shop insert errors'
    finally:
        pass


    return doc
