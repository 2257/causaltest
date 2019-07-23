#!/bin/bash
echo "[$(date +'%F %T')] >>> Tomcat begin to restart."
$CATALINA_HOME/bin/shutdown.sh
pidList=$(ps aux | grep $CATALINA_HOME | grep -v grep | awk '{print $2}')
for pid in $pidList; do
	kill -9 $pid
	echo "[$(date +'%F %T')] >>> Kill the process [$pid] successfully."
done
$CATALINA_HOME/bin/startup.sh
echo "[$(date +'%F %T')] >>> Tomcat restart complete."
