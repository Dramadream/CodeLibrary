

# 0 一些重要的东西

- 扩展 
- == 和equals是一样的，===才是比较内存地址
- Java字符串缓冲区，常量池
- show byte code
- 字面量
- 正则表达式
- 4.1.3 可见性修饰符的说明没有太懂



# 1 序章，简介

1. Kotlin和Java一样是一种**静态类型**的编程语言。必须编译之后才能运行。

2. **可空类型**，编译时会检测可能存在的空指针异常。

3. 函数式编程

   1. 头等函数----把函数当值来使用，可以用变量保存，或当参数，或者当返回值
   2. 不可变对象----状态在创建之后不会再发生变化
   3. 无副作用----使用的是纯函数，在输入相同时，会产生同样的结果，并不会修改其他对象的状态，也不会和外界交互。

   好处：代码简洁，**多线程安全**，更易测试

   kotlin可提供的函数式编程方式：函数作为返回值，lambda表达式，不可变数据，丰富的底层API

4. Kotlin的设计哲学

   务实，简洁，安全，互操作性，



# 2 基础

## 2.1 基础要素：函数、变量

### 2.1.1 函数

1. 语句和表达式

   Kotlin中除了for,do,do/while之外，其他的大多数控制结构都是表达式，而不是语句。

   而赋值操作，Java中是表达式，Kotlin中是语句，

2. 如果函数体写在花括号内，我们说这个函数有**代码块体**。

   ```kotlin
   fun max(a:Int,b:Int):Int{
       return if (a > b) a else b
   }
   ```

   如果它直接返回了一个表达式，我们叫它有**表达式体**。

   ```kotlin
   fun min(a: Int, b: Int): Int = if (a > b) b else a
   ```

   IntelliJ IDEA提供了两种函数风格的快捷转换

3. 函数的返回

   - Unit类型和Nothing类型(表明函数不会正常返回数据，主要用于测试相关的框架 )

4. 函数的参数

   - 可以使用命名函数调用的方式来指定参数的值

     ```kotlin
     fun main() {
         printArea(height = 134, width = 123)
     }
     
     fun printArea(width: Int, height: Int) {
         println("$width * $height = ${width * height}")
     }
     ```

   - 可以指定函数参数的默认值，可以使用这种方式，来实现类似于函数的重载

     ```kotlin
     fun main() {
         printArea()
     }
     
     fun printArea(width: Int = 100, height: Int = 100) {
         println("$width * $height = ${width * height}")
     }
     ```

   - 可变参数 vararg

     请注意如果同时有可变参数不是最后一个参数时，后面的一定要指定参数名

     ```kotlin
     
     ```

### 2.1.2 变量

跟函数差不多的写法。 

val  变量名 :  类型 = xxx

不过需要注意的是，有初始化时，可以省略变量类型，但是没有初始化时，必须显示声明变量类型。

- val--value，不可变引用，对应java中的final

- var-- variable，可变引用，对应java中的变量

- 编译期常量 **const val**，指编译了之后就无法更改值。只能用于顶层(方法外定义)常量，**且只能是String或基本数据类型**

  能使用val尽量使用val，而且很多引用数据类型/指针类型是不允许被修改的。更重要的是**符合函数式编程的要求**

> 应尽量使用不可变引用val，仅在必要时转成var。
>
> 尽管val本身的引用是不可变的，但是它指向的对象的属性等是可变的。

软关键字和修饰符关键字都可以在非应用场景时作为标识符(变量名等等)

```kotlin
val public = "公共的" // 这种情况是合法的
```

### 2.1.3 字符串

1. 字面量 ？？ 

2. 原始字符串(raw string)-->3个双引号"""Hello world"""，就是包裹起来的字符串，原样打印出来

3. 字符串常量

   ${表达式}

   还可以在花括号内直接使用双引号等

   ```kotlin
   // 声明变量可为空的做法是在类型后加?
   fun str(par: String?){
       val name = "123131";
   	println("name : $name");
   	println("name : ${name}");
       println("name : ${ if (par != null )par else "another one"}"); 
   }
   ```
   

### 2.1.4 类和对象

使用

```kotlin
class Person(val name: String)
```

来代替

```java
class Person{
    // 上面的是val，不是var，所以这里要用final修饰
    private final String name;
    
    public Person(String name){
        this.name = name;
    }
    
    public String getName(){
        return this.name;
    }
}
```

> Kotlin中的默认访问权限修饰符是public，而不是"package"

访问器：对于Java，有getter和setter。默认的实现如下，而对于kotlin，不需要

```kotlin
class Person(
    val name: String,// 是常量，只读，所以对外只有getter方法，没有setter方法
    var isMarried: Boolean// 是变量，可读可写，所以对外有getter和setter方法
)
```

当然你也可以自己实现，来代替默认的方法。

属性isSquare不需要字段来保存它的值，它只有一个自定义实现的getter。并且是每次访问时实时计算出来的。

```kotlin
class Rectangle(val height: Int, val width: Int){
    // 属性isSquare不需要字段来保存它的值，它只有一个自定义实现的getter。并且是每次访问时实时计算出来的。
    val isSquare: Boolean
        get(){
            return height == width
        }
    	// 这里可以将花括号省略，换成 "="
}
```

init方法和次构造器

​	init方法的执行顺序是在主构造函数中。

### 2.1.5 包和目录

导入方式和java基本相同

但是Kotlin中可以把多个类放在同一文件中，文件的名字还可以随意选择。

并且，包层级结构不需要遵循目录层级结构。

> 不管怎样，大多数情况下，遵循Java的目录布局并根据包结构，把源码文件放到目录中，依然是个不错的实践。在Kotlin和Java混用的项目中坚持这样的结构尤为重要。
>
> 但是对于很小的类，你应该把它们放到同一个文件中。



### 2.1.6 数据类型

- 基本数据类型

  1. Java中的基本数据类型有8个，但是分为两大类。分别问boolean和数值类型。也就是说char，byte，short，int，long，float，double都属于数值类型，数值类型之间可以直接互相转换。

  2. 但是Kotlin中char是一个单独的类型。也就是说8个类型分3大类，Boolean，Char和数值类型(Byte，Short，Int，Long，Float，Double)。char和数值相互转换必须通过特定的函数。且赋值时，不同的数值类型之间也必须显式地转换；计算时，可以自动转。转换的规则是

     ​	Int下的都转为Int，Int上的都转为大的类型 

  3. 数字的默认类型。整数-Int，浮点-Double。如果声明Long或者Float类型，最好是用大写(F,L)的放后面

- 可空数据类型

  安全调用运算符：?

  ​	意思是这个对象可能为空

  ```kotlin
  fun divide(n1: Int, n2: Int): Double? = if (n2==0) null else (n1.toDouble()/n2)
  
  fun main(){
      
      val divNum1 = divide(100, 0)
      // 这里divNum1可能为null，如果为null，则整个结果都是null
      // 且这里divNum1是null，不会抛出异常，所以说它是安全地
      val result1 = divNum1?.plus(100)   
  }
  ```

  非空断言：!!

  ​	意思是断言这个对象是非空的，如果这个对象为null，就会抛出空指针。

  安全转换运算符：as?

  Elvis运算符(控制合并运算符)：?:

  ```kotlin
  if (a == null) a else b
  A ?: B // 表达式，A为null，就为B，A不为null，就为A
  ```

  

### 2.1.7 其他运算符

- 冒号(:)

- 双冒号(::)

- 箭头(->)

- 展开运算符(*)

- 区间(..)

  


## 2.2 枚举和when

Kotlin中的枚举性能如何？

枚举中的值之间用“,”隔开，用“;”表示枚举的值结束，把枚举常量列表和普通的属性和方法定义分开。这也是Kotlin语法中唯一必须使用分号的地方



### 2.2.1 使用When来代替if/else和switch/case语句

when在Kotlin中也是表达式，有返回值。

when不只是可以int和String，可以是任何对象。那如何判断对象的相等？equls还是比较指针？

when也可以不带有任何参数，这时候就相当于if/else，这时，分支条件就是任意的布尔表达式。



### 2.2.2 类型转换

在Kotlin中，你要使用is检查来判断一个变量是否是某种类型，如果你经常使用C#写代码，这种表示法应该不会陌生。is检查和Java中的instanceOf类似。但是在Java中，如果你已经检查过一个变量是某种类型并且要把它当作这种类型来访问器成员是，在instanceOf检查之后还需要显示的加上类型转换。如果最初的变量会使用超过一次，常常选择把类型转换的结果存储在另一个单独的变量中。而在Kotlin中，**编译器会自动的执行类型转换(is)**，不需要再重新显式的赋值。

Kotlin也提供了显式的类型转换：

```kotlin
val n = e as Num
```

> **代码块中最后的表达式就是结果**，在所有使用代码块并期望得到一个结果的地方成立。



### 2.2.3 循环控制

1. while和do-while循环，与Java中的语法没有什么区别

2. for循环

   for..in循环

   一般是用区间来使用，

   ​	区间只能用**整数或字符**，Int,Long,Char

   ​	声明开区间是until

   ```kotlin
   // 正常的区间是右边闭合的区间
   // until是右边不闭合的区间
   for (i in 0..19) {
       print(fizzBuzz(i))
   }
   println()
   for (i in 1 until 20) {
       print(fizzBuzz(i))
   }
   println()
   for (i in 100 downTo 80 step 1) {
       print(fizzBuzz(i))
   }
   ```

   for..in循环

   对于map的for循环

   对于list的for循环

   withIndex，indices

   in区间，不光只限于字符，也可以适用于其它任何实现了java.compareable接口的任意类



# 3 函数的定义和调用

3.1 在kotlin中创建集合，这里的集合暂时只是java中的集合

to函数。意思为创建键值对

## 3.2 让函数更好用

1. 可以指定参数的名称

2. 可以设置默认的参数值

   > 考虑到Java中没有默认参数值的概念，所以，Java调用Kotlin的函数时，必须显式的指定所有的参数值。
   >
   > 这是你可以在Kotlin的函数上添加@JvmOverloads注释，这样编译器会自动生成几个重载方法

3. 消除静态工具类：顶层函数和属性

   使用时，直接调用对应的方法全名即可。

   > 如果要Java调用Kotlin中的顶层函数或属性。需要在文件最开始加上@file:Jvm("StringUtils")，package  strings这样的包声明放在后面。
   >
   > 使用时直接strings.StringUtils.xxx()即可

4. 顶层属性使用差不多，只是要注意访问权限，更改属性的类型。

   是var，val，还是const val

## 3.3 给别人的类添加方法：扩展函数和属性

```kotlin
fun main() {
    val msg = "abcdefg"
    println(msg.lastChar())
}

fun String.lastChar(): Char = this.get(this.length - 1)
fun String.lastChar1(): Char = this[length - 1]
fun String.lastChar2(): Char = get(length - 1)
```

上面的扩展方法意思就是所有的String类对象都可以使用这几个方法。

事实上，**扩展函数是静态函数**。也就是顶层函数，包含这个函数的Java类名称是有这个函数申明的文件名决定的。

> 假设lastChar这个方法声明在StringUtils.kt中，使用方法如下
>
> ```java
> char c = StringUtils.lastChar("Hello");
> ```

Kotlin支持直接导入方法或者属性

> 如果有方法名或者属性名冲突时，Java的做法是采用全名，而Kotlin建议使用**as**关键字创造别名
>
> ```kotlin
> import strings.lastChar as last
> val c = "Hello".last()
> ```



实际扩展函数是静态函数的高级语法糖，所以它不能被重写。

```java
public class Test {

    public static void main(String[] args) {
        Person a = new Teacher();
        // 这里printCategory是静态方法，编译之后是Person.printCategory();所以继承关系是无效的
        a.printCategory();
        a.printName();
    }
}

class Person {
    public static void printCategory() {
        System.out.println("Person");
    }

    public void printName(){
        System.out.println("Person 张三");
    }
}


class Teacher extends Person {
    public static void printCategory() {
        System.out.println("Teacher");
    }

    public void printName(){
        System.out.println("Teacher 张三");
    }
}
```



> 扩展函数并不是类的一部分，它是声明在类之外的。尽管可以给基类和子类都分别定义一个同名的扩展函数，但是调用哪个函数是由该变量的静态类型决定的。

但是它使用起来更加方便。一般作为工具函数来使用。



扩展属性和普通的属性的定义是一样的，需要有get和set函数。

## 3.4 处理集合：可变参数，中缀调用和库的支持

可变参数 vararg

当需要传递的参数已经包在数组中时，java可直传递数组对象，而Kotlin需要显示的解包数组，即使用 展开运算符
(\*)，在数组对象前价格(\*)即可。

### 3.4.2 中缀调用

```kotlin
1.to("one")
1 to "one"
```

声明：infix

中缀调用可以与只有一个参数的函数一起使用。



# 4. 类、对象和接口

Kotlin中的嵌套类不是内部类，并没有对其外部类的隐式的引用。

Kotlin中的类默认都是final和public的。

 object关键字，单例，半生对象，对象表达式(相当于Java的匿名类)



## 4.1 继承

Kotlin中:代替了extends和implements

```kotlin
super<Clickable>.showoff()
```

继承关系的修饰符：open，final，abstract

> open和只能转换
>
> ​	类默认为final的一个重要好处是智能转换可以大量使用。

可见性修饰符：

​	public(默认)，protected，private；

​	internal，只在模块内部可见。。模块即为一组一起编译的Kotlin文件。这样的话，真正实现了对模块的封装。

​	sealed类。使用sealed修饰的类默认是open的，在when中使用sealed类时，不用添加else



## 4.2 构造器，属性

主构造器

从构造器

初始化语句块 init()

接口没有构造方法



数据类：data

单例类：object -- 其实是静态类，包括属性和函数

装饰者模式：by



Kotlin中的类不能拥有静态成员，取而代之的是顶层变量

