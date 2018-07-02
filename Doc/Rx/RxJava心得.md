在RxJava2里面，Observable、Flowable、Single、Maybe、Completable 这几个在使用起来区别不大，因为他们都可以用一个或多个函数式接口作为参数进行订阅（subscribe），需要几个传几个就可以了。但是从各个的设计初衷来讲，个人感觉最适用于网络请求这种情况的是Single 和 Completable。

网络请求是一个Request对应一个 Response，不会出现背压情况，所以不考虑 Flowable；

网络请求是一个Request对应一个 Response，不是一个连续的事件流，所以在 onNext 被调用之后，onComplete 就会被马上调用，所以只需要 onNext 和 onComplete 其中一个就够了，不考虑 Observable、Maybe ；

对于关心ResponseBody的情况，Single适用；

对于不关心ResponseBody的情况，Completable适用。

