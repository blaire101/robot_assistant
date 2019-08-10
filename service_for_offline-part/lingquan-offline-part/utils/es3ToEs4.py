# -*- coding: UTF-8 -*-
'''
@author: Blair
'''

import sys

from datetime import datetime

'''estools'''
from estools.EsHelper import ESHelper

def esToes(esHelper1, index1, esHelper2, index2):

    body = {
        "from": 0,
        "size": 10000,
        "query": {
            "filtered": {
                "query": {
                    "bool": {
                        "must": [
                        ],
                        "must_not": [
                        ],
                        "should": [
                        ]
                    }
                }
            }
        },
        "_source": {
            "includes": [
            ],
            "excludes": []
        }
    }

    docs1 = esHelper1.search_hits_hits(index1, index1, body)

    for doc1 in docs1:
        doc1['_index'] = index2
        doc1['_type'] = index2

    #for doc1 in docs1:
    #print docs1

    #esHelper2.insertBatch(docs1)

if __name__ == '__main__':

    if (len(sys.argv) < 3):
        print "len(sys.argv) < 3"
        sys.exit(1)

    index1 = sys.argv[1]

    ES_HOST1 = '10.28.147.225'
    ES_PORT1 = 9200
    ES_USERNAME1 = 'es_admin2'
    ES_PASSWORD1 = '******'

    esHelper1 = ESHelper(ES_HOST1, ES_PORT1, ES_USERNAME1, ES_PASSWORD1)

    index2 = sys.argv[2]

    ES_HOST2 = '10.28.13.243'
    ES_PORT2 = 9200
    ES_USERNAME2 = 'es_cluster1_admin6'
    ES_PASSWORD2 = '******'

    esHelper2 = ESHelper(ES_HOST2, ES_PORT2, ES_USERNAME2, ES_PASSWORD2)

    esToes(esHelper1, index1, esHelper2, index2)


    print "end"