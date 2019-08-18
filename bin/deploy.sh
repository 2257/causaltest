#!/bin/bash
script_dir=$(cd $(dirname ${BASH_SOURCE[0]}); pwd)
#step 1 ,delete the context in server.xml 
sed -i "/causalwebserver/d" $CATALINA_HOME/conf/server.xml
cd $CATALINA_HOME/bin
#step 2 , stop the runnig tomcat
PID=$(ps -fu `whoami`|grep tomcat|grep -v grep|awk '{print $2}')
if [ -z "$PID" ];then
	echo -e "\033[4;32m [$(date +'%F %T')] >>> [There is no tomcat running.] \033[0m"
else
	echo -e "\033[4;32m [$(date +'%F %T')] >>> [Tomcat begin to restart.] \033[0m"
	$CATALINA_HOME/bin/shutdown.sh
	pidList=$(ps aux | grep $CATALINA_HOME | grep -v grep | awk '{print $2}')
	for pid in $pidList; do
       		kill -9 $pid
        	echo -e "\033[4;32m [$(date +'%F %T')] >>> [Kill the process [$pid] successfully.] \033[0m"
	done
	sleep 1
fi
#step 3 ,start tomcat
$CATALINA_HOME/bin/startup.sh
echo -e "\033[4;32m [$(date +'%F %T')] >>> [Tomcat restart complete.] \033[0m"
#sleep 1
#step 4 , build maven
cd $script_dir/../causalwebserver
mvn clean
mvn install
mvn tomcat7:deploy
#step 5 , update server.xml and add context conf
function getline(){
	sed -n -e '/unpackWARs/=' $CATALINA_HOME/conf/server.xml
}
line=$(getline)
sed -i '/unpackWARs/a\<Context path=\"\" docBase=\"causalwebserver\" reloadable=\"true\"\/>' $CATALINA_HOME/conf/server.xml

$CATALINA_HOME/bin/shutdown.sh
pidList=$(ps aux | grep $CATALINA_HOME | grep -v grep | awk '{print $2}')
for pid in $pidList; do
	kill -9 $pid
	echo -e "\033[4;32m [$(date +'%F %T')] >>> [Kill the process [$pid] successfully.] \033[0m"
	done
sleep 1
$CATALINA_HOME/bin/startup.sh
echo -e "\033[4;32m [$(date +'%F %T')] >>> [Tomcat reload complete.] \033[0m"


echo -e "\033[32m  ----------------------------------------------\033[0m"
echo -e "\033[32m | [$(date +'%F %T')] >>> [Deploy finished.] | \033[0m"
echo -e "\033[32m  ----------------------------------------------\033[0m"


