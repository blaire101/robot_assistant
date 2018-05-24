# encoding: utf-8
'''
Created on 2017年9月5日

@author: Blair Chan
'''

from constant import LANDMARK_START_NUM
from constant import LANDMARK_SIZE_LIMIT_MAX

def get_landmark_hits(landmark_index, landmark_doc_type, esHelper):
    query = {
        "from": LANDMARK_START_NUM,
        "size": LANDMARK_SIZE_LIMIT_MAX,
        "query": {
            "bool": {
                "must": [],
                "must_not": [
                    {
                        "term": {
                            "status": 1
                        }
                    }
                ],
                "should": []
            }
        },
        "sort": [
            {
                "landmark_id": {
                    "order": "asc"
                }
            }
        ]
    }

    landmark_docs = esHelper.search_hits_hits(index=landmark_index, doc_type=landmark_doc_type, body=query)
    return landmark_docs
