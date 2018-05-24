# -*- coding: UTF-8 -*-
'''
@author: Blair
'''

COUPON_START_NUM = 0
COUPON_SIZE_LIMIT = 300
COUPON_SIZE_4_LIMIT = 100

SHOP_START_NUM = 0
SHOP_SIZE_PER_SEARCH_LIMIT = 100

SHOP_SIZE_LIMIT = 300
SHOP_SIZE_4_LIMIT = 100

MIN_START = 0
MAX_SIZE = 10000

LANDMARK_START_NUM = 0
LANDMARK_SIZE_LIMIT_MAX = 20000

## Dis Str

DIS_STR_0 = "0km"
DIS_STR_500 = "0.5km"
DIS_STR_1000 = "1km"
DIS_STR_2000 = "2km"
DIS_STR_5000 = "5km"
DIS_STR_7000 = "7km"
DIS_STR_15000 = "15km"

## Dis Tag

DIS_TAG_LIST = [1, 2, 3, 4, 5, 6]

## five kinds Index Prefix
INDEX_COUPON_PREFIX = "pre_seek0_coupon_"
INDEX_SHOP_PREFIX = "pre_seek0_shop_"
INDEX_LANDMARK_PREFIX = "pre_seek0_landmark_"

INDEX_LANDMARK_SHOP_COUPON_PREFIX = "pre_seek0_landmark_shop_coupon_"

YMD_HMS = "%Y-%m-%d %H:%M:%S"

### five kinds Index Name List ###

# coupon
INDEX_COUPON_LIST = [
    "pre_seek0_coupon_hangzhou",
    "pre_seek0_coupon_shanghai",
    "pre_seek0_coupon_qingdao",
    "pre_seek0_coupon_xiamen",
    "pre_seek0_coupon_wuhan",
    "pre_seek0_coupon_chengdu",
    "pre_seek0_coupon_taizhou"
]

# shop
INDEX_SHOP_LIST = [
    "pre_seek0_shop_hangzhou",
    "pre_seek0_shop_shanghai",
    "pre_seek0_shop_qingdao",
    "pre_seek0_shop_xiamen",
    "pre_seek0_shop_wuhan",
    "pre_seek0_shop_chengdu",
    "pre_seek0_shop_taizhou"
]

# landmark
INDEX_LANDMARK_LIST = [
    "pre_seek0_landmark_hangzhou",
    "pre_seek0_landmark_shanghai",
    "pre_seek0_landmark_qingdao",
    "pre_seek0_landmark_xiamen",
    "pre_seek0_landmark_wuhan",
    "pre_seek0_landmark_chengdu",
    "pre_seek0_landmark_taizhou"
]

# city_code list
CITY_CODE_LIST = [
    "hangzhou",
    "shanghai",
    "qingdao",
    "xiamen",
    "wuhan",
    "chengdu",
    "taizhou"
]

# landmark shop coupon
INDEX_LANDMARK_SHOP_COUPON_LIST = [
    "pre_seek0_landmark_shop_coupon_hangzhou",
    "pre_seek0_landmark_shop_coupon_shanghai",
    "pre_seek0_landmark_shop_coupon_qingdao",
    "pre_seek0_landmark_shop_coupon_xiamen",
    "pre_seek0_landmark_shop_coupon_wuhan",
    "pre_seek0_landmark_shop_coupon_chengdu",
    "pre_seek0_landmark_shop_coupon_taizhou"
]


## merge shop
HZ_CENTER_POINT = [120.19592285, 30.26458740]
SH_CENTER_POINT = [121.44836425, 31.15997314]
QD_CENTER_POINT = [120.47058105, 36.18072509]
XM_CENTER_POINT = [118.13049316, 24.58465576]
WH_CENTER_POINT = [114.34020996, 30.58319091]
CD_CENTER_POINT = [104.07897949, 30.67657470]
TZ_CENTER_POINT = [121.420819, 28.655753]      # 台州市人民政府

CITY_CENTRE_POS_DICT = {
    "hangzhou": HZ_CENTER_POINT,
    "shanghai": SH_CENTER_POINT,
    "qingdao": QD_CENTER_POINT,
    "xiamen": XM_CENTER_POINT,
    "wuhan": WH_CENTER_POINT,
    "chengdu": CD_CENTER_POINT,
    "taizhou": TZ_CENTER_POINT
}

DIS_RANGE_5 = ['0km', '5km']
DIS_RANGE_10 = ['5km', '10km']
DIS_RANGE_15 = ['10km', '15km']
DIS_RANGE_20 = ['15km', '20km']
DIS_RANGE_25 = ['20km', '25km']
DIS_RANGE_30 = ['25km', '30km']
DIS_RANGE_40 = ['30km', '40km']
DIS_RANGE_50 = ['40km', '50km']
DIS_RANGE_70 = ['50km', '70km']

DIS_RANGE_MERGE_SHOP = [DIS_RANGE_5, DIS_RANGE_10, DIS_RANGE_15, DIS_RANGE_20, DIS_RANGE_25, DIS_RANGE_30, DIS_RANGE_40,
                        DIS_RANGE_50, DIS_RANGE_70]
