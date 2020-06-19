# 1. RecyclerView CheckBox 复用问题

1. 单纯的复用问题很好解决，在adapter中设置集合或map、set记录选中的item的index即可

2. 单选问题


   单选。某个item的事件会涉及到其它item的UI，这时我们一般的操作是修改某中数据，然后notifyDataSetChanged()，刷新全局，来达到使其它界面也修改的目的。但是会报如下错误：

   ```
   java.lang.IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling
   ```

   翻译：不能在RecyclerView计算layout或者滑动的时候使用 notifyDataSetChanged() 方法 

   解决方法：在notifyDataSetChanged()时对Rv的情况判断。

   		具体到本问题中就是在checkedChangeListener之外设置条件。代码如下：

   ```java
   /*
    * 单选时，只需记录index，即可
    */
   cbItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
       @Override
       public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
           if (isChecked) {
               mIndex = index;
           }
           if (!onBind) {
               notifyDataSetChanged();
           }
       }
   });
   onBind = true;
   cbItem.setChecked(index == mIndex);
   onBind = false;
   ```

```java
if (recycleview.getScrollState() == RecyclerView.SCROLL_STATE_IDLE || (leagues_recycleview.isComputingLayout() == false)) {
         adpater.notifyDataSetChanged();
}
```



# 2. EditText 焦点问题

1. 禁止EditText自动获取焦点

在父布局中加入下面属性即可

```xml
//是否可聚焦 
android:focusable="true"
android:focusableInTouchMode="true"
//是否是触摸方式获取焦点
```

2.  解决有EditText的界面，软键盘和EditText焦点切换的问题

```java
/**
 * 处理EditText焦点问题
 */
@Override
public boolean dispatchTouchEvent(MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_DOWN) {
        View v = getCurrentFocus();
        if (v instanceof EditText) {
            Rect outRect = new Rect();
            v.getGlobalVisibleRect(outRect);
            if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                v.setFocusable(false);
                v.setFocusableInTouchMode(true);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }
    return super.dispatchTouchEvent(event);
}
```

3.  避免有两个EditText时，两个EditText互相争夺焦点的问题

解决方法同1

# 3. Layout 布局宽高失效的问题

1.  主要原因是没有加到Parent中。

**通过对比，发现宽高失效与不失效的区别在与Adapter中创建ViewHolder是加载布局的方式不同：**

```
LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_test_item,null)
```

以上这种加载方式Item宽高失效。

```
LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_test_item,parent,false)
```

2.  如果没有可添加的Parent，也可以做到。

方式就是保证最少有两层ViewGroup，外层为RelativeLayout，设置宽高为match_parent或wrap_content，这样View就会正常显示了。

```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/rlt_footer"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="100dp"
        android:layout_centerInParent="true"
        android:gravity="center">


        <ProgressBar
            android:id="@+id/progressBar_footer"
            style="?android:attr/progressBarStyle"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"/>

        <TextView
            android:id="@+id/tv"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginStart="16dp"
            android:gravity="center"
            android:text="@string/pull_to_load"
            android:textColor="@color/text_grey_B4B4B4"/>

    </LinearLayout>

</RelativeLayout>
```

  

# 4.多渠道打包

1. V2版签名

- [美团Walle](https://tech.meituan.com/android-apk-v2-signature-scheme.html)
- [腾讯VasDolly](https://github.com/Tencent/VasDolly)

# 5. ConstrainLayout

1. 基本属性

   ```
   layout_constraintRight_toLeftOf
   layout_constraintRight_toRightOf
   layout_constraintTop_toTopOf
   layout_constraintTop_toBottomOf
   layout_constraintBottom_toTopOf
   layout_constraintBottom_toBottomOf
   layout_constraintBaseline_toBaselineOf
   ```

   **注意baseline是指文字的baseline**

2. match_parent这个属性没有效果。可以用match_constrain来代替

3. 控制在父控件中的左右或者上下位置比例

   ```
   layout_constraintHorizontal_bias  //控件的水平偏移比例
   layout_constraintVertical_bias   //控件的垂直偏移比例
   ```

4. 固定宽高比例

   ```
   app:layout_constraintDimensionRatio="16:6"
   app:layout_constraintDimensionRatio="W,16:6"
   app:layout_constraintDimensionRatio="H,16:6"
   ```

5. 线性约束。包括权重和链的种类

   ```
   app:layout_constraintHorizontal_weight
   app:layout_constraintHorizontal_chainStyle
   ```

   weight就和LinearLayout中的用法一样

   - chainstyle：**spread(默认)**、**spread_inside**、**packed**
   - 可以通过设置weight、bias、width/height来灵活控制显示排列效果

6. GuideLine，不会显示的辅助线

   ```
   android:orientation					// 方向
   app:layout_constraintGuide_begin	    // 距离顶部或左边距离
   app:layout_constraintGuide_end		// 距离底部或右边的距离
   app:layout_constraintGuide_percent   // 距离顶部或左边的百分比。0.5/0.8
   ```


# 6. Glide集成出错

```
"void com.bumptech.glide.module.RegistersComponents.registerComponents(android.content.Context, com.bumptech.glide.Glide, com.bumptech.glide.Registry)"
```

这是要添加一个类并复写其中的方法

```java
@GlideModule
public class MyAppGlideModule extends AppGlideModule {
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
```
# 7. Dialog的属性

# 8.TextView相关

1. 动态设置字体大小

   ```java
   tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,15); //22像素 
   tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,15); //22SP 
   tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);//22DIP
   ```


# 9.View的onTouchListener问题

View中可以接收到ACTION_DOWN却不能收到ACTION_MOVE和ACTION_UP事件

要设置clickable为true，即可解决

```java
view.setClickable(true);
```
# 10. RecyclerView滑动相关的几个方法

![原理图 ](http://static.open-open.com/lib/uploadImg/20160919/20160919143609_419.png)

computeVerticalScroll**Extent**()是当前屏幕显示的区域高度；

computeVerticalScroll**Offset**() 是当前屏幕之前滑过的距离

computeVerticalScroll**Range**()是整个View控件的高度。

# 11. 截屏的实现

https://www.cnblogs.com/BoBoMEe/p/4556917.html

# 12.免费私有云

BitBucket：<https://bitbucket.org/>

 开源中国：<http://git.oschina.net/> 

GitCafe：<https://gitcafe.com/> 

GitLab：<http://www.gitlab.org/> 

coding：<https://coding.net/> 



# 13. EditText可变高度

注意下面的代码顺序不能错

```java
etAddComment.setHorizontallyScrolling(false);
etAddComment.setImeOptions(EditorInfo.IME_ACTION_SEND);
etAddComment.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
etAddComment.setOnEditorActionListener(this);
etAddComment.setSingleLine(false);
etAddComment.setMaxLines(3);
```

# 14.GestureDetector

**1.OnGestureListener，这个Listener监听一些手势，如单击、滑动、长按等操作：** 
- **onDown(MotionEvent e):**用户按下屏幕的时候的回调。 
- **onShowPress(MotionEvent e)：**用户按下按键后100ms（根据Android7.0源码）还没有松开或者移动就会回调，官方在源码的解释是说一般用于告诉用户已经识别按下事件的回调（我暂时想不出有什么用途，因为这个回调触发之后还会触发其他的，不像长按）。 
- **onLongPress(MotionEvent e)：**用户长按后（好像不同手机的时间不同，源码里默认是100ms+500ms）触发，触发之后不会触发其他回调，直至松开（UP事件）。 
- **onScroll(MotionEvent e1, MotionEvent e2,float distanceX, float distanceY)：**手指滑动的时候执行的回调（接收到MOVE事件，且位移大于一定距离），e1,e2分别是之前DOWN事件和当前的MOVE事件，distanceX和distanceY就是当前MOVE事件和上一个MOVE事件的位移量。 
- **onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY)：**用户执行抛操作之后的回调，MOVE事件之后手松开（UP事件）那一瞬间的x或者y方向速度，如果达到一定数值（源码默认是每秒50px），就是抛操作（也就是快速滑动的时候松手会有这个回调，因此基本上有onFling必然有onScroll）。 
- **onSingleTapUp(MotionEvent e)：**用户手指松开（UP事件）的时候如果没有执行`onScroll()`和`onLongPress()`这两个回调的话，就会回调这个，说明这是一个点击抬起事件，但是不能区分是否双击事件的抬起。

**2.OnDoubleTapListener，这个Listener监听双击和单击事件。** 
- **onSingleTapConfirmed(MotionEvent e)：**可以确认（通过单击DOWN后300ms没有下一个DOWN事件确认）这不是一个双击事件，而是一个单击事件的时候会回调。 
- **onDoubleTap(MotionEvent e)：**可以确认这是一个双击事件的时候回调。 
- **onDoubleTapEvent(MotionEvent e)：**`onDoubleTap()`回调之后的输入事件（DOWN、MOVE、UP）都会回调这个方法（这个方法可以实现一些双击后的控制，如让View双击后变得可拖动等）。

**3.OnContextClickListener，很多人都不知道ContextClick是什么，我以前也不知道，直到我把平板接上了外接键盘——原来这就是鼠标右键。。。** 
- **onContextClick(MotionEvent e)：**当鼠标/触摸板，右键点击时候的回调。

**4.SimpleOnGestureListener，实现了上面三个接口的类，拥有上面三个的所有回调方法。** 
\- 由于SimpleOnGestureListener不是抽象类，所以继承它的时候只需要选取我们所需要的回调方法来重写就可以了，非常方便，也减少了代码量，符合接口隔离原则，也是模板方法模式的实现。而实现上面的三个接口中的一个都要全部重写里面的方法，所以我们一般都是选择SimpleOnGestureListener。

# 15.RelativeLayout和LinearLayout性能比较 

https://blog.csdn.net/guyuealian/article/details/52162774

# 16.Dialog根布局为ConstraintLayout会出Bug

# 17.TabLayout点击效果颜色

```java
tabLayout.setTabRippleColor(ColorStateList.valueOf(getResources().getColor(R.color.bg_top_title_2A253C)));
tabLayout.setUnboundedRipple(true);
```

# 18.Dialog 生命周期

	1. 点击显示按钮，第一次显示Dialog，然后按BACK键返回。
	show() —> onCreate() —> onStart();
	cancel() —> onDismiss() —> Stop();
	2. 再次点击显示按钮，然后点击Dialog外部。
	show() —> onStart();
	cancel() —> onDismiss() —> Stop();
	3. 再次点击显示按钮，然后执行Dialog.dismiss() 方法。
	show() —> onStart();
	onDismiss() —> Stop();

# 19. 部分机型无法显示Toast

1.确定系统中打开了通知权限；

2.确定Manifest中添加了统治权限

```xml
<!-- 通知权限 -->
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```
# 20.显示透明Activity

```xml
<style name="translucent" parent="AppTheme">
    <item name="android:windowBackground">@color/translucent_background</item>
    <item name="android:windowIsTranslucent">true</item>
    <item name="android:windowAnimationStyle">@android:style/Animation.Translucent</item>
</style>
```

```xml
<color name="translucent_background">#00000000</color>
```

再在AndroidManifest中给Activity指定Theme



# 21.Gradle 配置

https://www.jb51.net/article/145005.htm

```
buildTypes {// 生产/测试环境配置
  release {// 生产环境
   buildConfigField("boolean", "LOG_DEBUG", "false")//配置Log日志
   buildConfigField("String", "URL_PERFIX", "\"https://release.cn/\"")// 配置URL前缀
   minifyEnabled false//是否对代码进行混淆
   proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'//指定混淆的规则文件
   signingConfig signingConfigs.release//设置签名信息
   pseudoLocalesEnabled false//是否在APK中生成伪语言环境，帮助国际化的东西，一般使用的不多
   zipAlignEnabled true//是否对APK包执行ZIP对齐优化，减小zip体积，增加运行效率
   applicationIdSuffix 'test'//在applicationId 中添加了一个后缀，一般使用的不多
   versionNameSuffix 'test'//在applicationId 中添加了一个后缀，一般使用的不多
  }
  debug {// 测试环境
   buildConfigField("boolean", "LOG_DEBUG", "true")//配置Log日志
   buildConfigField("String", "URL_PERFIX", "\"https://test.com/\"")// 配置URL前缀
   minifyEnabled false//是否对代码进行混淆
   proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'//指定混淆的规则文件
   signingConfig signingConfigs.debug//设置签名信息
   debuggable false//是否支持断点调试
   jniDebuggable false//是否可以调试NDK代码
   renderscriptDebuggable false//是否开启渲染脚本就是一些c写的渲染方法
   zipAlignEnabled true//是否对APK包执行ZIP对齐优化，减小zip体积，增加运行效率
   pseudoLocalesEnabled false//是否在APK中生成伪语言环境，帮助国际化的东西，一般使用的不多
   applicationIdSuffix 'test'//在applicationId 中添加了一个后缀，一般使用的不多
   versionNameSuffix 'test'//在applicationId 中添加了一个后缀，一般使用的不多
  }
 }
```

release{}闭包和debug{}闭包两者能配置的参数相同，最大的区别默认属性配置不一样：

- minifyEnabled：表明是否对代码进行混淆，true表示对代码进行混淆，false表示对代码不进行混淆，默认的是false。
- proguardFiles：指定混淆的规则文件，这里指定了proguard-android.txt文件和proguard-rules.pro文件两个文件，proguard-android.txt文件为默认的混淆文件，里面定义了一些通用的混淆规则。proguard-rules.pro文件位于当前项目的根目录下，可以在该文件中定义一些项目特有的混淆规则。
- buildConfigField：用于解决Beta版本服务和Release版本服务地址不同或者一些Log打印需求控制的。例如：配置buildConfigField("boolean", "LOG_DEBUG", "true")，这个方法接收三个非空的参数，第一个：确定值的类型，第二个：指定key的名字，第三个：传值，调用的时候BuildConfig.LOG_DEBUG即可调用。
- debuggable：表示是否支持断点调试，release默认为false，debug默认为true。
- jniDebuggable：表示是否可以调试NDK代码，使用lldb进行c和c++代码调试，release默认为false
- signingConfig：设置签名信息，通过signingConfigs.release或者signingConfigs.debug，配置相应的签名，但是添加此配置前必须先添加signingConfigs闭包，添加相应的签名信息。
- renderscriptDebuggable：表示是否开启渲染脚本就是一些c写的渲染方法，默认为false。
- renderscriptOptimLevel：表示渲染等级，默认是3。
- pseudoLocalesEnabled：是否在APK中生成伪语言环境，帮助国际化的东西，一般使用的不多。
- applicationIdSuffix：和defaultConfig中配置是一的，这里是在applicationId 中添加了一个后缀，一般使用的不多。
- versionNameSuffix：表示添加版本名称的后缀，一般使用的不多。
- zipAlignEnabled：表示是否对APK包执行ZIP对齐优化，减小zip体积，增加运行效率，release和debug默认都为true。

# 22.Okhttp cancel会造成onFaliure回调的解决方法

测试发现不同的失败类型返回的IOException e 不一样，所以可以通过e.toString 中的关键字来区分不同的错误类型 

```
自己主动取消的错误的 java.net.SocketException: Socket closed
超时的错误是 java.net.SocketTimeoutException
网络出错的错误是java.net.ConnectException: Failed to connect to xxxxx
```

SO： 可以这样方便的处理

```java
 call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(e.toString().contains("closed")) {
                 //如果是主动取消的情况下
                }else{
                  //其他情况下
            }
     ....
```

# 23. ScrollView有时会自动滑到最底部的解决办法

​	当ScrollView里面布局很长的时候，Scroll会自动滑动到底部。原因可能是底部获取到了焦点。解决方法是**将焦点重新设置到上部的某个部件即可**，方法如下： 

```java
上部分的某控件.setFocusable(true);
上部分的某控件.setFocusableInTouchMode(true);
上部分的某控件.requestFocus();
```
# 24.点击水波纹

```
android:background="?attr/selectableItemBackgroundBorderless"
android:background="?attr/selectableItemBackground"
```
# 25 微信进程保活

1. 进程拆分

将网络相关的，放到push进程中，这样，占用的内存非常小，这样就能尽量减少push被杀死的可能。

题外话：微信3个进程：UI进程，Push进程，Gallery和WebView进程

2. 进程保活

   push有AlarmReceiver， ConnectReceiver，BootReceiver。这些receiver 都可以在push被杀后，重新拉起。特别AlarmReceiver ，结合心跳逻辑，微信被杀后，重新拉起最多一个心跳周期。  

   历史原因，我们在push和worker通信使用Broadcast和AIDL。 

3. 提高进程优先级



# 26 Netty学习资料

<https://github.com/BazingaLyn/netty-study> 

# 27 关于Android 架构的一些思考

1.为什么需要架构

大型项目，多人开发，问题很多，大大的降低了开发效率。

所以，最终目的是为了提高并行开发的效率和可维护性。

2.如何架构

其实最根本的核心原则就是单一职责原则。每个人负责每个人该写的代码，每个类有自己独特的职能。

而做到这些的手段就是解耦，将大的项目分解成为N个小项目，再在小项目中分解为不同的包，不同的类。

3.项目级别的架构：

模块化，组件化，插件化。

这种事根据模块来纵向划分。而横向划分，一般是划分出层次，具体到某个业务的架构：MVX，X指P,C,VM等



# 28 手机归属地查询

百度  GET
http://mobsec-dianhua.baidu.com/dianhua_api/open/location?tel=18692276974
淘宝  GET
https://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=18692276974



# 29.安装Apk

1. 8.0以上需要加上

```
<!-- 允许安装apk，8.0 以上需要加上 -->
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"/>
```

2. 然后在安装的地方检测是否有权限，如果没权限，要去申请。

```java
/**
 * 检测是否有权限
 */
@RequiresApi (api = Build.VERSION_CODES.O)
private boolean isHasInstallPermissionWithO(Context context){
    if (context == null){
        return false;
    }
    return context.getPackageManager().canRequestPackageInstalls();
}
```



```java
/**
 * 开启设置安装未知来源应用权限界面
 * @param context
 */
@RequiresApi (api = Build.VERSION_CODES.O)
private void startInstallPermissionSettingActivity(Context context) {
    if (context == null){
        return;
    }
    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
    ((Activity)context).startActivityForResult(intent,REQUEST_CODE_APP_INSTALL);
}
```



```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== Activity.RESULT_OK ){
            when(requestCode){
                REQUEST_CODE_APP_INSTALL -> {
                    onSettingCheckUpdate()
                }
            }
        }
    }
```

# 30.Matisse的使用



```java
Matisse.from(this)
        // 资源类型，图片还是视频还是其它
        .choose(MimeType.ofImage())
        // 是否支持拍照
        .capture(true)
        // 7.0 以上拍照需要的FileProvider路径
        .captureStrategy(MatisseUtil.getCaptureStrategy())
        // 图片加载
        .imageEngine(new Glide4Engine())
        // 最多选几张
        .maxSelectable(count)
        // 选中后是否显示数字
        .countable(true)
        .forResult(REQUEST_CODE_CHOOSE);
```

- 关于FileProvider路径

  - 在res/xml下新建file_paths_public.xml

  ```xml
  <paths>
      <external-path
          name="my_images"
          path="Pictures"/>
  </paths>
  ```

  - manifest的Application下添加。注意resource的名字和刚刚新建的要一样

  ```xml
  <provider
      android:name="android.support.v4.content.FileProvider"
      android:authorities="${applicationId}.fileprovider"
      android:exported="false"
      android:grantUriPermissions="true">
      <meta-data
          android:name="android.support.FILE_PROVIDER_PATHS"
          android:resource="@xml/file_paths_public">
      </meta-data>
  </provider>
  ```

# 31.线程池

核心线程，任务队列，非核心线程。



1. 线程池执行任务的原则

   1. 线程数未达到核心线程的最大值，会直接启动核心线程来执行任务；
   2. 线程已达到或超过核心线程的最大值，那么任务会被插到任务队列中排队等待执行；
   3. 任务队列已满，且非核心线程未满，那么会启动新的非核心线程来执行任务；
   4. 任务队列已满，非核心线程也满，会抛出异常RejectedExecution。

2. 普通线程池的创建

   ```java
   /**
    * @param corePoolSize    核心线程数量，除非allowCoreThreadTimeOut被设置为True，否则它空闲时也不会死
    * @param maximumPoolSize 最大线程数，减去corePoolSize就是最大非核心线程数量了
    * @param keepAliveTime   闲置时长，超时后线程就会被回收，作用与非核心线程(allowCoreThreadTimeOut为True时，也会对核心线程生效)。当任务很多，每个任务执行时间很短的情况下调大该值有助于提高线程利用率。
    * @param unit            闲置时长的单位
    * @param workQueue       缓冲任务队列
    * @param threadFactory   线程工厂，可用于设置线程名字等等，一般无须设置该参数。
    */
   public ThreadPoolExecutor(int corePoolSize,
                             int maximumPoolSize,
                             long keepAliveTime,
                             TimeUnit unit,
                             BlockingQueue<Runnable> workQueue,
                             ThreadFactory threadFactory) {
       ...
   }
   ```

   

3. 四种主要的线程池

4. FixedThreadPool

   ```java
       public static ExecutorService newFixedThreadPool(int nThreads) {
           // 两个Size数量一样，只有核心线程
           return new ThreadPoolExecutor(nThreads, nThreads,
                                         // 闲置时不会被回收
                                         0L, TimeUnit.MILLISECONDS,
                                         // 无上限的基于链表的队列，这里表明可以有无限制个任务在排队
                                         new LinkedBlockingQueue<Runnable>());
       }
   ```

   - 只有核心线程，且线程数量固定的线程池，线程不会被回收，除非线程池关闭。
   - 当线程池被填满后，新的任务会处于等待状态，直到有新的线程闲置，且任务队列数量无上限。

   FixedThreadPool适合**执行长期的任务，性能好很多** 。

   

5. CachedThreadPool

   ```java
   public static ExecutorService newCachedThreadPool() {
       // 没有核心线程，只有非核心线程
       return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                     // 闲置时间为60秒
                                     60L, TimeUnit.SECONDS,
                                     // SynchronousQueue是没有容量的无缓冲队列，意思是，加入到这个队列之后，会立马被移出去。这里就移到了非核心线程中
                                     new SynchronousQueue<Runnable>());
   }
   ```

   - 只有非核心线程，线程数量不固定，最大线程数为Integer.MAX_VALUE，线程闲置超时时间为60秒。

   - SynchronousQueue是不存储元素的，每次插入操作必须伴随一个移除操作，一个移除操作也要伴随一个插入操作。 

   - 当一个任务执行时，先用SynchronousQueue的offer提交任务，如果线程池中有线程空闲，则调用SynchronousQueue的poll方法来移除任务并交给线程处理；如果没有线程空闲，则开启一个新的非核心线程来处理任务。
   - 由于maximumPoolSize是无界的，所以如果线程处理任务速度小于提交任务的速度，则会不断地创建新的线程，这时需要注意不要过度创建，应采取措施调整双方速度，不然线程创建太多会影响性能。
   - CachedThreadPool适用于**有大量需要立即执行的耗时少的任务**的情况。 

   

6. SingleThreadPool

   ```java
   public static ExecutorService newSingleThreadExecutor() {
       return new FinalizableDelegatedExecutorService
           // 只有一个核心线程，且不会被主动回收
           (new ThreadPoolExecutor(1, 1,
                                   0L, TimeUnit.MILLISECONDS,
                                   // 无上限的基于链表的队列，这里表明可以有无限制个任务在排队
                                   new LinkedBlockingQueue<Runnable>()));
   }
   ```

   只有一个核心线程，其它的任务可能要等待，所有的任务都在同一个线程中执行。

7. ScheduledThreadPool

   核心线程数量固定，主要用于执行定时或周期任务。

   非核心线程数量不固定，但闲置时会被回收。

   用法和其它的定时器差不对。



4. 任务队列

   由于上面的构造方法涉及到了阻塞队列，所以补充一些阻塞队列的知识。
    阻塞队列：我的理解是，生产者——消费者，生产者往队列里放元素，消费者取，如果队列里没有元素，消费者线程取则阻塞，如果队列里元素满了，则生产者线程阻塞。

   常见的阻塞队列有下列7种：

   ```
   ArrayBlockingQueue ：一个由数组结构组成的有界阻塞队列。
   LinkedBlockingQueue ：一个由链表结构组成的有界阻塞队列。
   PriorityBlockingQueue ：一个支持优先级排序的无界阻塞队列。
   DelayQueue：一个使用优先级队列实现的无界阻塞队列。
   SynchronousQueue：一个不存储元素的阻塞队列。
   LinkedTransferQueue：一个由链表结构组成的无界阻塞队列。
   LinkedBlockingDeque：一个由链表结构组成的双向阻塞队列。
   ```

5. 其它方法

   ```java
   1.shutDown()  关闭线程池，不影响已经提交的任务
   
   2.shutDownNow() 关闭线程池，并尝试去终止正在执行的线程
   
   3.allowCoreThreadTimeOut(boolean value) 允许核心线程闲置超时时被回收
   
   4.submit 一般情况下我们使用execute来提交任务，但是有时候可能也会用到submit，使用submit的好处是submit有返回值。
   
   5.beforeExecute() - 任务执行前执行的方法
   
   6.afterExecute() -任务执行结束后执行的方法
   
   7.terminated() -线程池关闭后执行的方法
   ```



参考链接：

- [Android 线程池原理及使用](https://www.jianshu.com/p/7b2da1d94b42 )
- [常见的四种线程池和区别](https://www.cnblogs.com/1925yiyi/p/9040605.html )
- [Android开发——Android中常见的4种线程池（保证你能看懂并理解）](https://blog.csdn.net/seu_calvin/article/details/52415337 )



# 32.需要注意的技术

- FlexboxLayout，FlowLayout 流式布局
- 



# 33.ARouter

1. path是指支持路由的界面上添加的，这里的意思其实是具体实现类的路径，所以注意path至少两级，并且顶级姚宇module相同。

2. 疑问：`IProvider可不可以有两个实现类，两个实现类的注解路径怎么处理？`

3. ARouter传递对象的方法

   1. 使用withObject：需要添加对该对象的处理类，实现**SerializationService**，并在目标界面实例化该类

      ```java
      // 使用withObject传递数据
      ARouter.getInstance().build("/test/1")
                  .withObejct("key4", new Test("Jack", "Rose"))
                  .navigation();
                  
      /**
       * 处理传递参数中自定义的Object---》withObject，实现SerializationService。
       * Object 和 json 的相互转换
       */
      @Route(path = "/custom/json")
      public class JsonSerializationService implements SerializationService {
          Gson gson;
          @Override
          public <T> T json2Object(String input, Class<T> clazz) {
              return gson.fromJson(input,clazz);
          }
          @Override
          public String object2Json(Object instance) {
              return gson.toJson(instance);
          }
          @Override
          public <T> T parseObject(String input, Type clazz) {
              return gson.fromJson(input,clazz);
          }
          @Override
          public void init(Context context) {
              gson = new Gson();
          }
      }
      
      
      /**
       * 目标Activity的处理
       */
      @Route(path = "/test/1")
      public class YourActivity extend Activity {
          ...
          SerializationService serializationService = ARouter.getInstance().navigation(SerializationService.class);
          serializationService.init(this);
          User obj = serializationService.parseObject(getIntent().getStringExtra("key4"), User.class);
      }
      
      ```

4. Uri跳转，不会

5. 跳转结果也可以监听

6. 拦截器为什么不能是内部类？

   ```java
   // 创建拦截器对象的方法是使用反射。
   // 而对于非静态内部类，使用getConstructor()的方式是获取不到构造器的
   IInterceptor iInterceptor = interceptorClass.getConstructor().newInstance();
   ```






# 34. 多Module混淆

两种方法

1. 全部在主模块中配置混淆信息

2. 在各自的模块中配置混淆信息

   需要在Gradle中配置混淆信息

   ```java
   release {    consumerProguardFiles 'proguard-rules.pro'}
   ```



# 35 组件化

https://juejin.im/post/5b5f17976fb9a04fa775658d?tdsourcetag=s_pctim_aiomsg

https://blog.csdn.net/guiying712/article/details/55213884?tdsourcetag=s_pctim_aiomsg



# 36 后台任务

https://www.cnblogs.com/qoix/p/9649322.html

| 场景                           | 推荐                          |
| ------------------------------ | ----------------------------- |
| 需系统触发，不必完成           | ThreadPool + Broadcast        |
| 需系统触发，必须完成，可推迟   | WorkManager                   |
| 需系统触发，必须完成，立即     | ForegroundService + Broadcast |
| 不需系统触发，不必完成         | ThreadPool                    |
| 不需系统触发，必须完成，可推迟 | WorkManager                   |
| 不需系统触发，必须完成，立即   | ForegroundService             |



# 37 ROOM

SQLite的ALTER TABLE命令[非常局限](https://link.jianshu.com/?t=https%3A%2F%2Fsqlite.org%2Flang_altertable.html)，只支持重命名表以及添加新的字段





# 38 6.0 7.0 8.0 升级问题记录

1. 6.0 动态权限申请
2. 7.0 应用间文件共享受限，需要使用FileProvider
3. 8.0 Notification必须要有Channel，且需要检查通知权限
4. 8.0 后台进程限制
5. 8.0 隐式广播不可用，必须使用显示广播
6. 8.0 悬浮窗类型更改