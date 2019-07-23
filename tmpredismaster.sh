#!/bin/bash
#ps -ef | grep 172.24.83.25:7001 | grep -v grep | awk '{print $2}' |xargs kill -9
#ssh -Tq zhaole@xuserver002 << remotessh
#ps -ef | grep 172.24.83.27:7003 | grep -v grep | awk '{print \$2}' |xargs kill -9
#exit
#remotessh
#ssh -Tq zhaole@xuserver003 << remotessh
#ps -ef | grep 172.24.83.26:7005 | grep -v grep | awk '{print \$2}' |xargs kill 
#-9
#exit
#remotessh
#echo -e "\033[32m master nodes are stopped \033[0m"
#sleep 15
/data/zhaole/redis-cluster/redis-server /data/zhaole/redis-cluster/7001/redis.conf
ssh -Tq zhaole@xuserver002 << remotessh
/data/zhaole/redis-cluster/redis-server /data/zhaole/redis-cluster/7003/redis.conf
exit
remotessh
ssh -Tq zhaole@xuserver003 << remotessh
/data/zhaole/redis-cluster/redis-server /data/zhaole/redis-cluster/7005/redis.conf
exit
remotessh
echo -e "\033[32m master nodes are started \033[0m"
