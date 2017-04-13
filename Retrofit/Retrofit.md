#Retrofit简单入门

Retrofit是由square公司开发的基于okhttp框架之上的一套网络请求框架.可以在gitHub官网上下载到对应的开发包:

[https://github.com/square/retrofit](https://github.com/square/retrofit)

##简单的配置

找到app对应的Gradle文件,配置如下:

	compile 'com.squareup.retrofit2:retrofit:2.1.0'

为了让retrofit框架对发送的参数/返回的参数能够自动的在json与bean之间进行转换 一般我们还要添加多2个转换工厂的依赖:

	compile 'com.squareup.retrofit2:converter-gson:2.0.0-beta4'//ConverterFactory的Gson依赖包
	compile 'com.squareup.retrofit2:converter-scalars:2.0.0-beta4'//ConverterFactory的String依赖包


###一个简单的网络请求

首先我们想做一个登陆的网络请求,请求的接口参数如下

- 域名:http://mall.520it.com
- 路径:/login
- 请求类型:GET
- 参数:
  - username:用户名	2
    - pwd:密码 		123456

**Retrofit开发步骤如下:[请完善此例,下面有更详细解释]**

1. 为登陆服务创建对应的接口,并为特定的登陆请求创建接口方法:

   	public interface ILoginService {

   	    @GET("/login")
   	    Call<String> loginByUser(@Query("username")String name,
   	                             @Query("pwd")String pwd);
   	
   	}

2. 在APP创建的时候,创建一个Retrofit工厂:

   	Retrofit mRetrofit = new Retrofit.Builder()
   	        .baseUrl("http://mall.520it.com")
   	        //.addConverterFactory(GsonConverterFactory.create())
   	        .addConverterFactory(ScalarsConverterFactory.create())
   	 		.build();

3. 在需要调用登陆网络请求的方法中,获取对象的方法,将其构建为一个Call对象:

        ILoginService loginService = mRetrofit.create(ILoginService.class);
    	Call<String> resultJosnCall = loginService.loginByUser("2", "123456");

4. 拿到Call对象后,将该耗时操作交由Retrofit工厂执行:

   	resultJosnCall.enqueue(new Callback<String>() {
   	     @Override
   	     public void onResponse(Call<String> call, Response<String> response) {
   	         Log.v(TAG, response.body().toString());
   	     }
   	
   	     @Override
   	     public void onFailure(Call<String> call, Throwable t) {
   	         Log.v(TAG, "onFailure.........." + t.getLocalizedMessage());
   	     }
   	 });


完成上面的步骤后,运行程序!


##接口方法的配置

###Get请求的接口配置1:

下面的代码就是为登陆请求创建了一个简单的接口方法.

* @Get表示我们执行的Get请求.
* (/login)表示的是Url请求的路径.
* Call里面的<>执行返回数据的类型,因为这里想返回一个JSON格式的字符串 所以指定为String
* @Query 表明的是请求参数  username 和  pwd 后面即是传进来的对应的数据

-----
	public interface ILoginService {

		    @GET("/login")
		    Call<String> loginByUser(@Query("username")String name,
		                             @Query("pwd")String pwd);
		
	}


###Get请求的接口配置2:

如上的请求,也可以将所有参数配置一个HashMap,值需要在接口方法中修改成@QueryMap注解即可

	public interface ILoginService {

	    @GET("/login")
	    Call<String> loginByUser(@QueryMap Map<String,String> paramsMap);
	
	}

如果上面的注解改变了,那么获取接口方法的时候应该是这样的:

	ILoginService loginService = mRetrofit.create(ILoginService.class);
	Map<String,String> paramsMap=new HashMap<>();
	paramsMap.put("username","2");
	paramsMap.put("pwd","123456");
	Call<String> resultJosnCall = loginService.loginByUser(paramsMap);

###Get请求的接口配置3:

有的时候 传递的参数类型不一定都是8大基本类型和String,也可以是对象,假如这里登陆的时候需要传递一个登陆参数,其类名为LoginUser,那么代码应该是这样的:

	public interface ILoginService {

	    @GET("/login")
	    Call<String> loginByUser(@Body LoginUser param);
	
	}


##Retrofit工厂的配置

下面的代码创建了一个Retrofit工厂.

* 该创建实例使用了建造者模式,可以方便我们灵活的配置该工厂所需要的特定参数
* baseUrl()指定的是域名. 一般建议使用常量代替
* addConverterFactory()指定接口返回数据的转换工厂,这里提供了GsonConverterFactory和ScalarsConverterFactory,前者会将返回的JSON语句通过GSON工具自动转换成对象,后者指定的就是返回一个JSON字符串.一般只需要调用一次该方法即可.
* 上面的addConverterFactory()如果使用的是GsonConverterFactory,那么**接口方法**返回的Call对象的泛型必须是一个Object而不是一个JSON字符串
* build() 创建该Retrofit对象

-----

	Retrofit mRetrofit = new Retrofit.Builder()
	        .baseUrl("http://mall.520it.com")
	        //.addConverterFactory(GsonConverterFactory.create())
	        .addConverterFactory(ScalarsConverterFactory.create())
			.build();

##获取接口方法

下面的代码主要用来获取某个请求的接口方法的.

* Retrofit.create()首先获取到了一个接口对象
* loginService.loginByUser()主要是调用该接口对应的方法,并传入我们要传给后台服务器的参数.返回一个Call对象包含了我们的返回数据类型.
* 注意:这里还没执行网络请求.

-----

    ILoginService loginService = mRetrofit.create(ILoginService.class);
    Call<String> resultJosnCall = loginService.loginByUser("2", "123456");

##执行网络请求


网络请求的方式分为2种:同步/异步

###异步请求:

下面的代码通过Call对象直接将请求任务添加到线程池中,并将返回的结果回退到Callback<String>对象中.

* 返回的响应对象是由Response类管理的 response,可以通过.body()拿到对应的JSON数据

-----

	resultJosnCall.enqueue(new Callback<String>() {
	    @Override
	    public void onResponse(Call<String> call, Response<String> response) {
	        Log.v(TAG, response.body().toString());
	    }
	
	    @Override
	    public void onFailure(Call<String> call, Throwable t) {
	        Log.v(TAG, "onFailure.........." + t.getLocalizedMessage());
	    }
	});

###同步请求:

	Response<String> response = resultJosnCall.execute();
	Log.v(TAG,response.body().toString());

同步请求的代码如上,执行一个请求后即可返回数据,不过 如果直接在主线程中运行该代码,会报如下异常:

	Caused by: android.os.NetworkOnMainThreadException

正确的代码应该是这样的:

	new Thread(){
	    @Override
	    public void run() {
	        Response<String> response = null;
	        try {
	            response = resultJosnCall.execute();
	            Log.v(TAG,response.body().toString());
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}.start();


##Post请求代码

还是一个登陆的网络请求接口,此时它同样支持POST请求,请求的接口参数如下

- 域名:http://mall.520it.com
- 路径:/login
- 请求类型:POST
- 参数:
  - username:用户名	2
    - pwd:密码 		123456

上面的Get请求只要修改接口方法即可,代码如下:

* @FormUrlEncoded说明该Post请求是以表单的方式提交的,该注解是必须添加上去的
* @Field 声明添加的From表单参数.

  	public interface ILoginService {

  	    @FormUrlEncoded
  	    @POST("/login")
  	    Call<String> loginByUser(@Field("username") String name,
  	                             @Field("pwd") String pwd);
  	
  	}


# Retrofit注解说明

## 1.@Get  @Post

## 2.@Quary

## 3. @QueryMap

## 4.动态的url访问`@PATH`,path只能定位路径

```java
//用于访问zhy的信息
http://192.168.1.102:8080/springmvc_users/user/zhy
//用于访问lmj的信息
http://192.168.1.102:8080/springmvc_users/user/lmj

@GET("{username}")
    Call<User> getUser(@Path("username") String username);
```

## 5.向服务器传入json字符串`@Body`

## 6.表单的方式传递键值对`@FormUrlEncoded`和`@Field`

这个只能是Post

```java
 	@POST("login")
    @FormUrlEncoded
    Call<User> login(@Field("username") String username, @Field("password") String password);
```

## 7.单文件上传`@Multipart`

下面看一下单文件上传，依然是再次添加个方法：

```java
public interface IUserBiz
{
    @Multipart
    @POST("register")
    Call<User> registerUser(@Part MultipartBody.Part photo, @Part("username") RequestBody username, @Part("password") RequestBody password);
}

```

这里`@MultiPart`的意思就是允许多个`@Part`了，我们这里使用了3个`@Part`，第一个我们准备上传个文件，使用了`MultipartBody.Part`类型，其余两个均为简单的键值对。

使用：

```java
File file = new File(Environment.getExternalStorageDirectory(), "icon.png");
RequestBody photoRequestBody = RequestBody.create(MediaType.parse("image/png"), file);
MultipartBody.Part photo = MultipartBody.Part.createFormData("photos", "icon.png", photoRequestBody);

Call<User> call = userBiz.registerUser(photo, RequestBody.create(null, "abc"), RequestBody.create(null, "123"));1234512345
```

ok，这里感觉略为麻烦。不过还是蛮好理解~~多个`@Part`，每个Part对应一个RequestBody。

## 8. 多文件上传

