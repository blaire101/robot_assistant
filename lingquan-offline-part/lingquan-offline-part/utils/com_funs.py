# encoding: utf-8
'''
Created on 2017年6月25日 下午2:36:13

@author: Blair Chan
'''

import urllib2
from xml.dom.minidom import parseString


def get_avg_price(price):
    res = price
    if (price.startswith("￥")):
        res =  price[len("￥"):]
    return float(res)

def get_valid_addr(addr):
    if (addr is None):
        return None
    pos = str(addr).strip().find(" ")
    if (pos == -1):
        return addr
    return addr[0:pos].strip()

def get_location(addr):
    if (addr is None):
        return None
    loc_cache = {}
    key = 'fiOr77tcXpC6lPnLsz82BEpADZ3uipjy'
    doc = urllib2.urlopen('http://api.map.baidu.com/geocoder?address=%s&output=xml&key=%s' % ('杭州' + addr, key))
    dom = parseString(doc.read())
    lat = dom.getElementsByTagName('lat')[0].firstChild.data
    lng = dom.getElementsByTagName('lng')[0].firstChild.data
    return map(float, (lng, lat))

## Main Test

if __name__ == '__main__':

    # print get_location("万象城")
    # print get_location("打铁关")
    # print get_location("闸弄口")
    # print get_location("火车东站")
    # print get_location("彭埠")
    print "========================"
    # print get_location("唐昌镇")
    # print get_location("菜场镇")
    # print get_location("金堂县")
    # print get_location("海螺乡")
    # print get_location("富阳客运站")
    print "========================"


