package com.tyrion.jrpc.client;

import com.alibaba.fastjson.JSON;
import com.tyrion.jrpc.common.RpcRequest;
import com.tyrion.jrpc.common.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author TyrionJ
 * @date 2020/7/13 11:31
 */
@Slf4j
public class RpcClient {

    private static Bootstrap bootstrap;

    private String host;

    private int port;

    RpcClient host(String host) {
        this.host = host;
        return this;
    }

    RpcClient port(int port) {
        this.port = port;
        return this;
    }

    static {
        bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();

        bootstrap.group(group).channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                pipeline.addLast(new LengthFieldPrepender(4));
                pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                pipeline.addLast(new SimpleChannelInboundHandler<String>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) {
                        log.debug("receive response:{}", s);
                        RpcResponse response = JSON.parseObject(s, RpcResponse.class);
                        AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                        channelHandlerContext.channel().attr(key).set(response);
                        channelHandlerContext.channel().close();
                    }
                });
            }
        });
    }

    public RpcResponse send(RpcRequest request) throws InterruptedException {
        ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
        Channel channel = channelFuture.channel();
        channel.writeAndFlush(JSON.toJSONString(request));
        // go on when channel closed
        channelFuture.channel().closeFuture().sync();
        AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
        return channel.attr(key).get();
    }
}
