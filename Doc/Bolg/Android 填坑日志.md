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

