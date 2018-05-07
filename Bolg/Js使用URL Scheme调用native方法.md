# 什么是 URL Schema？ 

   android中的scheme是一种页面内跳转协议，通过定义自己的scheme协议，可以非常方便跳转app中的各个页面；通过scheme协议，服务器可以定制化告诉App跳转哪个页面，可以通过通知栏消息定制化跳转页面，可以通过H5页面跳转页面等。

## 使用流程

   1. 先让Activity注册实现schema
   2. Activity实现可以接收schema传过来的参数的方法
   3. 浏览器在shouldOverrideUrlLoading()方法中实现拦截URL判断

## 具体实现

### 1.先让Activity注册实现schema

   先定义好协议，比如msl://userpage表示打开用户界面Activity。那么现在Menifest文件中注册Activity，如下：

   

```xml
<activity  
    android:name=".userpage.UserPageActivity"  
    android:screenOrientation="portrait">  
    <intent-filter>  
        <action android:name="android.intent.action.VIEW" />  
        <category android:name="android.intent.category.DEFAULT" />  
  
        <data android:scheme="mls" />  
        <data android:host="userpage" />  
    </intent-filter>  
</activity>  
```

   其中schema表示这个链接的前缀，host代表短链的名字。如果你要在你的schema里面传参数，比如你要传uid和user_type，那么就跟普通的url的get参数格式一样：mls://userpage?uid=123&user_type=mogujie 。注意，中间千万不能有空格。那Activity怎么接收参数呢，往下看。

### 2.Activity实现可以接收schema传过来的参数的方法

```java
String uid,userType;  
private void parseUriParams() {  
    Uri uri = getIntent().getData();  
    if (uri != null) {  
        uid = uri.getQueryParameter("uid");  
        userType = uri.getQueryParameter("user_type");  
    }  
}  
```

### 3.浏览器在shouldOverrideUrlLoading()方法中实现拦截URL判断

```java
@Override  
public boolean shouldOverrideUrlLoading(WebView view, String url) {  
    if (url.startsWith("mls://")) {  
        Intent intent = new Intent();  
        intent.setData(Uri.parse(url));  
        startActivity(intent);  
        Log.v("tag_2", url);  
        return true;  
    }  
    return super.shouldOverrideUrlLoading(view, url);  
} 
```

