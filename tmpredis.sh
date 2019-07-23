#!/bin/bash
#ps -ef | grep 172.24.83.25:7002 | grep -v grep | awk '{print $2}' |xargs kill -9
#ssh -Tq zhaole@xuserver002 << remotessh
#ps -ef | grep 172.24.83.27:7004 | grep -v grep | awk '{print \$2}' |xargs kill -9
#exit
#remotessh
#ssh -Tq zhaole@xuserver003 << remotessh
#ps -ef | grep 172.24.83.26:7009 | grep -v grep | awk '{print \$2}' |xargs kill 
#-9
#exit
#remotessh
#echo -e "\033[32m master nodes are stopped \033[0m"
#sleep 20
/data/zhaole/redis-cluster/redis-server /data/zhaole/redis-cluster/7002/redis.conf
ssh -Tq zhaole@xuserver002 << remotessh
/data/zhaole/redis-cluster/redis-server /data/zhaole/redis-cluster/7004/redis.conf
exit
remotessh
ssh -Tq zhaole@xuserver003 << remotessh
/data/zhaole/redis-cluster/redis-server /data/zhaole/redis-cluster/7009/redis.conf
exit
remotessh
echo -e "\033[32m master nodes are started \033[0m"
