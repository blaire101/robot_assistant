#!/bin/bash
###############################################################################
#                                                                             
# @date:   2017.07.06
#                                                                            
############################################################################### 

cd `dirname $0`/.. && wk_dir=`pwd` && cd -
source ${wk_dir}/util/env

${my_python} ${main_path}/main_lk_shopping.py ${hangzhou}
echo_ex "${hangzhou} run shopping end !"

${my_python} ${main_path}/main_lk_shopping.py ${shanghai}
echo_ex "${shanghai} run shopping end !"

${my_python} ${main_path}/main_lk_shopping.py ${taizhou}
echo_ex "${taizhou} run shopping end !"


${my_python} ${main_path}/main_lk_shopping.py ${qingdao}
echo_ex "${qingdao} run shopping end !"

${my_python} ${main_path}/main_lk_shopping.py ${xiamen}
echo_ex "${xiamen} run shopping end !"


${my_python} ${main_path}/main_lk_shopping.py ${wuhan}
echo_ex "${wuhan} run shopping end !"

${my_python} ${main_path}/main_lk_shopping.py ${chengdu}
echo_ex "${chengdu} run shopping end !"


check_success

exit 0


