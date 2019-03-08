# Window

**Window**: 定义窗口样式和行为的抽象基类，用于作为顶层的view加到WindowManager中，其实现类是PhoneWindow。
 每个Window都需要指定一个Type（应用窗口、子窗口、系统窗口）。Activity对应的窗口是应用窗口；PopupWindow，ContextMenu，OptionMenu是常用的子窗口；像Toast和系统警告提示框（如ANR）就是系窗口，还有很多应用的悬浮框也属于系统窗口类型。

**WindowManager**：用来在应用与window之间的管理接口，管理窗口顺序，消息等。

**WindowManagerService**：简称Wms，WindowManagerService管理窗口的创建、更新和删除，显示顺序等，是WindowManager这个管理接品的真正的实现类。它运行在System_server进程，作为服务端，客户端（应用程序）通过IPC调用和它进行交互。

**Token**：这里提到的Token主是指窗口令牌（Window Token），是一种特殊的Binder令牌，Wms用它唯一标识系统中的一个窗口。

 

 

 

 

 # Context



![Context](https://upload-images.jianshu.io/upload_images/1187237-1b4c0cd31fd0193f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/628/format/webp)



1：如果我们用ApplicationContext去启动一个LaunchMode为standard的Activity的时候会报错`android.util.AndroidRuntimeException: Calling startActivity from outside of an Activity context requires the FLAG_ACTIVITY_NEW_TASK flag. Is this really what you want?`这是因为非Activity类型的Context并没有所谓的任务栈，所以待启动的Activity就找不到栈了。解决这个问题的方法就是为待启动的Activity指定FLAG_ACTIVITY_NEW_TASK标记位，这样启动的时候就为它创建一个新的任务栈，而此时Activity是以singleTask模式启动的。所有这种用Application启动Activity的方式不推荐使用，Service同Application。
 2：在Application和Service中去layout inflate也是合法的，但是会使用系统默认的主题样式，如果你自定义了某些样式可能不会被使用。所以这种方式也不推荐使用。
 一句话总结：凡是跟UI相关的，都应该使用Activity做为Context来处理；其他的一些操作，Service,Activity,Application等实例都可以，当然了，注意Context引用的持有，防止内存泄漏。

