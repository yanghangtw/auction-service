# Auction Service

本地启动需要准备MySQL
```shell
docker -e MYSQL_ROOT_PASSWORD=123456 -e MYSQL_DATABASE=auction_service -p 3306:3306 mysql:8.0
```

运行测试需要本地安装Docker，因为测试使用了`TestContainers`

实现了一个Stroy，部分组件创建了占位的空文件
