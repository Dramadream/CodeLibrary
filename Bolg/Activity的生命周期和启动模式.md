# Activity的生命周期


![Google官方图](https://developer.android.google.cn/images/activity_lifecycle.png)


Google官方给的图中，最主要的几个方法有7个。先来一个个分析。

   1. onCreate()

      Activity的第一个方法，整个生命周期中只会被调用一次。
      
      一般我们在里面做一些初始化操作，如setContentView，findViewById等。

   2. onStart()

      执行此方法时，Activity已显示，但不在前台，不可交互。一般会被多次调用。
      
      一般也是在这里进行初始化操作，只是我们习惯在onCreate中进行。

   3. onResume()

      执行此方法时，Activity以在前台，可以与用户交互，Activity进入Resumed状态。
      
      一般动画的初始化在这里进行。还有重新初始化在onPause和onStop中释放的资源。

   4. onPause()

      执行此方法时，表示Activity正在停止，此时Activity可见，但不可交互。此方法必须在500毫秒（0.5秒）内执行完成，然后紧接着执行onStop方法和下个Activity的onResume方法，否则直接销毁Activity实例。
      
      不耗时的资源释放的操作可以在这里进行。

   5. onStop()

      执行此方法时，Activity不可见，仅在后台运行，Activity进入Stopped状态。
      
      一般也是执行资源的释放操作，不过也不能太耗时。

   6. onDestroy()

      执行此方法时，Activity正在被销毁。
      
      这里就是最终的资源的释放和回收操作的地方。

   7. onRestart()
     
      从onStop到onStart中间的方法。当Activity在后台运行一段时间之后重新被调用就会执行这个方法。

##### 还有其他几个比较重要的生命周期方法，这里一一列出来

   8. onSaveInstanceState()
   
      此方法在onPause之后，onStop之前。在点击Home键后调用，来保存状态。
      
   9. onRestoreInstanceState()
   10. onActivityResult()
   11. onNewIntent()
   
      当已有Activity实例，重用它时，会调用onNewIntent方法
      setIntent方法
      
   12. onAttachedToWindow()  onDetachedFromWindow()
   13. onWindowFocusChanged()  
     
      这是最标准的判断Activity是否已获得用户焦点的方法。


# LaunchMode（启动模式）


## LaunchMode的基本知识


先来复习一下。我们都知道Activity有四种启动模式。分别是：Standard，singleTop，singleTask，singleInstance。

   1. Standard，标准模式。
   
      每次使用Activity都会创建新的Activity实例，并加入到任务栈中。
      
   2. singleTop，栈顶复用模式
   
      使用Activity时。若已有Activity的实例在任务栈栈顶时，不会创建新的实例，而是复用栈顶的Activity实例；若没有实例，或已有实例但不在栈顶，都会创建新的实例使用。
      
   3. singleTask，栈内复用模式 

      使用Activity时。若栈已有实例存在，则清除任务栈中该Activity实例上面的其它Activity，将它们销毁，将该Activity实例至于栈顶。
      
   4. singleInstance，单独占用一个任务栈

      整个Application中只有一个该Activity的实例，并且该实例享用一个单独的Task。如果，有其它应用也想使用这个Activity实例，则两个App公用同一个实例。
      
## LaunchMode 的特殊情况

### SingleTask，即栈内复用时，会有特殊情况

有两个任务栈，分属于两个不同的App，后台任务栈CD都是singleTask模式。这时，B调用D，连续按Back，Activity的出栈顺序是DCBA，而不是DBAC

![](https://img-blog.csdn.net/20160723233834035?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

![](https://img-blog.csdn.net/20160726092218754)

而如果B调用C，则出栈顺序就是CBA了，因为在调用C的时候，D已经被移除了。