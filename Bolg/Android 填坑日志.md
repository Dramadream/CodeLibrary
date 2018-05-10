# RecyclerView CheckBox 复用问题

1. 单纯的复用问题很好解决，在adapter中设置集合或map、set记录选中的item的index即可

2. 单选问题

   单选。某个item的事件会涉及到其它item的UI，这时我们一般的操作是修改某中数据，然后notifyDataSetChanged()，刷新全局，来达到使其它界面也修改的目的。但是会报如下错误：

   ```
   java.lang.IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling
   ```

   翻译：不能在RecyclerView计算layout或者滑动的时候使用 notifyDataSetChanged() 方法 

   解决方法：在notifyDataSetChanged()时对Rv的情况判断。

   ​		具体到本问题中就是在checkedChangeListener之外设置条件。代码如下：

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



## EditText 焦点问题

#### 1. 禁止EditText自动获取焦点

在父布局中加入下面属性即可

```xml
 android:focusable="true"//是否可聚焦
 android:focusableInTouchMode="true"//是否是触摸方式获取焦点
```

