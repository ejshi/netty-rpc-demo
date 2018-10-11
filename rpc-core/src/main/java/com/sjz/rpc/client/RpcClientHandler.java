package com.sjz.rpc.client;

import com.sjz.rpc.model.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        System.out.println("client receive RPC server process result");
        RpcFuture future = DataStoreSupport.getFutureFromMap(rpcResponse.getRequestId());
        if(future != null){
            System.out.println("client receive RPC server return result to local cache");
            future.done(rpcResponse);
        }else{
            System.out.println("RPC recall error，requestId = " + rpcResponse.getRequestId());
            throw new IllegalStateException("RPC recall error，requestId = " + rpcResponse.getRequestId());
        }
    }
}
