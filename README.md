
## Lottor介绍
Lottor用于解决微服务架构下分布式事务的问题，基于可靠性消息事务模型实现。

### Lottor的结构
Lottor由三部分组成：

- Lottor Server
- Lottor Client
- Lottor Admin UI

Lottor服务器与客户端之间使用Netty通信。所有的客户端（生产端和消费端）都会与服务器保持长连接。Lottor Admin UI用于展示系统中的事务组详细信息，包括预提交的事务组、消费失败的事务消息，并支持页面操作失败的消息（如重试）。

![Lottor的设计](http://ovcibtedi.bkt.clouddn.com/lottor-arche.jpg)

关于Lottor的实现思路简略可以参见[基于可靠消息方案的分布式事务：Lottor介绍](http://blueskykong.com/2018/05/04/lottor-intro/)，后面会详细介绍。
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
c.b.l.c.n.impl.NettyClientServiceImpl   : 连接到Lottor Server【127.0.0.1:9998】
c.b.l.c.n.impl.NettyClientServiceImpl   : Connect to【127.0.0.1:9998】c.b.l.c.n.h.NettyClientMessageHandler   : 成功连接到Lottor Server
c.b.l.c.cache.impl.TxOperateServiceImpl : 启动OperatePool操作线程数量为:8
c.b.l.core.service.impl.InitServiceImpl : 分布式事务Cache初始化成功！
o.s.s.c.ThreadPoolTaskScheduler         : Initializing ExecutorService  'taskScheduler'
```
客户端还会按照一定的时间周期向Lottor Server发送心跳，并收到服务端的心跳回应。如下所示:

```
c.b.l.c.n.h.NettyClientMessageHandler : 发送【心跳】事件到Lottor Server【127.0.0.1:9998】
c.b.l.c.n.h.NettyClientMessageHandler : 接收到 Lottor 服务端 【127.0.0.1:9998】 的【心跳】事件
```

### Docker启动
为了让读者更方便的在本地尝鲜，在项目中提供了docker-compose.yml用以便捷且快速地启动相关的中间件。在docker-compose.yml中包含了如下组件的配置：

- Lottor UI，Lottor 的前端项目Dashboard，用以展示分布式事务调用的相关信息，包括事务组的状态、事务消息的状态以及异常原因。
- Consul-0.8.5，服务发现与注册
- MongoDB-3.2，持久化存储
- Redis-4，持久化存储
- Rabbitmq-3.6-management，消息中间件
- Mysql-5.7，实例的user服务和auth服务存储

读者在本地安装好docker-compose之后，即可一键启动这些组件服务。

```bash
docker-compose up -d
```
![](http://image.blueskykong.com/lottor-docker.jpg "启动相关组件")
### 调用
Lottor-Samples中的场景为：调用User服务创建一个用户，用户服务的user表中增加了一条用户记录。除此之外，还会调用Auth服务创建该用户对应的角色和权限信息。
#### User提供的API端点
User服务只提供了一个创建用户接口，通过请求参数来模拟创建用户时会遇到的情况：

- 成功生产且成功消费

	User服务成功执行本地事务并发送确认消息，Lottor Server接收到确认的事务组消息，然后Lottor Server发送事务消息到Auth服务，Auth服务成功消费并向Lottor Server响应消息消费成功的状态。
	
	访问lottor-samples-auth提供的API接口`http://localhost:8009/user?result=success`即可看到User、Auth和Lottor Server控制台的日志信息。

    User日志信息：

    ```
    发送preCommit消息
    开始创建Lottor事务组,  事务组 id 为【1172893261】
    接收到 Lottor 服务端 【192.168.1.77:9998】 的【接收】事件
    发送事务组confirm消息, 本地事务完成状态为【true】
    事务发起方事务组confirm, 事务组 id 为【1172893261】
    事务组【1172893261】, confirm status为【已经提交】
    事务组【1172893261】成功发送确认消息
    ```

   Lottor Server日志信息：

    ```
    Lottor Server接收到客户端【192.168.1.77:62481】的【创建事务组】事件
    Lottor Server接收到客户端【192.168.1.77:62481】的【完成提交】事件
    send tx-msg and target service【auth】
    success send msg, and msg id is 【1958885429】
    ```
    Auth日志信息：

    ```
    ===============consume notification message: =======================
    TransactionMsg(groupId=1172893261, subTaskId=1958885429, source=user, target=auth, method=auth-role, args=UserRoleDTO(roleEnum=ADMIN, userId=0df80f61-2802-4dfc-9119-1de97e3b3a00), createTime=1533478144000, message=null, updateTime=null, consumed=0)
    auth-role
    matched case auth-role
    发送Consume消息，groupId【1172893261】 and subTaskId【1958885429】，消费结果为【true】
    tx-transaction 消费完成，事务组 id 为【1172893261】，消息 id 为【1958885429】
    ```


- 生产方本地事务执行失败

    User服务执行本地事务失败，并发送事务组回滚的消息到Lottor Server，取消该事务组中的消息发送。
	
	访问lottor-samples-user提供的API接口`http://localhost:8009/user?result=fail`即可看到User服务和Lottor Server控制台的日志信息。

    User服务的日志信息：

    ```
    发送preCommit消息
    开始创建Lottor事务组,  事务组 id 为【1964885182】
    接收到 Lottor 服务端 【127.0.0.1:9998】 的【接收】事件
    SQLErrorCodes loaded: [DB2, Derby, H2, HSQL, Informix, MS-SQL, MySQL, Oracle, 发送事务组confirm消息, 本地事务完成状态为【false】
    事务发起方事务组confirm, 事务组 id 为【1964885182】
    务组【1964885182】, confirm status为【回滚】
    事务组【1964885182】成功发送确认消息
    执行本地事务失败，cause is 【
    ### Error updating database.  Cause: java.sql.SQLException: Column count doesn't match value count at row 1
    ### The error may involve com.blueskykong.lottor.samples.user.service.mapper.UserMapper.saveUserFailure-Inline
    ### The error occurred while setting parameters
    ### SQL: INSERT INTO user(id,username,password,self_desc) VALUES(?,?)
    ### Cause: java.sql.SQLException: Column count doesn't match value count at row 1
    ; bad SQL grammar []; nested exception is java.sql.SQLException: Column count doesn't match value count at row 1】
    ```
    Lottor Server日志信息：

    ```
    Lottor Server接收到客户端【127.0.0.1:62481】的【创建事务组】事件
    Lottor Server接收到客户端【127.0.0.1:62481】的【完成提交】事件
    ```

- 成功生产但消费失败

    User服务成功执行本地事务并发送确认消息，Lottor Server接收到确认的事务组消息，然后Lottor Server发送事务消息到Auth服务，Auth服务消费失败，并向Lottor Server响应消息消费失败的状态。
 
    访问lottor-samples-user提供的API接口`http://localhost:8009/user?result=fail`即可看到User、Auth和Lottor Server控制台的日志信息。这里只展示Auth服务的日志信息，其他两个服务的日志信息可以参见第一种情况。

    Auth日志信息：

    ```
    ===============consume notification message: =======================
    TransactionMsg(groupId=1177311097, subTaskId=1713403133, source=user, target=auth, method=auth-role, args=UserRoleDTO(roleEnum=ADMIN, userId=null), createTime=1533478468000, message=null, updateTime=null, consumed=0)
    auth-role
    SQLErrorCodes loaded: [DB2, Derby, H2, HSQL, Informix, MS-SQL, MySQL, Oracle, PostgreSQL, Sybase, Hana]
    ### Error updating database.  Cause: com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException: Column 'user_id' cannot be null
    ### The error may involve com.blueskykong.lottor.samples.auth.service.mapper.RoleUserMapper.saveRoleUser-Inline
    ### The error occurred while setting parameters
    ### SQL: INSERT INTO user_role(id,user_id,role_id) VALUES(?,?,?)
    ### Cause: com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException: Column 'user_id' cannot be null
    ; SQL []; Column 'user_id' cannot be null; nested exception is com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException: Column 'user_id' cannot be null
    发送Consume消息，groupId【1177311097】 and subTaskId【1713403133】，消费结果为【false】
    tx-transaction 消费完成，事务组 id 为【1177311097】，消息 id 为【1713403133】
    ```

### 项目截图

![项目结构](http://image.blueskykong.com/lottor-project.jpg)

打开Lottor DashBoard，地址为http://127.0.0.1:3000/lottor（Lottor Server的地址可以通过docker-compose中的环境变量`SERVER_TAG`配置，默认为http://127.0.0.1:9666），可以看到如下页面：

![UI首页](http://image.blueskykong.com/lottor-new-index.jpg)

![事务组信息](http://image.blueskykong.com/lottor-item-new.jpg)

![事务组状态](http://image.blueskykong.com/lottor-msg-new.jpg)

### 致谢
Lottor的具体实现上，参考了2PC的分布式事务解决方案`happylifeplat-transaction`的通信框架。最近看了一下，发现已经更名为`Raincat`，读者欲了解更多，可以参见https://github.com/yu199195/Raincat，在此致谢。

**Lottor项目地址：https://github.com/keets2012/Lottor**
#### 订阅最新文章，欢迎关注我的公众号

![微信公众号](http://ovci9bs39.bkt.clouddn.com/qrcode_for_gh_ca56415d4966_430.jpg)





