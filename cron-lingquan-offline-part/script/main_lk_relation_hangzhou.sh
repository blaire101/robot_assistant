#!/bin/bash
###############################################################################
#                                                                             
# @date:   2017.07.03
#                                                                            
############################################################################### 

cd `dirname $0`/.. && wk_dir=`pwd` && cd -
source ${wk_dir}/util/env

echo_ex "${hangzhou}"
echo ${my_python}
${my_python} ${main_path}/main_lk_relation.py ${hangzhou}

check_success

exit 0
