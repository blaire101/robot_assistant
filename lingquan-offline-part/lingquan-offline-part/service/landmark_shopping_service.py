# encoding: utf-8
'''
Created on 2017年7月28日

@author: Blair Chan
'''

from datetime import datetime

import constant

def insert_lankmark_shopping(lk_doc, dis_tag, all_shopping_count, shopping_count, shopping_list, landmark_shopping_index, esHelper):


    doc = get_lk_shopping_doc(lk_doc, dis_tag, all_shopping_count, shopping_count, shopping_list)

    if doc is None:
        return

    _id = lk_doc['_id'] + '_' + str(dis_tag)

    esHelper.index(index=landmark_shopping_index, doc_type=landmark_shopping_index, id=_id, data=doc)


def get_lk_shopping_doc(lk_doc, dis_tag, all_shopping_count, shopping_count, shopping_list):

    doc = None

    create_time = datetime.now()
    create_time_str = create_time.strftime(constant.YMD_HMS)

    try:
        doc = {
            "unique_landmark_id": lk_doc['_id'],
            "landmark_type": lk_doc['_source']['landmark_type'],

            "all_shopping_count": all_shopping_count,
            "shopping_count": shopping_count,

            "shopping_list": shopping_list,

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


