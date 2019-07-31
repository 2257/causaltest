# You should create a cluster in https://cloud.mongodb.com and
# apply API in https://docs.atlas.mongodb.com/reference/api/processes-get-one/
# to get your specific node host address. And then replace the address in these commands.

sudo  iptables -I INPUT -s caulsaltest-shard-00-02-ocqgv.mongodb.net -j DROP
sleep 20
sudo iptables -D INPUT -s caulsaltest-shard-00-02-ocqgv.mongodb.net -j DROP