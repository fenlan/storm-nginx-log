# <p align='center'>nginx日志监控</p>

<div align="center">

![build status](https://travis-ci.org/fenlan/storm-nginx-log.svg?branch=master)
[![codebeat badge](https://codebeat.co/badges/cbf116f9-877c-420c-b8c1-1414beb9917a)](https://codebeat.co/projects/github-com-fenlan-storm-nginx-log-master)
![size](https://github-size-badge.herokuapp.com/fenlan/storm-nginx-log.svg)
![platform](https://img.shields.io/badge/platform-Linux-orange.svg)
![progress](http://progressed.io/bar/25?title=completed)

</div>

## 简单介绍
项目基于`storm`的实时nginx日志监控，通过读取nginx的日志文件`access.log`来收集nginx服务器的状态，并在一定时间内，统计访问ip的国家地址、指定时间内所有访问次数、访问的状态码、访问的站点、访问者使用的系统、访问者使用的浏览器。

## 实现思路
1. 数据读取
`Storm`Spout读取`access.log`文件，将新产生的行记录提交给SpliteBolt。

2. 数据存储
storm将日志行记录划分成不同的块，其中包括ip地址块、访问时间块、访问请求信息块、访问状态码快、访问请求返回大小块、访问主机信息块。将每个块提交给CounterBolt。此外，在SpliteBolt中对行记录统计得到每天访问量等信息，存入Redis。

3. 数据处理
Storm CounterBolt对提交来的块做对应的统计处理，将处理结果存入Redis。

## Storm处理过程
1. `storm`分块结果
``` java
collector.emit(new Values("remote_addr", milli_time + "##" + remote_addr));
collector.emit(new Values("request", milli_time + "##" + request));
collector.emit(new Values("status", milli_time + "##" + status));
collector.emit(new Values("body_bytes_sent", milli_time + "##" + body_bytes_sent));
collector.emit(new Values("virtual_host", milli_time + "##" + virtual_host));
collector.emit(new Values("http_user_agent", milli_time + "##" + http_user_agent));
```

| key | value |
|--------|--------|
|   remote_addr     |   客户端ip地址     |
|   milli_time   |   访问时间       |
|   request|   请求的url和HTTP协议 |
|   status |   请求状态码     |
|   body_bytes_sent   |   访问请求返回内容大小   |
|   http_user_agent   |   客户端系统信息  |
|   virtual_host   |  虚拟站点  |

2. `storm`统计信息
- IP统计

``` java
city = AnalyzeIP.cityOfIP(value);
counter(cityOfIP_counter, city);
```

- status统计

``` java
/**    将状态码分为 1** 2** 3** 4** 5**     **/
Integer status = Integer.parseInt(value) / 100;
String statusStr = status + "**";
counter(status_counter, statusStr);
```

- 客户端信息统计
	- 客户端系统类型统计
	```java
    String system = UserAgent.systemRegx(value);
    counter(system_counter, system);
    ```

    - 客户端浏览器类型统计
    ``` java
    String browser = UserAgent.browserRegx(value);
    counter(browser_counter, browser);
    ```

- virtual_host统计

``` java
String regx = "([^/]*)(\\/\\/[^/]*\\/)([^ ]*)";
Pattern pattern = Pattern.compile(regx);
Matcher matcher = pattern.matcher(value);
if (matcher.find()) {
    String matcherString = matcher.group(2);
    String virtual_host = matcherString.substring(2, matcherString.length()-1);
    counter(virtualHost_counter, virtual_host);
    jedis.hset("virtual_host", virtual_host, virtualHost_counter.get(virtual_host).toString());
}
```

## 统计结果截图
### Keys
![](https://github.com/fenlan/Mycode/blob/master/images/nginxLog/keys.png)

### 客户端系统统计结果
![](https://github.com/fenlan/Mycode/blob/master/images/nginxLog/user_system.png)

### 每天点击量统计
![](https://github.com/fenlan/Mycode/blob/master/images/nginxLog/days_counter.png)

### 状态码统计
![](https://github.com/fenlan/Mycode/blob/master/images/nginxLog/status_code.png)

### 每天请求响应文件大小
![](https://github.com/fenlan/Mycode/blob/master/images/nginxLog/days_bytes_counter.png)

### Virtual_host统计
![](https://github.com/fenlan/Mycode/blob/master/images/nginxLog/virtual_host.png)

### 每天访问人数统计
![](https://github.com/fenlan/Mycode/blob/master/images/nginxLog/visitor_counter.png)

### 客户端地址统计
![](https://github.com/fenlan/Mycode/blob/master/images/nginxLog/city_of_ip.png)

### 客户端浏览器统计
![](https://github.com/fenlan/Mycode/blob/master/images/nginxLog/user_browser.png)

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
