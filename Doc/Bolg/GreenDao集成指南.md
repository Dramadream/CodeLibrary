# 1.Gradle中添加库

### 1.1 Projects中添加依赖

```
classpath "org.greenrobot:greendao-gradle-plugin:$greendao_version"
```

### 1.2 Module中添加依赖

```
apply plugin: 'org.greenrobot.greendao'

implementation "org.greenrobot:greendao:$greendao_version"
```



### 1.3 Gradle中对GreenDao进行配置

在Android 模块下配置

```
android {
    ...

    //GreenDao 配置
    greendao {
        //版本号，升级时可配置
        schemaVersion 1
        // 指定GreenDao生成的代码的包名，默认存放位置是build/generated/source/green目录下
        // 这里可随意指定，不过一般跟javaBean放在差不多一起
        daoPackage 'com.xxx.db.gen'
        // 跟上面一样，不过这里值代码路径
        targetGenDir 'src/main/java'
    }
}
```

### 1.4 混淆

这个是接入GreenDao最坑的地方，一定要把javabean和生成的Dao类都避免了才行。

```
#-------------------------------------------  GreenDao  --------------------------------------------
-keep class org.greenrobot.greendao.**{*;}
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
    public static java.lang.String TABLENAME;
}
-keep class **$Properties
-keep class com.xxx.db.bean.** { *; }
-keep class com.xxx.db.gen.** { *; }
```

# 2.使用JavaBean产生对应的Dao类，并管理

### 2.1 同步完成，开始写代码

#### 2.1.1 首先新建javabean类，并加上注释

```java
// 类上注明这个是数据库表对应的类
@Entity
public class Article {

    // ID，主键，最好是Long，也可以是long，autuincrement表示是否自增长
    @Id(autoincrement = false)
    private Long id;
    // 索引
    @Index(unique = true)
    private long chapterId;
    private String apkLink;
    // GreenDao不能直接处理List，这里需要特殊处理，后面再说
    @Convert(columnType = String.class, converter = TagConvert.class)
    private List<Tag> tags;
    ...
    
}
```

#### 2.1.2 生成对应的Dao类

javabean编写完成后，Make Project(Ctrl + F9)，完成后你会发现javabean中会多出一些代码，带有**@Generated(hash = 232289557)**的部分不要随意改动。

还有在之前**com.xxx.db.gen**包下面生成了3个类,DaoMaster，DaoSession，XXXDao。

这样这一步就完成了。

#### 2.1.3 对GreenDao进行初始化操作

在Application中添加如下代码：

```java
/**
 * 初始化GreenDao,直接在Application中进行初始化操作
 */
private void initGreenDao() {
    DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "aserbao.db");
    SQLiteDatabase db = helper.getWritableDatabase();
    DaoMaster daoMaster = new DaoMaster(db);
    daoSession = daoMaster.newSession();
}

private DaoSession daoSession;

public DaoSession getDaoSession() {
    return daoSession;
}
```

接下来就可以正常使用，获取DaoSession，再获取对应的Dao，然后增删改查了。



但是，还没完，这只是最基础的接入，你需要抽取优化，需要对版本进行管理。

### 2.2 优化管理和GreenDao数据库版本升级管理 

#### 2.2.1 首先来一个GreenDao的统一管理类，来管理GreenDao的使用

```java
package com.fkw.knowledge.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.fkw.knowledge.db.gen.DaoMaster;
import com.fkw.knowledge.db.gen.DaoSession;


/**
 * Author:          Kevin <BR/>
 * CreatedTime:     2018/12/5 16:28 <BR/>
 * Desc:            GreenDao 的管理类 <BR/>
 * <p/>
 * ModifyTime:      <BR/>
 * ModifyItems:     <BR/>
 *
 * @author: Kevin <BR/>
 */
public class DaoManager {

    private static final String TAG = DaoManager.class.getSimpleName();
    //创建数据库的名字
    private static final String DB_NAME = "1shuo_greendao.db";
    //初始化上下文
    private Context context;
    //多线程中要被共享的使用volatile关键字修饰  GreenDao管理类
    private volatile static DaoManager sInstance;
    //它里边实际上是保存数据库的对象
    private static DaoMaster sDaoMaster;
    //创建数据库的工具
    private static DaoMaster.DevOpenHelper sHelper;
    //管理gen里生成的所有的Dao对象里边带有基本的增删改查的方法
    private static DaoSession sDaoSession;

    /**
     * 单例模式获得操作数据库对象
     *
     * @return
     */
    public static DaoManager getInstance() {
        if (sInstance == null) {
            synchronized (DaoManager.class) {
                if (sInstance == null) {
                    sInstance = new DaoManager();
                }
            }
        }
        return sInstance;
    }

    private DaoManager() {
    }

    /**
     * 初始化上下文创建数据库的时候使用
     */
    public void init(Context context) {
        this.context = context;
    }

    /**
     * 判断是否有存在数据库，如果没有则创建
     *
     * @return
     */
    public DaoMaster getDaoMaster() {
        if (sDaoMaster == null) {
            // 创建数据库
            MySQLiteOpenHelper helper = new MySQLiteOpenHelper(context, DB_NAME, null);
            //获取可写数据库
            SQLiteDatabase db = helper.getWritableDatabase();
            //获取数据库对象
            sDaoMaster = new DaoMaster(db);
        }
        return sDaoMaster;
    }

    /**
     * 完成对数据库的添加、删除、修改、查询操作，
     *
     * @return
     */
    public DaoSession getDaoSession() {
        if (sDaoSession == null) {
            if (sDaoMaster == null) {
                sDaoMaster = getDaoMaster();
            }
            sDaoSession = sDaoMaster.newSession();
        }
        return sDaoSession;
    }

    /**
     * 关闭所有的操作，数据库开启后，使用完毕要关闭
     */
    public void closeConnection() {
        closeHelper();
        closeDaoSession();
    }

    public void closeHelper() {
        if (sHelper != null) {
            sHelper.close();
            sHelper = null;
        }
    }

    public void closeDaoSession() {
        if (sDaoSession != null) {
            sDaoSession.clear();
            sDaoSession = null;
        }
    }
}
```

这个类很简单，重要代码有两个部分，

1.getDaoMaster() 中创建升级数据库，并获取数据库对象；

2.以后会用getDaoSession() 来进行数据库的增删改查操作。

当然加入这个之后，之前的App的初始化就可以直接一行搞定了。

```
DaoManager.getInstance().init(this);
```

#### 2.2.2 数据库的创建和升级

```java
package com.fkw.knowledge.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.fkw.knowledge.db.gen.ArticleDao;
import com.fkw.knowledge.db.gen.DaoMaster;
import com.fkw.knowledge.db.gen.TagDao;

import org.greenrobot.greendao.database.Database;

/**
 * Author:          Kevin <BR/>
 * CreatedTime:     2018/12/5 16:13 <BR/>
 * Desc:            GreenDao的DBHelper，主要用户数据库升级 <BR/>
 * <p/>
 * ModifyTime:      <BR/>
 * ModifyItems:     <BR/>
 *
 * @author: Kevin <BR/>
 */
public class MySQLiteOpenHelper extends DaoMaster.OpenHelper {
    /**
     * @param context 上下文
     * @param name    原来定义的数据库的名字   新旧数据库一致
     * @param factory 可以null
     */
    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    /**
     * @param db
     * @param oldVersion
     * @param newVersion 更新数据库的时候自己调用
     */
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        Log.d("flag", "-----调用了");
        //具体的数据转移在MigrationHelper2类中
        /**
         *  将db传入     将gen目录下的所有的Dao.类传入
         */
        //        MigrationHelper.migrate(db);
        if (newVersion > oldVersion) {
            MigrationHelper.migrate(db, ArticleDao.class, TagDao.class);
        }
    }
}
```

创建，GreenDao会自动创建，我们不用管。

升级的话，GreenDao没有做升级，需要我们自己处理，我们再抽取出来**MigrationHelper**单独来进行数据库的升级。

需要注意的一点：每次数据库升级是，在onUpgrade中要把所有的Dao类添加到**MigrationHelper.migrate**中，来完成数据库的升级。

#### 2.2.3 用复制数据库的方式来完成升级

```java
package com.fkw.knowledge.db;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.fkw.knowledge.BuildConfig;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.StandardDatabase;
import org.greenrobot.greendao.internal.DaoConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author:          Kevin <BR/>
 * CreatedTime:     2018/12/5 16:11 <BR/>
 * Desc:            GreenDao 数据库升级的帮助类 <BR/>
 * <p/>
 * ModifyTime:      <BR/>
 * ModifyItems:     <BR/>
 *
 * @author: Kevin <BR/>
 */
public class MigrationHelper {

    private static String TAG = "MigrationHelper";
    private static final String SQLITE_MASTER = "sqlite_master";
    private static final String SQLITE_TEMP_MASTER = "sqlite_temp_master";

    public static void migrate(SQLiteDatabase db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        printLog("【The Old Database Version】" + db.getVersion());
        Database database = new StandardDatabase(db);
        migrate(database, daoClasses);
    }

    public static void migrate(Database database, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        printLog("【Generate temp table】start");
        generateTempTables(database, daoClasses);
        printLog("【Generate temp table】complete");

        dropAllTables(database, true, daoClasses);
        createAllTables(database, false, daoClasses);

        printLog("【Restore data】start");
        restoreData(database, daoClasses);
        printLog("【Restore data】complete");
    }

    private static void generateTempTables(Database db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        for (int i = 0; i < daoClasses.length; i++) {
            String tempTableName = null;

            DaoConfig daoConfig = new DaoConfig(db, daoClasses[i]);
            String tableName = daoConfig.tablename;
            if (!isTableExists(db, false, tableName)) {
                printLog("【New Table】" + tableName);
                continue;
            }
            try {
                tempTableName = daoConfig.tablename.concat("_TEMP");
                StringBuilder dropTableStringBuilder = new StringBuilder();
                dropTableStringBuilder.append("DROP TABLE IF EXISTS ").append(tempTableName).append(";");
                db.execSQL(dropTableStringBuilder.toString());

                StringBuilder insertTableStringBuilder = new StringBuilder();
                insertTableStringBuilder.append("CREATE TEMPORARY TABLE ").append(tempTableName);
                insertTableStringBuilder.append(" AS SELECT * FROM ").append(tableName).append(";");
                db.execSQL(insertTableStringBuilder.toString());
                printLog("【Table】" + tableName + "\n ---Columns-->" + getColumnsStr(daoConfig));
                printLog("【Generate temp table】" + tempTableName);
            } catch (SQLException e) {
                Log.e(TAG, "【Failed to generate temp table】" + tempTableName, e);
            }
        }
    }

    private static boolean isTableExists(Database db, boolean isTemp, String tableName) {
        if (db == null || TextUtils.isEmpty(tableName)) {
            return false;
        }
        String dbName = isTemp ? SQLITE_TEMP_MASTER : SQLITE_MASTER;
        String sql = "SELECT COUNT(*) FROM " + dbName + " WHERE type = ? AND name = ?";
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery(sql, new String[]{"table", tableName});
            if (cursor == null || !cursor.moveToFirst()) {
                return false;
            }
            count = cursor.getInt(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return count > 0;
    }


    private static String getColumnsStr(DaoConfig daoConfig) {
        if (daoConfig == null) {
            return "no columns";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < daoConfig.allColumns.length; i++) {
            builder.append(daoConfig.allColumns[i]);
            builder.append(",");
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }


    private static void dropAllTables(Database db, boolean ifExists, @NonNull Class<? extends AbstractDao<?, ?>>... daoClasses) {
        reflectMethod(db, "dropTable", ifExists, daoClasses);
        printLog("【Drop all table】");
    }

    private static void createAllTables(Database db, boolean ifNotExists, @NonNull Class<? extends AbstractDao<?, ?>>... daoClasses) {
        reflectMethod(db, "createTable", ifNotExists, daoClasses);
        printLog("【Create all table】");
    }

    /**
     * dao class already define the sql exec method, so just invoke it
     */
    private static void reflectMethod(Database db, String methodName, boolean isExists, @NonNull Class<? extends AbstractDao<?, ?>>... daoClasses) {
        if (daoClasses.length < 1) {
            return;
        }
        try {
            for (Class cls : daoClasses) {
                Method method = cls.getDeclaredMethod(methodName, Database.class, boolean.class);
                method.invoke(null, db, isExists);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void restoreData(Database db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        for (int i = 0; i < daoClasses.length; i++) {
            DaoConfig daoConfig = new DaoConfig(db, daoClasses[i]);
            String tableName = daoConfig.tablename;
            String tempTableName = daoConfig.tablename.concat("_TEMP");

            if (!isTableExists(db, true, tempTableName)) {
                continue;
            }

            try {
                // get all columns from tempTable, take careful to use the columns list
                List<String> columns = getColumns(db, tempTableName);
                ArrayList<String> properties = new ArrayList<>(columns.size());
                for (int j = 0; j < daoConfig.properties.length; j++) {
                    String columnName = daoConfig.properties[j].columnName;
                    if (columns.contains(columnName)) {
                        properties.add(columnName);
                    }
                }
                if (properties.size() > 0) {
                    final String columnSQL = TextUtils.join(",", properties);

                    StringBuilder insertTableStringBuilder = new StringBuilder();
                    insertTableStringBuilder.append("INSERT INTO ").append(tableName).append(" (");
                    insertTableStringBuilder.append(columnSQL);
                    insertTableStringBuilder.append(") SELECT ");
                    insertTableStringBuilder.append(columnSQL);
                    insertTableStringBuilder.append(" FROM ").append(tempTableName).append(";");
                    db.execSQL(insertTableStringBuilder.toString());
                    printLog("【Restore data】 to " + tableName);
                }
                StringBuilder dropTableStringBuilder = new StringBuilder();
                dropTableStringBuilder.append("DROP TABLE ").append(tempTableName);
                db.execSQL(dropTableStringBuilder.toString());
                printLog("【Drop temp table】" + tempTableName);
            } catch (SQLException e) {
                Log.e(TAG, "【Failed to restore data from temp table 】" + tempTableName, e);
            }
        }
    }

    private static List<String> getColumns(Database db, String tableName) {
        List<String> columns = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName + " limit 0", null);
            if (null != cursor && cursor.getColumnCount() > 0) {
                columns = Arrays.asList(cursor.getColumnNames());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            if (null == columns)
                columns = new ArrayList<>();
        }
        return columns;
    }

    private static void printLog(String info) {
        if (BuildConfig.LOG_DEBUG) {
            Log.d(TAG, info);
        }
    }
}
```

 原理很简单，就是将所有的数据复制到新的数据库中，再删掉原有的数据库就OK， 具体实现过程请看代码。

要用的话直接用就行了。

# 3.对数据的操作

这个就很简单了：

```java
DaoManager.getInstance().getDaoSession().getLiveBgmDao();
```

接下来就是调接口了，这个很简单，就一一演示了，百度一大把。

