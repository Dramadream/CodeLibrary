长连接

NIO

异步



连接，发送消息，接收消息，异常处理。

断线重连，心跳发送，数据粘包，编解码器

消息体的数据结构



本地长连接状态维护

本地长连接账号维护



### SimpleChannelInboundHandler和ChannelInboundHandlerAdapter

-   1.继承关系
-   2.有何差异
    -   1.read0  和 read 方法
    -   read0中主要做了类型匹配以及用完之后释放指向保存该消息的 ByteBuf 的内存引用。



### 心跳

1.在initChannel中添加对应的处理机制。

第一步 ，`pipeline.addLast(new IdleStateHandler(0, 30, 0));`，这里配置超时策略，三个参数分别表示读超时，写超时，和读写超时时间。

第二步，`pipeline.addLast(new ChannelHandle());`，这里配置对应的事件回调处理，ChannelHandle里有多个回调，这里只说心跳相关的，触发回调后，要对事件做判断，然后做不同的处理。一般客户端是对写入做处理，这里，例子中检测到是写入超时，发送心跳包

```
new Bootstrap()
        ...
        .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();
                // 设置读写超时时间，0表示不监控，
                // 这里表示服务器长时间不发消息过来，客户端不管。
                // 客户端30秒没有发送消息，就会触发对应的事件回调
                pipeline.addLast(new IdleStateHandler(0, 30, 0));
                pipeline.addLast(new ChannelHandle());
            }
        })
```

 

```
 private class ChannelHandle extends SimpleChannelInboundHandler<String> {
        /**
         * 事件触发回调，这里做心跳发送操作
         */
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            super.userEventTriggered(ctx, evt);
            // 这里表示如果是超时事件
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent e = (IdleStateEvent) evt;
                 // 这里表示如果是写入超时，就发送心跳
                if (e.state() == IdleState.WRITER_IDLE) {
                    // 空闲了，发个心跳吧
                    ctx.writeAndFlush("This is heartbeat!");
                }
            }
        }
```









# 1.Netty是什么

## 1.1 初识Netty

[Netty 百度百科](https://baike.baidu.com/item/Netty/10061624 )

重点： 网络框架，开源，异步，事件驱动。 快速开发，高性能，高可靠性。TCP、UDP

通俗的说，它是一个使用最广泛的java长连接框架。对java原生的长连接做了很多的优化和扩展。

## 1.2 使用场景

也就是什么情况下我们会用到它，要学习它。

一般是用于直播、游戏、通信等场景。这些都需要低延迟，高性能的数据交换。

至于它的优缺点和架构等，可以看这篇文章。这里不展开了。

 [新手入门：目前为止最透彻的的Netty高性能原理和框架架构解析](https://www.cnblogs.com/imstudy/p/9908791.html)



# 2.初识Netty，先跑通第一个Server和Client



# 3.Client 的各项配置



# 4.数据粘包



# 5. 心跳机制



# 6.断线重连



# 7.

