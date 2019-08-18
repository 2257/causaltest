#!/bin/bash
echo -e "\033[32m  -------------------------------------------------\033[0m"
echo -e "\033[32m | [$(date +'%F %T')] >>> [ReDeploying......] | \033[0m"
echo -e "\033[32m  -------------------------------------------------\033[0m"

script_dir=$(cd $(dirname ${BASH_SOURCE[0]}); pwd)
cd ${script_dir}/../causalwebserver
mvn tomcat7:redeploy -q
$CATALINA_HOME/bin/shutdown.sh
pidList=$(ps aux | grep $CATALINA_HOME | grep -v grep | awk '{print $2}')
for pid in $pidList; do
         kill -9 $pid
done
$CATALINA_HOME/bin/startup.sh
echo -e "\033[32m  ----------------------------------------------------\033[0m"
echo -e "\033[32m | [$(date +'%F %T')] >>> [ReDeploy successfully.] | \033[0m"
echo -e "\033[32m  ----------------------------------------------------\033[0m"


