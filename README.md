# XChat-server

![Java11](https://img.shields.io/badge/Java-11-red)
![Maven3](https://img.shields.io/badge/MAVEN-3-blue)

## 项目描述

&emsp;&emsp;XChat是一款分布式去中性化且注重保护隐私的网络通讯系统，主要包含一个服务端实现和一个客户端内核，本项目是其服务端部分，使用Java语言开发。

## 第三方依赖

- [SQLite](https://sqlite.org/)
    > 体积小巧但是性能强悍的单文件数据库
- [Bson](https://bsonspec.org/)
    > 二进制序列化的数据描述与交换的通用编码格式
- [Logback](https://logback.qos.ch/)
    > 实现了SLF4J接口的高性能日志框架

## 构建

&emsp;&emsp;需要Maven3、JDK11+

```
    mvn compile
    mvn package
```

## 技术摘要

### 网络部分

&emsp;&emsp;本项目网络核心使用Java原生的NIO接口实现，即同步非阻塞网络IO模型，此模型在Linux Kernel 2.5.44以上的版本中的底层是Epoll实现，其他系统则是Poll或Selector实现。  
&emsp;&emsp;相对于传统的同步阻塞式网络IO，同步非阻塞网络IO模型能提供更高的并发性能以及更高效的连接管理，此模型在Epoll实现下理论性能最好。

### 加解密部分

&emsp;&emsp;本项目对发送的数据进行严格加密，核心加密部分采用AES算法，密钥长度256位，加密模式为CTR计数器模式，数据校验使用GMAC-128，数据分组无填充，并对每个网络传输帧都使用独立的128位随机初始向量进行混淆，全方位保护数据安全。

## 许可证

&emsp;&emsp;本项目采用[GPL v2](https://www.gnu.org/licenses/old-licenses/gpl-2.0.txt)开源协议开放源代码。
