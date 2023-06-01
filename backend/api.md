# 后端接口需求文档

下面提供的传入参数和返回值都是初始版的要求

### 注意事项

1. **目前后端所有api均为post方法**

2. **所有写了地址的，是后端已经完成的**

3. **目前所有后端接口新加功能，都没测过，如果写了测试，是没有删全哈哈哈**

## 添加动态接口
- 实现效果：
  - 初始版：这是草稿发表后，需要向动态总表里面插入一条新的数据
  - 升级版：我发现动态还有标签我还没实现，之后还得在补充，先预留一下代码空间
- 传入参数
```java
String userid //用户id 
String createAt,
String title,
String content,
String tag, //新增的标签
int resource_num,//图片的数量
String resource_type,//资源类型 图片为jpg，视频为mp4
```
- 返回值

```
部署后前端图片传到后端还没写好
```

* 地址

```java
"/post/new_post"
```

* 完成状态
  * 后端是否写完：上传图片未整合
  * 是否测试：无

* 后端人民的疑问：
  * 此后支持markdown的话后端存储格式仍未string吗？ // 暂时不考虑

## 获取动态接口

- 实现效果：这是在进入动态展示页面时候
  - 初始版：不传参数，直接从后端拿到所有的动态
  - 升级版：就是在上面的基础上，会传入三个参数，分别是searchkey(需要支持多关键词，匹配标题、类别、内容、用户名),tags,sorted. 前两个是筛选，第三个是排序，可以先预留一下代码空间，方便以后升级
- 传入参数
```java
String userid: 用户id
String search_key: 搜索关键词 和帖子的标题、内容、tag、用户名匹配；支持多关键词，按逻辑与进行匹配，关键词之间用空格分隔
String tag: 标签 
String sort_by: 排序依据 //热度怎么计算？这里还没有实现

```
- 返回值
  首先是一个列表，列表里面的每一项如下

  每一项均为post，在前端有定义
```java
	String postid;//唯一的标签 怎么生成还要再考虑 mongodb的唯一标签？
    String author_id;//作者id
    String author_name;//作者名字
    String author_head;//作者头像
    String create_time;//创建时间 前端接口为createAt
    String title;//标题
    String content;//内容  !!支持md，可能需要修改
    int comment_count;//评论量
    int like_count;//点赞量
    List<String> like_userid_list;//点赞用户id列表
    int star_count;//收藏量
    List<String> star_userid_list;//收藏用户id列表

    int if_like;//对于目前浏览的用户，是否点赞 0没有，1有
    int if_star;//对于目前浏览的用户，是否收藏 0没有，1有

    int resource_num;//图片或视频的数量
    String resource_type;//资源类型 图片为jpg，视频为mp4
    String[] resource_list;//资源列表 记录其在static/images中的名称

    String tag;//标签仅一个
    List<Comment> comment_list;//评论列表
```

* 地址

```java
"/post/get_all"
```

* 完成状态
  * 后端是否写完：是
  * 是否测试：后端用apifox测试了

* 后端人民的疑问：
  * 评论的list？ // 暂时先不考虑

## 根据id获取单个post

- 传入参数

  ```java
  String postid 帖子id
  String userid 浏览用户的id
  ```

- 返回值：

  ```java
  Post post
  ```

- 地址：post/get_post

## 点赞和收藏接口

- 实现效果：点击后，前端向后端传递被点击的动态的id和点击的是收藏还是点赞,后端返回一个收藏/点赞后的新的收藏数/点赞数
- 传入参数
```java
String userid
String postid
int state // 0为点赞，1为收藏
int cancel // 0为第一次点，1为取消前面的收藏/点赞
```
- 返回值
```java
int count; // 新的收藏数或者点赞数
```

* 地址

``` java
"/post/like_or_star"
```

## 登录

- 传入参数

```java
String username
String password
```

- 返回值：

```java
String userid 
```



## 注册

- 传入参数

  ```java
  String username
  String password
  ```

- 返回值：

  ```java
  success/fail
  ```

## 发布评论

- 传入参数

  ```java
  String postid 帖子id
  String userid
  String content
  Date creat_time // “yyyy-MM-dd HH:mm:ss” 格式，让前端传主要是考虑前端可以实时显示评论，而不需要从后端获取返回的时间戳，也可以后端统一写
  ```

- 返回值：

  ```java
  success/fail
  ```

- 地址：post/new_comment

## 获取个人信息

- 传入参数

  ```java
  String userid
  ```

- 返回值：自定义类User

  ```java
  User user
  ```

- 接口：user/get_user_info

## 修改用户名、简介

- 传入参数

  ```java
  String userid
  String username
  String description;//简介
  ```
  
- 返回值：

  ```java
  String success/失败原因
  ```

- 地址：user/update_username_and_description 

## 修改密码

- 传入参数

  ```java
  String userid
  String password
  ```

- 返回值：

  ```java
  String success/失败原因
  ```

- 地址：user/update_user_password

## 修改个人头像

- 传入参数

  ```java
  String userid
  ```

- 返回值：

  ```java
  String user_head_id //前端需要有额外操作：用这一id调用 jyjj传图像接口传输新的头像
  ```

- 地址：user/update_user_avatar

## 关注或取关其他用户

- 传入参数

  ```java
  String follower_id//关注者的id
  String be_followed_id//被关注者的id
  type：“follow”或“unfollow” //关注还是取消关注
  ```

- 返回值：

  ```java
  String success//失败原因
  ```

- 地址：/user/get_follow_or_fans_list

## 获取关注或粉丝列表

- 传入参数

  ```java
  String userid：用户id
  String type：“follow”或“fans” 用户关注的人还是粉丝
  ```

- 返回值：

  ```java
  List<User>：用户关注或粉丝列表
  ```

- 地址：/user/get_follow_or_fans_list

## 获取用户发布或收藏的所有动态

- 传入参数

  ```java
  userid：用户id
  type：“create”或“star” 用户发过的帖子还是收藏的帖子
  ```

- 返回值：一个list，list中的每个元素是一个post,post具体信息见上，不多写了避免修改时遗漏

- 地址：/user/get_user_postlist
