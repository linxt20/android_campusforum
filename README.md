# android大作业

## 后端运行方法

修改application.properties里面的静态文件夹路径（改为自己的backend文件夹路径下的/target/static![Screenshot 2023-06-07 at 19.02.04](/Users/janet/Library/Application Support/typora-user-images/Screenshot 2023-06-07 at 19.02.04.png)

修改ResourceController.java中的这个变量，（改为自己的backend文件夹路径下的/target/static/static

![Screenshot 2023-06-07 at 19.02.04](/Users/janet/Library/Application Support/typora-user-images/Screenshot 2023-06-07 at 19.03.05.png)

在backend文件夹中打开终端，运行`mvn install`会在target中生成打包后的.jar文件，在终端通过:

```shell
cd target
java -cp static -jar backend-0.0.1-SNAPSHOT.jar
```

方式运行后端 

## 预设置

- 加入github
- 设置手机模拟器版本：Android 虚拟机 Pixel XL，屏幕分辨率 1440*2560，系统版本：Android 11.0

## 现有困难
- 第三方库有哪些？怎么用？
  - 部分参考源码
    - https://cloud.tsinghua.edu.cn/f/064a0959b7fd48828d78/?dl=1
  - 第三方组件网站：
    - 直接使用的组件 https://blog.csdn.net/sinat_33585352/article/details/88825720
    - 直接使用的组件 https://blog.csdn.net/qq_36875339/article/details/77602890
    - 直接使用的组件 https://github.com/ColorfulCat/AndroidLibs/tree/master/%E5%8A%A8%E7%94%BBAnimation
  - 关于使用方式：要在 Android Studio 中使用这些第三方库，通常需要将库的依赖项添加到项目的 build.gradle 文件中。然后，你可以像使用系统自带的控件一样在布局文件中添加这些自定义控件，并在代码中操作它们。
- 这个消息分享的功能有点抽象，没想明白实现方式:`对信息进行分享:将消息标题或内容分享到任何其他可展示内容的APP中`
  - 这个实际实现的时候再具体研究

## 文档部分
- ~~需求与设计文档：介绍系统的角色、功能清单(详细说明)、功能实现方式规划 (ddl：4/26)~~
- 项目说明文档：核心功能的设计与实现，组员的分工  (ddl：6/18)
- 各个功能的操作指南 (ddl：6/18)

## 原型设计部分
### 原型设计页面大概规划
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

### 原型设计成果：
在规划的基础上进一步优化了布局和设计：
https://modao.cc/app/1Jj3Dyt3rt0do85bEFvrNp

## 目前功能点与分工安排
- 林欣涛:前端
- 张雨恬:后端
- 汪静雅:前端

