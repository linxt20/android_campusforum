# 后端接口需求文档

下面提供的传入参数和返回值都是初始版的要求



**目前后端所有api均为post方法**

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

## 获取全部动态接口

- 实现效果：这是在进入动态展示页面时候
  - 初始版：不传参数，直接从后端拿到所有的动态
  - 升级版：就是在上面的基础上，会传入三个参数，分别是searchkey(需要支持多关键词，匹配标题、类别、内容、用户名),tags,sorted. 前两个是筛选，第三个是排序，可以先预留一下代码空间，方便以后升级
- 传入参数
```
String userid //用户id
```
- 返回值
  首先是一个列表，列表里面的每一项如下

  每一项均为post，在前端有定义
```java
	String postid;
    String author_id;//作者id
    String author_name;//作者名字
    String author_head;//作者头像
    String create_time;//创建时间 前端接口为createAt
    String title;//标题
    String content;//内容  !!支持md，可能需要修改
    int comment_count;//评论量
    int like_count;//点赞量
    String[] like_userid_list;//点赞用户id列表
    int star_count;//收藏量
    String[] star_userid_list;//收藏用户id列表
    int if_like;//对于目前浏览的用户，是否点赞 0没有，1有
    int if_star;//对于目前浏览的用户，是否收藏 0没有，1有
    int resource_num;//图片或视频的数量
    String resource_type;//资源类型 图片为jpg，视频为mp4
    String[] resource_list;//资源列表 记录其在static/images中的名称
    String tag;//标签仅一个
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

* 完成情况：
  * 后端是否写完：是
  * 是否测试：apifox测试

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

