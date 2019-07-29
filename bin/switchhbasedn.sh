time=$(date "+%Y-%m-%d %H:%M:%S")
echo "${time}"

ssh  zhaole@xuserver002 << remotessh
ps -ef | grep hbase | grep -v grep |awk '{print \$2}'|xargs  kill -9
echo -e "\033[32m xuserver002 hbase is stopped \033[0m"
sleep 20
exit
remotessh
/data/zhaole/hbase-1.2.6/bin/start-hbase.sh
#ssh  zhaole@xuserver002 << remotessh
#/data/zhaole/mon/bin/mongod -f /data/zhaole/mon/conf/mongo.conf
#exit
#remotessh
#echo -e "\033[32m xuserver002 mongod is started \033[0m"

