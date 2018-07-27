# 基于可靠消息方案的分布式事务：Lottor使用
## Lottor介绍
Lottor用于解决微服务架构下分布式事务的问题，基于可靠性消息事务模型实现。

### Lottor的结构
Lottor由三部分组成：

- Lottor Server
- Lottor Client
- Lottor Admin UI

Lottor服务器与客户端之间使用Netty通信。所有的客户端（生产端和消费端）都会与服务器保持长连接。Lottor Admin UI用于展示系统中的事务组详细信息，包括预提交的事务组、消费失败的事务消息，并支持页面操作失败的消息（如重试）。

![Lottor的设计](http://ovcibtedi.bkt.clouddn.com/lottor-arche.jpg)

### 如何使用
Lottor Client存储方式支持Redis和MongoDB，Lottor Server目前数据存储只支持MongoDB。Lottor客户端和服务器都会注册到服务发现组件，支持Consul、zookeeper、Eureka。目前对于Spring Cloud的集成更为方便，Lottor Server服务器发送事务消息到Lottor Client消费方时，使用了Spring Cloud集成的消息驱动组件Spring Cloud Stream，不过这属于弱耦合，Lottor暂时不考虑这部分的解耦。

体验一下Lottor准备的samples，需要准备如下组件：

1. 安装好Consul或其他服务发现组件；
2. 消息中间件rabbitmq或kafka（目前Spring Cloud Stream完全适配这两种消息中间件）；
3. MongoDB，Lottor Server存储的方式；
4. Redis（可选），客户端的存储方式，也可为Redis。

如果你想很快尝试项目中的Samples，请选择如上的准备事项的第一个选择，避免耽误你的时间。

具体启动步骤：

1. 首先需要启动Lottor Server，有两个端口，9888用于Netty Server通信，9666为对外暴露的Http端口（用于提供Lottor Admin UI的接口和Lottor Client连接）；
2. 启动lottor-samples下的lottor-demo-consumer，端口为8007；
3. 启动lottor-samples下的lottor-demo-producer，端口为8009。

启动好如上三个服务之后，将会在两个客户端服务的控制台看到如下的日志：

```
c.b.t.c.n.impl.NettyClientServiceImpl    : 连接txManager-socket服务-> 127.0.0.1:9998
c.b.t.c.n.impl.NettyClientServiceImpl    : Connect to -> 127.0.0.1:9998 server successfully!
c.b.t.c.n.h.NettyClientMessageHandler    : 建立链接-->io.netty.channel.DefaultChannelHandlerContext@739fd967
c.b.t.c.c.impl.TxOperateServiceImpl      : 启动OperatePool操作线程数量为:8
c.b.t.core.service.impl.InitServiceImpl  : 分布式补偿事务初始化成功！
```
客户端还会按照一定的时间周期向Lottor Server发送心跳，并收到服务端的心跳回应。如下所示:

```
c.b.t.c.n.h.NettyClientMessageHandler    : 向服务端发送的心跳
c.b.t.c.n.h.NettyClientMessageHandler    : 接收服务端 /127.0.0.1:9998 ，执行的动作为:心跳
```

访问lottor-demo-producer提供的API接口`http://localhost:8009/produce`即可看到生产方和消费方控制台的日志信息。

// todo 待补充更详细的调用
### 项目截图

![项目结构](http://image.blueskykong.com/lottor-project.jpg)

![UI界面](http://ovcibtedi.bkt.clouddn.com/lottor-ui.jpg)

![首页](http://ovcibtedi.bkt.clouddn.com/lottor-index.jpg)

![事务组信息](http://ovcibtedi.bkt.clouddn.com/lottor-group.jpg)

![事务组状态](http://ovcibtedi.bkt.clouddn.com/lottor-gropu-state.jpg)

#### 订阅最新文章，欢迎关注我的公众号

![微信公众号](http://ovci9bs39.bkt.clouddn.com/qrcode_for_gh_ca56415d4966_430.jpg)


