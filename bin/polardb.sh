curl https://polardb.aliyuncs.com/?Action=DeleteDBNodes&DBClusterId=pc-xxxxxxxxxx&DBNodeId.N=pi-xxxxxxxxxx&<[公共请求参数]>

sleep 20
curl https://polardb.aliyuncs.com/?Action=RestartDBNode&DBClusterId=pc-xxxxxxxxxx&DBNodeId=pi-xxxxxxxxxx&<[公共请求参数]>

# https://help.aliyun.com/document_detail/116037.html?spm=a2c4g.11186623.2.27.1fe550eb0vaynt#reference-187257