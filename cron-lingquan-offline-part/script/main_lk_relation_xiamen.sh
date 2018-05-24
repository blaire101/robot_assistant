#!/bin/bash
###############################################################################
#                                                                             
# @date:   2017.07.06
# @desc:   main_lk_relation
#                                                                            
############################################################################### 

cd `dirname $0`/.. && wk_dir=`pwd` && cd -
source ${wk_dir}/util/env

${my_python} ${main_path}/main_lk_relation.py ${xiamen}

check_success

exit 0
