# Redis-all

本项目Redis-all为Redis各类实战的案例合集，持续迭代中，目前包含特性如下：

- 基于Redis+Token，整合验证码，实现登录注册、权限校验、Token续期、session共享
- 缓存穿透解决方案（回种空值）
- 缓存击穿解决方案（逻辑过期）
- 缓存雪崩解决方案（过期时间离散化）
- 缓存预热与缓存构建
- 基于 INCR 实现分布式id（32位时间戳 + 32位序列号）
- 分布式锁（SET lockName lockOwner EX 10 NX）
  - 解决线程故障，无法释放锁（TTL）
  - 解决其他线程误释放锁
    - 线程误删（UUID+threadId）
    - 查询与删除非原子性操作（Lua 脚本）
  - 缺陷
    - 不可重入
    - 不可重试（阻塞）
    - 超时释放（业务执行过长，锁却已经释放）
    - 主从一致性
- Redisson
  - 分布式锁（可重、可重试、超时释放、主从一致性）
  - watch dog（看门狗机制自动续期）
  - RedLock
- 秒杀(超卖、一人一单)
- 同步版本
  - 乐观锁
    - version版本号(stock > 0)：成功率低
    - CAS
  - 悲观锁
    - synchronized
    - select... for update
- 异步版本
  - Redisson分布式锁
  - BlockQueue阻塞队列
  - ThreadPoolExecutor线程池
  - Lua脚本
    - Set解决一人一单
    - Incr库存扣减
    - 防止超卖
- 消息队列
  - List
  - Pub/Sub
    - 消息无法持久化
  - Stream
    - 消息可回溯
    - 一个消息可以被多个消费者消费
    - 可以阻塞读取
    - 有消息漏读的风险
- Set
  - 点赞、取消点赞
- ZSet
  - 排行榜
  - 唯一值判断
- Set
  - 关注和取关

# 技术选型
- 基础框架
  - SpringBoot
  - SpringMVC
  - Spring
- Web服务器
  - Nginx
  - Tomcat
- 缓存组件
  - Redis
  - SpringDataRedis
- 数据库
  - Mysql
  - Mybatis + Mybatis-Plus
- 工具
  - Docker
  - Maven
  - PostMan
  
# Startup
- 导入数据库，SQL文件在如下目录
  - src/main/resources/db/redis-all.sql
  - 尽量使用MySQL8.0以上版本（推荐Docker安装）
- 启动Redis实例
  - 物理机安装 
  - Docker
```shell
docker pull redis:6.2.7

docker run -itd -p 6379:6379 \
           -v /home/lions7/projects/sadc/redis/data:/data \
           --restart always \
           --name redis \
           redis
```
- Maven导入依赖，具体依赖详见pom.xml
- 配置 src/main/resources/application.yaml 文件中Redis实例地址与Mysql实例地址
- banner可通过更换src/main/resources/banner.txt替换，主要为了好看