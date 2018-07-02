#### 1. XML中的使用

​	1. 多种情形

``` xml
<com.facebook.drawee.view.SimpleDraweeView
  android:id="@+id/my_image_view"
  android:layout_width="20dp"
  android:layout_height="20dp"
  fresco:fadeDuration="300"
  fresco:actualImageScaleType="focusCrop"   // 真正的图片的缩放类型
  fresco:placeholderImage="@color/wait_color"// 占位图
  fresco:placeholderImageScaleType="fitCenter"// 占位图的缩放类型
  fresco:failureImage="@drawable/error"// 失败图片
  fresco:failureImageScaleType="centerInside"// 失败图片的缩放类型
  fresco:retryImage="@drawable/retrying"// 点击重试的图片
  fresco:retryImageScaleType="centerCrop"// 点击重试的图片的缩放类型
                                           
  // 进度条图片，深蓝色的矩形进度条，可自定义                                         
  fresco:progressBarImage="@drawable/progress_bar"
                                           
  fresco:progressBarImageScaleType="centerInside" // 进度条图片的缩放类型
  fresco:progressBarAutoRotateInterval="1000" //

  // 背景图，最底层图片，不支持缩放，XML中只能设置一个，代码中可以指定多个
  fresco:backgroundImage="@color/blue"  
  // 叠加图，最上层图片，不支持缩放，XML中只能设置一个，代码中可以指定多个
  fresco:overlayImage="@drawable/watermark" 
  // 按压图 同样不支持缩放，用户按压DraweeView时呈现。
  fresco:pressedStateOverlayImage="@color/red"

  // 圆角 
  // 1.目前只有占位图片和实际图片可以实现圆角
  // 2.动画不能圆角
  fresco:roundAsCircle="false"         // 圆形图片   
  fresco:roundedCornerRadius="1dp"     // 圆角图片的角的半径，四角不同半径只能在代码中设置
  fresco:roundTopLeft="true"
  fresco:roundTopRight="false"
  fresco:roundBottomLeft="false"
  fresco:roundBottomRight="true"
  fresco:roundWithOverlayColor="@color/corner_color"
  fresco:roundingBorderWidth="2dp"// 圆圈的粗细
  fresco:roundingBorderColor="@color/border_color"// 圆圈的颜色
  />
```

​	2.固定宽高比

```xml
<!-- 宽高比 -->
<com.facebook.drawee.view.SimpleDraweeView
    android:id="@+id/my_image_view"
    android:layout_width="20dp"
    android:layout_height="wrap_content"
    fresco:viewAspectRatio="1.33"/>
```

#### 2. 代码中的使用

##### 2.1 多图的情况(缩略图和大图一起)

​	动图无法在低分辨率那一层显示，先显示低分辨率的图，然后是高分辨率的图

```java
Uri lowResUri, highResUri;
DraweeController controller = Fresco.newDraweeControllerBuilder()
    .setLowResImageRequest(ImageRequest.fromUri(lowResUri))
    .setImageRequest(ImageRequest.fromUri(highResUri))
    .setOldController(mSimpleDraweeView.getController())
    .build();
mSimpleDraweeView.setController(controller);
```

##### 2.2 自动旋转

如果看到的图片是侧着的，用户会非常难受。许多设备会在 JPEG 文件的 metadata 中记录下照片的方向。如果你想图片呈现的方向和设备屏幕的方向一致，你可以简单地这样做到:

```java
ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
    .setAutoRotateEnabled(true)
    .build();
// as above
```