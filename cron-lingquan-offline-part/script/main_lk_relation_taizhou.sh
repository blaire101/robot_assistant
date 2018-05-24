#!/bin/bash
###############################################################################
#                                                                             
# @date:   2017.09.30
# @desc:   main_lk_relation
#                                                                            
############################################################################### 

cd `dirname $0`/.. && wk_dir=`pwd` && cd -
source ${wk_dir}/util/env

${my_python} ${main_path}/main_lk_relation.py ${taizhou}

check_success

exit 0
