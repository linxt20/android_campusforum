# 后端接口需求文档

下面提供的传入参数和返回值都是初始版的要求
## 1、添加动态接口
- 实现效果：
  - 初始版：这是草稿发表后，需要向动态总表里面插入一条新的数据
  - 升级版：我发现动态还有标签我还没实现，之后还得在补充，先预留一下代码空间
- 传入参数
```
String Username,
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

## 2、获取全部动态接口
- 实现效果：这是在进入动态展示页面时候
  - 初始版：不传参数，直接从后端拿到所有的动态
  - 升级版：就是在上面的基础上，会传入三个参数，分别是searchkey(需要支持多关键词，匹配标题、类别、内容、用户名),tags,sorted. 前两个是筛选，第三个是排序，可以先预留一下代码空间，方便以后升级
- 传入参数
```
无
```
- 返回值
```
String Username,
String createAt,
String title,
String content,
int comment_count,
int like_count,
int star_count,
String user_head,
String[] imagelist
```
