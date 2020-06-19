# 简介

[Room][1]是Google 推出的[JetPack][2]的一个架构组件，是对数据库的一个封装。让我们能更简单地操作数据库。

这里有一份官方的[Room说明][3]，可以直接去看，但是感觉很多地方不直观，有很多坑。所以我写了这篇文章，一是为了记录和深化Room，二是为了把一些坑和重点记录一下。



# 使用

## 0 说明

### 0.1 版本

Room的新版本(2.0.0以后)只支持Android X，如果你用的还是support，那就不行了。

要么转将support转成Android X，要么用低版本的Room，但是不建议用低版本的，还是建议升级成X。

### 0.2 总体说明

虽然Google官方给了图，但是感觉不够直观，这里直接说一下，使用Room所必须的四部分。

- Database类： 继承RoomDatabase。是数据库的总览类，需要在这里配置所有的表对应的实体类和数据库版本。
- Entity类：实体类，对应数据库中的一张表。可以指定主键外键索引等。
- Dao类：Entity对应的操作类。
- 初始化操作：指定Database类，指定数据库名称，完成对数据库的初始化、升级等操作。



大体用法和GreenDao差不多，但是有几点是不一样的：

- 数据库升级要稍微麻烦一点
- 这里的Database和Dao类都是接口或者abstract类，很多方法Room会自己帮我们实现。
- 基础的增删改，Room可以帮我们实现，但是查询和较复杂的其它操作需要我们自己写数据库查询语言
- 所有的操作必须在子线程中运行，在UI线程会直接抛出异常。但是如果使用LiveData或RxJava，查询操作可以在UI线程。

在使用中，还有其他一些细节要注意，后面会一一讲到。



## 1 引入到Project中

这个没啥好说的，按[Google官方文档][4]即可。这里还是搬运一下，照顾无法翻墙的同学。

1. 添加依赖

    ```
        dependencies {
          def room_version = "2.2.2"

          implementation "androidx.room:room-runtime:$room_version"
          // For Kotlin use kapt instead of annotationProcessor
          annotationProcessor "androidx.room:room-compiler:$room_version" 

          // optional - Kotlin Extensions and Coroutines support for Room
          implementation "androidx.room:room-ktx:$room_version"

          // optional - RxJava support for Room
          implementation "androidx.room:room-rxjava2:$room_version"

          // optional - Guava support for Room, including Optional and ListenableFuture
          implementation "androidx.room:room-guava:$room_version"

          // Test helpers
          testImplementation "androidx.room:room-testing:$room_version"
        }
    ```

2. 配置编译器选项

   这里说明一下，这里可以去掉不加，但是最好还是加上。不然也要在其它地方配置。

   ```
       android {
           ...
           defaultConfig {
               ...
               javaCompileOptions {
                   annotationProcessorOptions {
                       arguments = [
                           "room.schemaLocation":"$projectDir/schemas".toString(),
                           "room.incremental":"true",
                           "room.expandProjection":"true"]
                   }
               }
           }
       }
   ```



## 2. 开始创建数据库和表

我们按之前说的Room所需的4个必须部分一步步来。

首先我们构思好这个数据库叫test.db，有两张表Contact和ChatRecord，这样就再需要ContactDao和ChatRecordDao两个类，再来一个总的管理类DBManager。

好了，一共6个类。

### 2.1  首先是Database类

新建一个AppDatabase继承RoomDatabase。注意是接口或抽象类。

之前说过，它是数据库的总览，这里只需要一些简单的配置和方法即可。当然你自己加一些逻辑方法也行。

```java
@Database(entities = {Contact.class, ChatRecord.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ContactDao contactDao();

    public abstract ChatRecordDao chatRecordDao();

}
```

1. 添加@Database注解，并添加实体类(表)的说明和数据库版本
2. 然后添加两个抽象方法，用于对外暴露Dao对象。具体的实现Room会帮我们做。

### 2.2 创建实体类

```java
@Entity
public class Contact {

    @PrimaryKey(autoGenerate = true)
    public Long id;

    /** 通讯录ID */
    public String contactId;
    /** 昵称 */
    public String nickName;
    /** 头像 */
    public String avatar;
 
}
```

现在创建实体类，以Contact举例。

添加@Entity注解即可。

- 主键添加@Primary注解，如果想要自动生成，就加autoGenerate=true

- 索引。如果要加索引，要在类上添加注解。如果数据量很小，就不需要添加索引了。

    ```java
    @Entity(indices = {@Index("contactId")})
    public class Contact {
    	... 
    	
        /** 通讯录ID */
        public String contactId;
    }
    ```

- 表名。**默认就是类名(Contacts)，区分大小写**。如果想要指定表名，可以这样添加注解

    ```
    @Entity(tableName = "contacts")
    public class Contact {
    	... 
    }
    ```
    
- 字段名和默认值。

    **字段名默认就是Java中的字段，区分大小写。**默认值都是空数据

    ```java
    @Entity
    public class Contact {
    
        /** 昵称 */
        @ColumnInfo(name = "nick_name", defaultValue = "张三")
        public String nickName;
        /** 头像 */
        public String avatar;
     
    }
    ```





### 2.3 Dao类

Dao类，使用@Dao注解，同样也是接口或抽象类。

- 增删改都很简单，直接传入对应的实体类对象或对象列表，再加上注解即可。

  > 注意，这些方法必须在子线程中运行。

  ```java
      @Dao
      public interface ContactDao {
          @Insert
          public void insert(Contact... contacts);
  
          @Update
          public void update(Contact... contacts);
  
          @Delete
          public void delete(Contact... Contact);
      }
  ```

- 查询

  > 注意，不使用LiveData或Rxjava或Kotlin协程的查询也必须在子线程中运行。

  最简单的查询所有

  ```java
  @Dao
  public abstract class ContactDao {
  
      @Query("SELECT * FROM Contact")
      public abstract List<Contact> getAll();
  }
  ```

  条件查询。

  查询是这个昵称的联系人。使用:name来匹配参数，多个参数的用法相同。

  ```java
      @Query("SELECT * FROM Contact WHERE nickName =:name")
      public abstract Contact getByName(String name);
  ```

  其它的查询，可以自己用SQL语言试验。

- 使用LiveData

  最大的好处就是可以直接调用。

  ```java
  @Dao
  public abstract class ContactDao {
  
      @Query("SELECT * FROM Contact")
      public abstract LiveData<List<Contact>> getAll();
  }
  ```

- 其它用法

  其它进阶使用请看[Dao的官方文档][5]。



### 2.4 Room的初始化

​	首先是初始化工作，很简单。这里填好几个参数即可。重点是数据库版本升级，下面详细说。

```java
AppDatabase appDatabase = Room.databaseBuilder(context, AppDatabase.class, "speaker.db")
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        // 数据库创建时的回调，可以在这里进行数据初始化
                        LogUtils.i();
                    }
                })
     			// 数据库升级的操作
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build();
```



### 2.5 开始使用

```java
// 增删改的使用，注意要在子线程
ThreadUtils.getIoPool().execute(() -> mDao.deleteOne(entity);

// 使用LiveData查询
mContactDao.getAll().observe(this, contacts -> {
    if (contacts != null && mAdapter != null && mAdapter.mDatas != null) {
        LogUtils.i();
        mAdapter.notifyDataSetChanged();
    }
});
```



## 3. 数据库升级

具体原理我不清楚，后面再研究，步骤是这样的。

1. 修改添加实体类和对应的Dao类；

2. 修改AppDatabase类中的配置；

3. Room初始化时添加数据库迁移的实现。

   

假如我们添加一张Person的表。有两个字段，一个主键自增长的id，另一个是索引name。再给Contact表中添加一个字段。



### 3.1 实体类和Dao类修改

要添加的实体类如下：

```java
@Entity(indices = {@Index("name")})
public class Person {

    @PrimaryKey(autoGenerate = true)
    public Long id;

    public String name;
}
```

对应的PersonDao类和Contact的修改这里不再重复了。

### 3.2 AppDatabase的修改

修改注解和抽象方法

```
@Database(entities = {Contact.class, ChatRecord.class, Person.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ContactDao contactDao();
    public abstract ChatRecordDao chatRecordDao();
    public abstract PersonDao personDao();
}
```

### 3.1 初始化时添加数据库迁移的实现

```java
 static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE Person (" +
                    "id INTEGER," +
                    "name TEXT, " +
                    "PRIMARY KEY (id));");
            database.execSQL("CREATE INDEX index_Person_name ON Person(name);");
            database.execSQL("ALTER TABLE contact ADD COLUMN age INTEGER;");
        }
    };
```

> 注意SQLite的ALTER TABLE命令[非常局限](https://link.jianshu.com/?t=https%3A%2F%2Fsqlite.org%2Flang_altertable.html)，只支持重命名表以及添加新的字段。



# 其它



## 简化工作量





[1]: https://developer.android.com/topic/libraries/architecture/room	"Room"

[2]: https://developer.android.com/jetpack	"Jetpack"

[3]: https://developer.android.com/training/data-storage/room	"Room详解"
[4]: https://developer.android.com/jetpack/androidx/releases/room#declaring_dependencies	"添加Room依赖"

[5]: https://developer.android.com/training/data-storage/room/accessing-data	"Dao使用"

