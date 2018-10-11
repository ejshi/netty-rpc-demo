package com.sjz.rpc.server;

import com.alibaba.fastjson.JSON;
import com.sjz.rpc.model.RpcRequest;
import com.sjz.rpc.model.RpcResponse;
import com.sjz.rpc.registry.ServerRegister;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j(topic = "rpcServerHandler")
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        System.out.println("server receive data ...");
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(rpcRequest.getRequestId());

        Object data = doHandler(rpcRequest);
        rpcResponse.setData(data);
        rpcResponse.setResult(true);
        channelHandlerContext.writeAndFlush(rpcResponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("server cause exception", cause);
        ctx.close();
    }

    /**
     * jdk反射调用获取方法执行结果
     * @param rpcRequest 请求参数
     * @return
     * @throws Exception
     */
    private Object doHandler(RpcRequest rpcRequest) throws Exception {
        log.info("server receive client request, requestId = {}",rpcRequest.getRequestId());
        System.out.println("###########"+rpcRequest.getClassName());

        Object bean = ServerRegister.getBean(rpcRequest.getClassName());
        Class<?> beanClass = bean.getClass();

        Method method = beanClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());

        Object result = method.invoke(bean, rpcRequest.getParameters());
        System.out.println("回调结果集："+JSON.toJSONString(result));
        return result;
    }
}
