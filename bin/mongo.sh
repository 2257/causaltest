time=$(date "+%Y-%m-%d %H:%M:%S")
echo "${time}"

ssh  zhaole@xuserver002 << remotessh
ps -ef | grep mongo | grep -v grep |awk '{print \$2}'|xargs  kill -9
echo -e "\033[32m xuserver002 mongod is stopped \033[0m"
sleep 20
rm /data/zhaole/mon/data/mongod.lock
exit
remotessh
#ssh  zhaole@xuserver002 << remotessh
#/data/zhaole/mon/bin/mongod -f /data/zhaole/mon/conf/mongo.conf
#exit
#remotessh
#echo -e "\033[32m xuserver002 mongod is started \033[0m"

