

Project中新建module是默认为lib的，现在要独立运行成App，就需要做一些更改。



# 要点

1. gradle中配置

  1. 顶部应用类型为app

    ```
    apply plugin: 'com.android.application
    ```

  2. 设置包名，即applicationId

     ```
     defaultConfig {
         applicationId "com.xxx.xxx.net"
         ...
     }
     ```

2.  androidMenifest中配置

   1. 配置application标签。包括icon、theme等

      ```xml
      	<application
              android:name=".App"
              android:allowBackup="true"
              android:icon="@drawable/ic_launcher"
              android:label="@string/app_name"
              android:roundIcon="@drawable/ic_launcher"
              android:supportsRtl="true"
              android:theme="@style/AppTheme"
              tools:ignore="GoogleAppIndexingWarning">
          
      		...
          
          </application>
      ```

      

   2. 配置程序入口，添加intent-filter

      ```xml
      		<activity android:name=".TestActivity">
                  <intent-filter>
                      <action android:name="android.intent.action.MAIN" />
      
                      <category android:name="android.intent.category.LAUNCHER" />
                  </intent-filter>
              </activity>
      ```

做到上面几点，就可以作为App运行了。



### 但是

我们既然组件化了，当然是要可以随时切换是否独立运行。这样就需要有个开关来控制，然后再来配置一下AndroidMenifest文件，就行了。



# 配置

1. 首先是设置是否组件化的开关，这个开关一般我们发在单独的gradle配置文件或gradle.properties文件中

   1. 1在confi.gradle中是这样的。当然你得在project的build.gradle中添加对config的引用。

      project的build.gradle添加

      ```
      apply from: "config.gradle"
      ```

      然后在config.gradle中添加

      ```
      ext {
          /** Net模块是否作为App存在*/
          isNetApp = false
      }
      ```

   2. 在gradle.properties中设置，是这样的

      ```
      #控制运行哪个模块（1：运行app模块 2：运行net模块）
      runModule=2
      ```

   3. 然后就是引用刚刚设置的变量

      ```
      if (rootProject.ext.isNetApp) {
          apply plugin: 'com.android.application'
      } else {
          apply plugin: 'com.android.library'
      }
      ```

      或者

      ```
      if (Integer.valueOf(runModule) == 1) {
          apply plugin: 'com.android.application'
      } else {
          apply plugin: 'com.android.library'
      }
      ```

      应该都明白怎么用了。

2. 开关设置好了，在要做切换的地方根据开关，来设置。主要在module的build.gradle中。

   ```
   if (rootProject.ext.isNetApp) {
       apply plugin: 'com.android.application'
   } else {
       apply plugin: 'com.android.library'
   }
   ...
   android {
       ...
       defaultConfig {
   
           if (rootProject.ext.isNetApp) {
               //组件模式下设置applicationId
               applicationId "com.xxx.xxx.net"
           }
           ...      
       }
       
       // 配置不同模式下的AndroidManifest文件地址
       sourceSets {
           main {
               if (rootProject.ext.isNetApp) {
                   manifest.srcFile 'src/main/AndroidManifest.xml'
               } else {
                   // 这里再在对应的文件夹下新建AndroidMenifest文件，并配置好activity等参数即可
                   manifest.srcFile 'src/main/module/AndroidManifest.xml'
               }
               jniLibs.srcDirs = ['libs']
           }
       }
   }
   ```



OK，完成。这样我们就可以在config.gradle中切换模块是否单独运行。