ssh  zhaole@xuserver002 << remotessh
ps -ef | grep coordinator | grep -v grep |awk '{print \$2}'|xargs  kill -9
rm /data/zhaole/pgxl/data/nodes/dn_master/postmaster.pid
exit
remotessh
echo -e "\033[32m xuserver002 postgres datanode is stopped \033[0m"
sleep 20
/data/zhaole/pgxl/bin/pgxc_ctl start coordinator coord1
sleep 10
/data/zhaole/pgxl/bin/pgxc_ctl start datanode datanode1
echo -e "\033[32m xuserver002  postgres datanode is started \033[0m"
