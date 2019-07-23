ssh -Tq zhaole@xuserver002 << remotessh
ps -ef | grep hbase | grep -v grep | awk '{print $2}' |xargs kill -9
exit
remotessh
echo -e "\033[32m xuserver002 hbase server are stopped \033[0m"

sleep 20
/data/zhaole/zookeeper-3.4.10/bin/zkServer.sh start
/data/zhaole/hbase-1.2.6/bin/start-hbase.sh
echo -e "\033[32m xuserver002 hbase server are started \033[0m"

