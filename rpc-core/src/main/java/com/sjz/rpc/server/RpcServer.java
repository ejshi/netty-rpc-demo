package com.sjz.rpc.server;

import com.sjz.rpc.model.RpcRequest;
import com.sjz.rpc.model.RpcResponse;
import com.sjz.rpc.registry.ServerRegister;
import com.sjz.rpc.serialize.RpcDecoder;
import com.sjz.rpc.serialize.RpcEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "RpcServer")
public class RpcServer {

    /**
     * RPC服务IP
     */
    private String serverAddress ;

    private NioEventLoopGroup boss;
    private NioEventLoopGroup worker;

    public RpcServer(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public void start() throws InterruptedException {
        System.out.println("============== rpcServer start ==============");
        boss = new NioEventLoopGroup();
        worker = new NioEventLoopGroup();

        ServerBootstrap s = new ServerBootstrap();
        s.group(boss, worker)
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(
                            new LengthFieldBasedFrameDecoder(65536, 0, 4 ,0,0));
                    socketChannel.pipeline().addLast(new RpcDecoder(RpcRequest.class));
                    socketChannel.pipeline().addLast(new RpcEncoder(RpcResponse.class));
                    socketChannel.pipeline().addLast(new RpcServerHandler());
                }
            })
            .option(ChannelOption.SO_BACKLOG,1024)
            .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);

        String[] addressArray = serverAddress.split(":");
        String host = addressArray[0];
        int port = Integer.parseInt(addressArray[1]);

        ChannelFuture f = s.bind(host, port).sync();
        //注册RPC服务到注册中心
        ServerRegister.registerServer("rpc-api", host, port);

        System.out.println("========== rpc server start success ==========");

        f.channel().closeFuture().sync();

    }

    public void stop(){
        if(boss != null){
            boss.shutdownGracefully();
        }
        if(worker != null){
            worker.shutdownGracefully();
        }
    }
}
