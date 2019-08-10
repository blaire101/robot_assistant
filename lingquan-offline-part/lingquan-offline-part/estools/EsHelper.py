from elasticsearch import Elasticsearch
from elasticsearch import helpers

import constant


class EsHelper:
    def __init__(self, host, port, user, password):
        es_url = 'http://' + user + ":" + password + '@' + host + ':' + str(port)
        self.es = Elasticsearch([es_url])

    def __init__(self, es_http_url):
        self.es = Elasticsearch([es_http_url])

    def insert(self, index, doc_type, data):
        res = None
        try:
            res = self.es.index(index=index, doc_type=doc_type, body=data)
        except BaseException, e:
            print("es.insert Exist Error ! index : %s, info: %s", index, str(e))
        finally:
            pass
        return res

    def index(self, index, doc_type, id, data):
        res = None
        try:
            res = self.es.index(index=index, doc_type=doc_type, id=id, body=data)
        except BaseException, e:
            print("es.insert Exist Error ! index : %s, info: %s", index, str(e))
        finally:
            pass
        return res

        # print 'insert success!'

    def insertBatch(self, dataList):
        batchSize = 1000
        while (len(dataList) > batchSize):
            helpers.bulk(self.es, dataList)
            del dataList[0:batchSize]

        if (len(dataList) > 0):
            helpers.bulk(self.es, dataList)
            del dataList[0:len(dataList)]
            # print 'insertBatch success!'

    def get(self, index, doc_type, id):
        res = None
        try:
            res = self.es.get(index=index, doc_type=doc_type, id=id)
        except BaseException, e:
            print("es.get Exist Error ! index : %s, info: %s", index, str(e))
        finally:
            pass
        return res

    def search(self, index, doc_type, body):
        res = None
        try:
            res = self.es.search(index=index, doc_type=doc_type, body=body)
        except BaseException, e:
            print("es.search Exist Error ! index : %s, info: %s", index, str(e))
        finally:
            pass
        return res

    def search_hits(self, index, doc_type, body):
        res = None
        try:
            res = self.es.search(index=index, doc_type=doc_type, body=body)['hits']
        except BaseException, e:
            print("es.search Exist Error ! index : %s, info: %s", index, str(e))
        finally:
            pass
        return res

    def search_hits_hits(self, index, doc_type, body):
        res = None
        try:
            res = self.es.search(index=index, doc_type=doc_type, body=body)['hits']['hits']
        except BaseException, e:
            print("es.search Exist Error ! index : %s, info: %s", index, str(e))
        finally:
            pass
        return res

    def search_aggs(self, index, doc_type, body):
        res = None
        try:
            res = self.es.search(index=index, doc_type=doc_type, body=body)['aggregations']
        except BaseException, e:
            print("es.search Exist Error ! index : %s, info: %s", index, str(e))
        finally:
            pass
        return res

    def delete(self, index, doc_type, id):
        res = None
        try:
            res = self.es.delete(index, doc_type, id)
        except BaseException, e:
            print("es.delete id Exist Error ! index : %s, info: %s", index, str(e))
        finally:
            pass
        return res

    def delete_by_query(self, index, doc_type, body):
        res = None
        try:
            res = self.es.delete_by_query(index, doc_type, body)
        except BaseException, e:
            print("es.delete_by_query Exist Error ! index : %s, info: %s", index, str(e))
        finally:
            pass
        return res


if __name__ == '__main__':
    esHelper = EsHelper(constant.ES_HOST, constant.ES_PORT, constant.ES_USERNAME, constant.ES_PASSWORD)

    index_v = "seek_third_landmark_hangzhou"
    doc_type_v = "seek_third_landmark_hangzhou"
    query = {
        "query": {
            "bool": {
                "must": [{
                    "term": {
                        "deleted": 0
                    }
                }],
                "must_not": [],
                "should": []
            }
        }
    }
    res = esHelper.search_hits_hits(index_v, doc_type_v, query)
    print res
