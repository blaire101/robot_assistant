# encoding: utf-8

import constant
import math

def get_0_range_distance(distance_tag):

    start_distance = "0km"
    end_distance = "0km"

    if (distance_tag == constant.DIS_TAG_LIST[0]):
        # start_distance = constant.DIS_STR_0
        end_distance = constant.DIS_STR_500
    elif (distance_tag == constant.DIS_TAG_LIST[1]):
        # start_distance = constant.DIS_STR_500
        end_distance = constant.DIS_STR_1000
    elif (distance_tag == constant.DIS_TAG_LIST[2]):
        # start_distance = constant.DIS_STR_1000
        end_distance = constant.DIS_STR_2000
    elif (distance_tag == constant.DIS_TAG_LIST[3]):
        # start_distance = constant.DIS_STR_2000
        end_distance = constant.DIS_STR_5000
    elif (distance_tag == constant.DIS_TAG_LIST[4]):
        # start_distance = constant.DIS_STR_5000
        end_distance = constant.DIS_STR_7000
    elif (distance_tag == constant.DIS_TAG_LIST[5]):
        # start_distance = constant.DIS_STR_7000
        end_distance = constant.DIS_STR_15000

    return [start_distance, end_distance]

def get_range_distance(distance_tag):

    start_distance = "0km"
    end_distance = "0km"

    if (distance_tag == constant.DIS_TAG_LIST[0]):
        start_distance = constant.DIS_STR_0
        end_distance = constant.DIS_STR_500
    elif (distance_tag == constant.DIS_TAG_LIST[1]):
        start_distance = constant.DIS_STR_500
        end_distance = constant.DIS_STR_1000
    elif (distance_tag == constant.DIS_TAG_LIST[2]):
        start_distance = constant.DIS_STR_1000
        end_distance = constant.DIS_STR_2000
    elif (distance_tag == constant.DIS_TAG_LIST[3]):
        start_distance = constant.DIS_STR_2000
        end_distance = constant.DIS_STR_5000
    elif (distance_tag == constant.DIS_TAG_LIST[4]):
        start_distance = constant.DIS_STR_5000
        end_distance = constant.DIS_STR_7000
    elif (distance_tag == constant.DIS_TAG_LIST[5]):
        start_distance = constant.DIS_STR_7000
        end_distance = constant.DIS_STR_15000

    return [start_distance, end_distance]

def is_valid_geo_point(geo_point):
    if (geo_point is None or geo_point == '' or len(geo_point) != 2):
        return False
    return True

def is_valid_docs(docs):
    if (docs is None or len(docs) == 0):
        print "is_invalid_docs"
        return False
    return True


######################

def get_landmark_index_by_city_code(city_code):
    landmark_index = constant.INDEX_LANDMARK_PREFIX + city_code
    return landmark_index

def get_coupon_index_by_city_code(city_code):
    coupon_index = constant.INDEX_COUPON_PREFIX + city_code
    return coupon_index

def get_shop_index_by_city_code(city_code):
    shop_index = constant.INDEX_SHOP_PREFIX + city_code
    return shop_index

def get_landmark_shop_coupon_index_by_city_code(city_code):
    landmark_coupon_shop_index = constant.INDEX_LANDMARK_SHOP_COUPON_PREFIX + city_code
    return landmark_coupon_shop_index

def get_shopping_index_by_city_code(city_code):
    shopping_index = constant.INDEX_SHOPPING_PREFIX + city_code
    return shopping_index

def get_landmark_shopping_index_by_city_code(city_code):
    landmark_shopping_index = constant.INDEX_LANDMARK_SHOPPING_PREFIX + city_code
    return landmark_shopping_index


######################

def is_valid_shop_index(shop_index):
    if (shop_index not in constant.INDEX_SHOP_LIST):
        print "shop_index is not in INDEX_SHOP_LIST,  shop_index : %s" % shop_index
        return False
    return True
def is_valid_coupon_index(coupon_index):
    if (coupon_index not in constant.INDEX_COUPON_LIST):
        print "coupon_index is not in INDEX_COUPON_LIST,  coupon_index : %s" % coupon_index
        return False
    return True

def get_city_code(index_name):
    if (index_name is None):
        return None
    tag = str(index_name).rfind("_")
    if (tag == -1):
        return None
    return index_name[tag:]

def get_valid_shop_name(shop_name):
    valid_shop_name = shop_name
    tag = shop_name.find("(")
    if (tag == -1):
        tag = shop_name.find("（")
    if (tag == -1):
        tag = shop_name.find("[")
    if (tag != -1):
        return shop_name[0:tag]
    return valid_shop_name

## from network
def rad(d):
    return d * 3.1415926 / 180.0

def get_distance(geo1, geo2):

    lng1 = geo1[0]
    lat1 = geo1[1]

    lng2 = geo2[0]
    lat2 = geo2[1]

    radlat1 = rad(lat1)
    radlat2 = rad(lat2)
    a = radlat1 - radlat2
    b = rad(lng1) - rad(lng2)
    s = 2 * math.asin(
        math.sqrt(math.pow(math.sin(a / 2), 2) + math.cos(radlat1) * math.cos(radlat2) * math.pow(math.sin(b / 2), 2)))
    earth_radius = 6378.137
    s = s * earth_radius
    if s < 0:
        return -s
    else:
        return s

if __name__ == '__main__':

    geo1 = [120.21583557128906, 30.2501678466]
    geo2 = [120.221328735, 30.2501678466]
    print get_distance(geo1, geo2)

    geo1 = [120.171890258789, 30.259780883]
    geo2 = [120.171890258789, 30.2570343017]
    print get_distance(geo1, geo2)

    geo1 = [120.1663970947, 30.259780883]
    geo2 = [120.171890258789, 30.2570343017]
    print get_distance(geo1, geo2)
