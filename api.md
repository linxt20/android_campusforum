# 后端接口需求文档

下面提供的传入参数和返回值都是初始版的要求

## 添加动态接口
- 实现效果：
  - 初始版：这是草稿发表后，需要向动态总表里面插入一条新的数据
  - 升级版：我发现动态还有标签我还没实现，之后还得在补充，先预留一下代码空间
- 传入参数
```java
用户id 等待后端数据类型
String Username,// 这个不需要了，用户名与用户的id绑定
String createAt,
String title,
String content,
int comment_count,// 这个后端初始化为0，前端不传入
int like_count, // 这个后端初始化为0，前端不传入
int star_count,// 这个后端初始化为0，前端不传入
String user_head, // 这个也不需要了，头像和用户的id绑定
String[] imagelist,
String tag, //新增的标签
```
- 返回值

```
id
```

* 地址

```java
"/post/new_post"
```

* 完成状态
  * 后端是否写完：
  * 是否测试：无

* 后端人民的疑问：
  * 此后支持markdown的话后端存储格式仍未string吗？ // 暂时不考虑

## 获取全部动态接口

- 实现效果：这是在进入动态展示页面时候
  - 初始版：不传参数，直接从后端拿到所有的动态
  - 升级版：就是在上面的基础上，会传入三个参数，分别是searchkey(需要支持多关键词，匹配标题、类别、内容、用户名),tags,sorted. 前两个是筛选，第三个是排序，可以先预留一下代码空间，方便以后升级
- 传入参数
```
用户id 等待后端数据类型
```
- 返回值
首先是一个列表，列表里面的每一项如下
```java
String Username,
String createAt,
String title,
String content,
int comment_count,
int like_count,
int if_liked,//0为未点赞，1为已点赞
int star_count,
int if_stared,//0为未收藏，1为已收藏
String user_head,
String[] imagelist
String tag // 新增的标签
```

* 地址

```java
"/post/get_list"
```

* 完成状态
  * 后端是否写完：
  * 是否测试：无

* 后端人民的疑问：
  * 评论的list？ // 暂时先不考虑

## 点赞和收藏接口

- 实现效果：点击后，前端向后端传递被点击的动态的id和点击的是收藏还是点赞,后端返回一个收藏/点赞后的新的收藏数/点赞数
- 传入参数
```
用户id
动态id
int state // 0为点赞，1为收藏
int cancel // 0为第一次点，1为取消前面的收藏/点赞
```
- 返回值
```java
int count; // 新的收藏数或者点赞数
```