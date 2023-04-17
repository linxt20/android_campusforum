# android大作业

## 预设置
- 加入github
- 设置手机模拟器版本：Android 虚拟机 Pixel XL，屏幕分辨率 1440*2560，系统版本：Android 11.0

## 现有困难
- 第三方库有哪些？怎么用？
  - 第三方组件网站：
    - https://android-arsenal.com/
    - https://github.com/material-components/material-components-android
    - https://github.com/wasabeef/awesome-android-ui
    - https://github.com/ColorfulCat/AndroidLibs
    - https://guides.codepath.com/android
  - 关于使用方式：要在 Android Studio 中使用这些第三方库，通常需要将库的依赖项添加到项目的 build.gradle 文件中。然后，你可以像使用系统自带的控件一样在布局文件中添加这些自定义控件，并在代码中操作它们。
- 前后端
  - **后端的选择：具体选择有待考量**
    - 看了一圈比较正常的还是django，这个是之前学过的
    - 另一个是java领域比较常用的Spring Boot，掌握一个新的广泛使用的框架，提前适应打工人生活(x)
    - 还有一种可能就是不用独立的后端，就是前后端不分离，就直接在android studio中写这个(这个得和助教沟通一下，不知道助教对前后端分离的意愿情况如何)
  - 前后端连接+轮询/websocket：这个好解决，等实际写的时候，再具体研究
- 这评分方式中有没有卷额外功能的需求？
  - 这个确实比较抽象
- 这个消息分享的功能有点抽象，没想明白实现方式:`对信息进行分享:将消息标题或内容分享到任何其他可展示内容的APP中`
  - 这个实际实现的时候再具体研究

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
- 原型设计网站：

## 目前功能点与分工安排


## 每次会议主要工作记录

### 第一次会议 23/04/16
主要工作：
- 原型设计
- 需求与设计文档整理
- 后端选择
- waiting

我现在想要用android studio写app的前端，spring boot写app的后端，如何从零开始创建项目，前端写一个按钮，点击后传递"hello"给后端，后端传递"hi"回前端，并显示在按钮上，其中使用OkHttp的前后端通信方式。你教我从零开始完成这个简单的实例。
前后端通信的例子如下：
前端定义
```java
public static final String sharedPrefFile = "ucom.example.bbs2";
    public static SharedPreferences mPreferences;
    public static String baseurl = "http://43.138.41.159:8000";
    //public static String baseurl = "http://183.173.243.2:8000";
    public static Integer user_id = 0;
    //网络接口
    public static HTTP http = HTTP.builder()
            .baseUrl(Const.baseurl)
            .charset(StandardCharsets.UTF_8)
            .callbackExecutor((Runnable run) -> {
                // 实际编码中可以吧 Handler 提出来，不需要每次执行回调都重新创建
                new Handler(Looper.getMainLooper()).post(run); // 在主线程执行
            })
            .addMsgConvertor(new FastjsonMsgConvertor())
            .build();
    public static boolean isDestroy(Activity mActivity) {
        return mActivity == null || mActivity.isFinishing() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && mActivity.isDestroyed());
    }
```

前端调用
```java
login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Const.http.async("/login/")
                        .bodyType("application/x-www-form-urlencoded")
                        .setOnResponse(httpResult -> {
                            HttpResult.Body body = httpResult.getBody();
                            Mapper content_mapper = body.toMapper();
                            Integer status = content_mapper.getInt("status");
                            if(status.equals(-1)){
                                Toast.makeText(LoginActivity.this,"登录失败，请检查用户名或密码",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Const.user_id = content_mapper.getInt("user_id");
                                SharedPreferences.Editor preferencesEditor = Const.mPreferences.edit();
                                preferencesEditor.clear();
                                preferencesEditor.putInt("user_id", Const.user_id);
                                preferencesEditor.apply();
                                Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                                Intent intent1 = new Intent(LoginActivity.this,MyService.class);
                                startService(intent1);
                                finish();
                            }
                        })
                        .addBodyPara("name",username.getText())
                        .addBodyPara("password",password.getText())
                        .post();

            }
        });
```
后端定义
```python
def login(request):
    '''登录'''
    try:
        if not request.method=="POST":#保证访问方式是post
            return JsonResponse({'status':-1,"exception":"method error"})
        info = request.POST.dict()
        print(info)
        (status,item,err)=db_utils.query(info,User)
        print(item)
        if status==0:
            if item:
                return JsonResponse({'status':0,"user_id":str(item[0]['user_id'])})
            else:
                return JsonResponse({'status':-1,"exception":'no user'})
        else:
            return JsonResponse({'status':-1,"exception":str(err)})
    except Exception as e:
        return JsonResponse({'status':-1,"exception":str(e)})
```