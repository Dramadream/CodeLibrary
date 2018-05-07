## 最常用的操作符

### timer  延时器

### interval   定时器

### takeUntil  条件停止发送

```java
AObservable.takeUntil(BObservable)
```

意思：AObsevable监听BObservable。AObservable会照常发送数据，直到BObsevable开始发送数据，AObservable就会停止发送。

### filter 过滤

