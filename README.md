# android大作业

## 预设置
- 加入github
- 设置手机模拟器版本：Android 虚拟机 Pixel XL，屏幕分辨率 1440*2560，系统版本：Android 11.0

## 现有困难
- 第三方库有哪些？怎么用？
- 前后端怎么连接？
- 这评分方式中有没有卷额外功能的需求？
- 这个消息分享的功能有点抽象，没想明白实现方式:`对信息进行分享:将消息标题或内容分享到任何其他可展示内容的APP中`

## 文档部分
- 需求与设计文档：介绍系统的角色、功能清单(详细说明)、功能实现方式规划 (ddl：4/26)
- 项目说明文档：核心功能的设计与实现，组员的分工  (ddl：6/18)
- 各个功能的操作指南 (ddl：6/18)
- 
## 原型设计页面大概规划
- 登录/注册页面(密码修改?)
- 主页导航
  - 信息展示部分(默认显示信息浏览界面)
    - 信息浏览界面 包含搜索、排序、分类、屏蔽操作 
    - 信息详情界面(点击item后显示) 包含 评论操作和评论显示
  - 信息发布按钮(只显示一个按钮)
    - 信息发布界面(支持文字、图片、视频、位置、消息标签、字体样式修改)
  - 个人信息页面(默认显示个人信息页面)
    - 个人信息(用户名、头像、简介、密码修改)
    - 用户主页(按照时间线展示用户发布信息)
    - 设置界面
    - 收藏信息列表界面
    - 关注和被关注的用户列表界面
      - 其他用户主页
      - 私信界面(应该得用websocket而不是轮询) 可以查看历史私信
    - 通知消息列表(查看通知、回复私信)