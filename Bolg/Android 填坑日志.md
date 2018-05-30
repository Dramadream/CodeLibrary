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



# 2. EditText 焦点问题

1. 禁止EditText自动获取焦点

在父布局中加入下面属性即可

```xml
 android:focusable="true"//是否可聚焦
 android:focusableInTouchMode="true"//是否是触摸方式获取焦点
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

   