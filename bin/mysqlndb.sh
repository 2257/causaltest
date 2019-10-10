time=$(date "+%Y-%m-%d %H:%M:%S")
echo "${time}"

ssh  zhaole@xuserver003 << remotessh
ps -ef | grep ndbd | grep -v grep |awk '{print \$2}'|xargs  kill -9
echo -e "\033[32m xuserver003 mysql datanode is stopped \033[0m"
sleep 20
ndbd --defaults-file=/data/zhaole/mysql/my.cnf --initial
echo -e "\033[32m xuserver003 mysql datanode is started \033[0m"
exit
remotessh
