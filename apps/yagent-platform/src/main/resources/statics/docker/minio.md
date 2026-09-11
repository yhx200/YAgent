先建一个目录，比如:\
D:\minio\data\
然后 PowerShell 执行：\
docker run -d `\
  -p 9000:9000 `\
  -p 9001:9001 `\
  --name yagent-minio `\
  -v D:\minio\data:/data `\
  -e "MINIO_ROOT_USER=yagentadmin" `\
  -e "MINIO_ROOT_PASSWORD=YAgent12345678" `\
  quay.io/minio/minio server /data --console-address ":9001"\
  
MinIO 官方仓库目前仍提供这种容器启动方式；9000 是 S3 API，9001 是管理 Console。\
然后浏览器打开：\
http://localhost:9001\
