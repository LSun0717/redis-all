# Redis-all

本项目为Redis各类实战的案例合集，结合应用场景与案例实战，包含特性如下：

- 基于Redis+Token，整合验证码，实现登录注册、权限校验、Token续期
- 缓存穿透解决方案（回种空值）
- 缓存击穿解决方案（逻辑过期）
- 缓存雪崩解决方案（缓存雪崩）
- 缓存预热与缓存构建

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