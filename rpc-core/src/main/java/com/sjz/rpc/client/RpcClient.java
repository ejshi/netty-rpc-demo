package com.sjz.rpc.client;

import com.sjz.rpc.model.RpcRequest;
import com.sjz.rpc.model.RpcResponse;
import com.sjz.rpc.registry.ServerRegister;
import com.sjz.rpc.serialize.RpcDecoder;
import com.sjz.rpc.serialize.RpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j(topic = "RpcClient")
public class RpcClient {

    private volatile Channel channel;

    private NioEventLoopGroup worker;

    private static final RpcClient rpcClient = new RpcClient();

    private RpcClient(){

    }

    public static RpcClient getInstance(){
        return rpcClient;
    }

    public void start() throws InterruptedException {
        System.out.println("=========== client start ============");

        worker = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(worker).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) {
                socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(65536,0,4,0,0));
                socketChannel.pipeline().addLast(new RpcDecoder(RpcResponse.class));
                socketChannel.pipeline().addLast(new RpcEncoder(RpcRequest.class));
                socketChannel.pipeline().addLast(new RpcClientHandler());
            }
        }).option(ChannelOption.SO_KEEPALIVE , true);

        List<InetSocketAddress> addressList = ServerRegister.subscribeServer();
        if(CollectionUtils.isEmpty(addressList)){
            throw new IllegalArgumentException("rpc server ip is null");
        }

        System.out.println("======client connect===========");
        //同步阻塞链接
        ChannelFuture f = b.connect(addressList.get(0)).sync();

        channel = f.channel();

        //使用异步，需要添加监听
//        ChannelFuture f = b.connect(addressList.get(0));
//        f.addListener(channelFuture -> {
//            if(channelFuture.isSuccess()){
//                System.out.println("client link to server success");
//                channel = f.channel(); //使用异步需要结合countDownLatch使用
//                countDownLatch.countDown();
//            }
//        });

        System.out.println("=========== client start success ============");
    }

    public void stop(){
        if(worker != null){
            worker.shutdownGracefully();
        }
    }

    /**
     * 发送数据
     * @param rpcRequest
     * @throws InterruptedException
     */
    public RpcFuture sendRequest(RpcRequest rpcRequest) throws InterruptedException {

        RpcFuture rpcFuture = new RpcFuture(rpcRequest);
        DataStoreSupport.setFutureToMap(rpcFuture);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        channel.writeAndFlush(rpcRequest).addListener(channelFuture -> {

            countDownLatch.countDown();
        });

        countDownLatch.await();
        return rpcFuture;
    }
}
