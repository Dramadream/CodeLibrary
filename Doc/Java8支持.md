## 在Android Studio中编译运行Java8支持的应用需要做下面的事情

#### 在app的build.gradle中添加

```java
android {
    ...
    defaultConfig {
        ...
        jackOptions {
            enabled true
        }
    }
    ...
    compileOptions {
      sourceCompatibility JavaVersion.VERSION_1_8
      targetCompatibility JavaVersion.VERSION_1_8
    }
}
```

