## 1.RxBus类，单例，对事件的统一管理



- 1、[Subject](http://reactivex.io/documentation/subject.html)同时充当了Observer和Observable的角色，Subject是非线程安全的，要避免该问题，需要将 Subject转换为一个 [SerializedSubject](http://reactivex.io/RxJava/javadoc/rx/subjects/SerializedSubject.html)，上述RxBus类中把线程非安全的PublishSubject包装成线程安全的Subject。


- 2、PublishSubject只会把在订阅发生的时间点之后来自原始Observable的数据发射给观察者。


- 3、ofType操作符只发射指定类型的数据，其内部就是filter+cast

```java
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class RxBus {

    private static volatile RxBus mInstance;

    private final Subject bus;


    public RxBus()
    {
        bus = new SerializedSubject<>(PublishSubject.create());
    }

    /**
     * 单例模式RxBus
     *
     * @return
     */
    public static RxBus getInstance()
    {

        RxBus rxBus2 = mInstance;
        if (mInstance == null)
        {
            synchronized (RxBus.class)
            {
                rxBus2 = mInstance;
                if (mInstance == null)
                {
                    rxBus2 = new RxBus();
                    mInstance = rxBus2;
                }
            }
        }

        return rxBus2;
    }


    /**
     * 发送消息
     *
     * @param object
     */
    public void post(Object object)
    {

        bus.onNext(object);

    }

    /**
     * 接收消息
     *
     * @param eventType
     * @param <T>
     * @return
     */
    public <T> Observable<T> toObserverable(Class<T> eventType)
    {
        return bus.ofType(eventType);
    }
}
```



## 2.创建你需要发送的事件类



我们这里用StudentEvent举例



```java

public class StudentEvent {
    private String id;
    private String name;

    public StudentEvent(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```



## 3.发送事件

``` java
RxBus.getInstance().post(new StudentEvent("007","小明"));
```



## 4.接收事件

``` java
rxSbscription=RxBus.getInstance().toObserverable(StudentEvent.class)
                .subscribe(new Action1<StudentEvent>() {
                    @Override
                    public void call(StudentEvent studentEvent) {
                        textView.setText("id:"+ studentEvent.getId()+"  name:"+ studentEvent.getName());
                    }
                });
```



注：rxSbscription是Sbscription的对象，我们这里把RxBus.getInstance().toObserverable(StudentEvent.class)赋值给rxSbscription以方便生命周期结束时取消订阅事件



## 5.取消订阅

```java
@Override
    protected void onDestroy() {
        if (!rxSbscription.isUnsubscribed()){
            rxSbscription.unsubscribe();
        }
        super.onDestroy();
    }
```

