# <p align='center'>nginx日志监控</p>

<div align="center">

![build status](https://travis-ci.org/fenlan/storm-nginx-log.svg?branch=master)
[![codebeat badge](https://codebeat.co/badges/cbf116f9-877c-420c-b8c1-1414beb9917a)](https://codebeat.co/projects/github-com-fenlan-storm-nginx-log-master)
![size](https://github-size-badge.herokuapp.com/fenlan/storm-nginx-log.svg)
![platform](https://img.shields.io/badge/platform-Linux-orange.svg)
![progress](http://progressed.io/bar/19?title=completed)

</div>

## 简单介绍
项目基于`Kafka` `storm`的实时nginx日志监控，通过读取nginx的日志文件`access.log`来收集nginx服务器的状态，并在一定时间内，统计访问ip的国家地址、指定时间内所有访问次数、访问的状态码、访问的站点、访问者使用的系统、访问者使用的浏览器。

## 实现思路
1. 数据读取
`Kafka`生产者读取`access.log`文件，将新产生的行记录发布给`nginx`topic。`Kafka`消费者订阅`nginx`topic，将记录发送给storm 的spout。

2. 数据存储
storm将日志行记录划分成不同的块，其中包括ip地址块、访问时间块、访问请求信息块、访问状态码快、访问请求返回大小块、访问主机信息块。将每个块存入Redis数据库中。

3. 数据处理
Storm处理包括行记录划分以及Redis数据库数据统计，将统计结果存入Redis数据库，前端Web程序读取Redis数据库的统计结果，通过`Highcharts`工具图表展示。

## Redis 数据库设计
1. `storm`分块结果
![](https://github.com/fenlan/Mycode/blob/master/images/redis.png)

| key | value |
|--------|--------|
|   ip     |   客户端地址     |
|   time   |   访问时间       |
|   request|   请求的url和HTTP协议 |
|   status |   请求状态码     |
|   size   |   返回内容大小   |
|   host   |   客户端系统信息  |

2. `storm`统计信息
> 未完待续

## 安装使用

### 基础环境
- JDK-1.8
- Redis 4+
- IntelliJ IDEA-2017.2
- Nginx

### 下载GeoLite2IP数据库
日志分析客户端ip地址是使用GeoLite数据库查询ip所在城市，统计城市访问量。
- [下载GeoLite2 开源数据库](https://dev.maxmind.com/zh-hans/geoip/geoip2/geolite2-%E5%BC%80%E6%BA%90%E6%95%B0%E6%8D%AE%E5%BA%93/)
- 解压数据库包
- 在项目的配置文件`src/main/resources/application.properties`中配置`geolite2City.path`为数据库的解压路径

### 安装Redis数据库
项目中使用Redis数据库存取监控信息。
- 下载[Redis数据库](https://redis.io/)
- 编译安装 `make && make test && make install`
- 启动 Redis 服务端 : `nohup ./redis-server &`
- 启动 Redis 客户端 : `./redis-cli`
- 测试 Redis : `127.0.0.1:6379> ping`
- 在项目的配置文件`src/main/resources/application.properties`中配置`redis.host`和`redis.port`

### 配置access.log地址
项目目前进度只读取`access.log`一个文件，在后面的进度中会读取所有的`access`日志文件
- 在项目的配置文件`src/main/resources/application.properties`中配置`logFile.path`的路径
