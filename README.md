# pk-ans-question
在线答题pk后台
[前端项目链接](https://github.com/liuzxhixian/answer-pk)

### 环境搭建

* JDK8
* 开发工具: IDEA2020
* 数据库：MySQL8

### 配置application.yml

* 端口号
* 数据库url、用户名、密码
![image](https://user-images.githubusercontent.com/46297458/141418923-3a550a3b-ec30-4f3d-bbb2-145a41c6d402.png)

* 导入数据库表结构和数据
![image](https://user-images.githubusercontent.com/46297458/141419468-2b8177e7-f58a-45fc-99b4-31e5eac87839.png)

### 项目原理
本项目主要是通过websocket和前端进行通信，使用springboot和spring对websocket的实现。
针对在线、答题、pk这些机制设计了对应的模型。具体可以下载项目阅读源码，有需要后续在更新说明
