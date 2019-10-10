time=$(date "+%Y-%m-%d %H:%M:%S")
echo "${time}"

ps -ef | grep cassandra | grep -v grep |awk '{print $2}'|xargs  kill -9
#sleep 40
echo -e "\033[32m xuserver001 cassandra is stopped \033[0m"
#nohup /data/zhaole/apache-cassandra-3.11.2/bin/cassandra -f &
#echo -e "\033[32m xuserver001 cassandra is started \033[0m"

