# 后端接口需求文档

下面提供的传入参数和返回值都是初始版的要求
## 1、添加动态接口
- 实现效果：
  - 初始版：这是草稿发表后，需要向动态总表里面插入一条新的数据
  - 升级版：我发现动态还有标签我还没实现，之后还得在补充，先预留一下代码空间
- 传入参数
```java
String Username,//是用户唯一的ID吗？
String createAt,
String title,
String content,
int comment_count,
int like_count,
int star_count,
String user_head,
String[] imagelist

```
- 返回值

```
无
```

* 地址

```java
"/post/new_post"
```

* 完成状态
  * 后端是否写完：
  * 是否测试：无

* 后端人民的疑问：
  * 此后支持markdown的话后端存储格式仍未string吗？

## 2、获取全部动态接口

- 实现效果：这是在进入动态展示页面时候
  - 初始版：不传参数，直接从后端拿到所有的动态
  - 升级版：就是在上面的基础上，会传入三个参数，分别是searchkey(需要支持多关键词，匹配标题、类别、内容、用户名),tags,sorted. 前两个是筛选，第三个是排序，可以先预留一下代码空间，方便以后升级
- 传入参数
```
无
```
- 返回值
```java
String Username,//是用户唯一的ID吗？
String createAt,
String title,
String content,
int comment_count,
int like_count,
int star_count,
String user_head,
String[] imagelist

//int if_liked,//0为未点赞，1为已点赞
//int if_stared,//0为未收藏，1为已收藏
```

* 地址

```java
"/post/get_list"
```

* 完成状态
  * 后端是否写完：
  * 是否测试：无

* 后端人民的疑问：
  * 用户的点赞、收藏信息？（类似于b站，前端应该可以看见用户是否对该动态已点赞 虽然圣经中没有 但是如果不允许重复点赞可能需要
  * 评论的list？
