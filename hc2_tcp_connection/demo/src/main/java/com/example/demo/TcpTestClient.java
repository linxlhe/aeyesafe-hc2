//package com.example.tsing.client;
package com.example.demo;

import com.alibaba.fastjson2.JSONObject;
import com.example.demo.TcpClientHandler;
import com.example.demo.MessageCodecSharableTcpTest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TcpTestClient {



    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharableTcpTest MESSAGE_CODEC = new MessageCodecSharableTcpTest();
        TcpClientHandler MESSAGE_REC = new TcpClientHandler();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.channel(NioSocketChannel.class);
                bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);
                bootstrap.option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(10,  100));
                bootstrap.group(group);
                bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4, -4, 4));
                        ch.pipeline().addLast(LOGGING_HANDLER);
                        ch.pipeline().addLast(new IdleStateHandler(0, 50, 0));
                        ch.pipeline().addLast(MESSAGE_CODEC);
                        ch.pipeline().addLast(MESSAGE_REC);

                        /*ch.pipeline().addLast(new ChannelDuplexHandler() {
                            // 用来触发特殊事件
                            @Override
                            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
                                IdleStateEvent event = (IdleStateEvent) evt;
                                *//*switch (event.state()) {
                                    case READER_IDLE:
                                        // 读取超时
                                        // 处理读取超时的逻辑
                                        break;
                                    case WRITER_IDLE:
                                        JSONObject r = new JSONObject();
                                        r.put("cmd", "ping");
                                        ctx.writeAndFlush(r.toJSONString());
                                        // 写入超时
                                        // 处理写入超时的逻辑
                                        break;
                                    case ALL_IDLE:
                                        // 读取和写入都超时
                                        // 处理读取和写入都超时的逻辑
                                        break;
                                    default:
                                        break;
                                }*//*
                            }
                        });*/
                    }
                });
            //    Channel channel = bootstrap.connect("192.168.1.188", 7260).sync().channel();
                Channel channel = bootstrap.connect("qinglanst.com", 7260).sync().channel();
                channel.closeFuture().sync();
            } catch (Exception e) {
                log.error("client error", e);
            } finally {
                group.shutdownGracefully();
            }

        // } catch (URISyntaxException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
    }
}
