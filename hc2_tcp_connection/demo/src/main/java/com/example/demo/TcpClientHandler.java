//package com.example.tsing.client.handle;
package com.example.demo;

import com.alibaba.fastjson2.JSONObject;
import com.example.demo.SubDeviceData;
import com.example.demo.Login;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务器监听处理器
 */
@Slf4j
public class TcpClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    /**
     * 消息接口
     */


    /**
     * 绑定
     *
     * @param channelHandlerContext
     * @param byteBuf
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        log.info("client connect :{}", byteBuf.toString(CharsetUtil.UTF_8));
    }


    /**
     * 取消绑定
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelInactive channleId:{}", ctx.channel().id());
        try {
        } catch (Exception e) {
            log.error("{}", e);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("channelActive channleId:{}", ctx.channel().id());
        try {
            //Use the online device's uid
            String[] uids = new String[]{"AD8A61A0B59F"};
            String token = Login.loginAndGetToken();
            new Thread(() -> {
                for (String uid : uids) {
                    SubDeviceData deviceData = new SubDeviceData();
                    deviceData.setUid(uid);
                    deviceData.setSecond((short) 10);
                    deviceData.setCmd("DEVICE_DATA");
                    //Generate new token every 1.5 hour
                    deviceData.setToken(token);
                    deviceData.setUserName("Aeyesafe@outlook.com");
                    if (ctx.channel().isWritable()) {
                        ctx.writeAndFlush(JSONObject.toJSONString(deviceData));
                    }else {
                        System.out.println("Server is not writable");
                    }
                }
            }).start();
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.error("--------------channelRead------{}",msg);
        super.channelRead(ctx, msg);
    }

    /**
     * 心跳机制  用户事件触发
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            switch (idleStateEvent.state()) {
                case WRITER_IDLE:
                    JSONObject r = new JSONObject();
                    r.put("cmd", "PING");
                    ctx.writeAndFlush(r.toJSONString());
                    // 写入超时
                    // 处理写入超时的逻辑
                    break;
                case READER_IDLE:

            }
        }
        super.userEventTriggered(ctx, evt);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        log.error("exceptionCaught:{}", cause);

    }
}



