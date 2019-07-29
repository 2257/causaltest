ssh  zhaole@xuserver002 << remotessh
ps -ef | grep cassandra | grep -v grep |awk '{print \$2}'|xargs  kill -9
echo -e "\033[32m xuserver002 cassandra is stopped \033[0m"
sleep 40
exit
remotessh
#ssh  zhaole@xuserver002 << remotessh
#nohup /data/zhaole/apache-cassandra-3.11.2/bin/cassandra -f &
#exit
#remotessh
#echo -e "\033[32m xuserver002 cassandra is started \033[0m"

