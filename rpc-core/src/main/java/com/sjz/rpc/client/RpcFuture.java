package com.sjz.rpc.client;

import com.sjz.rpc.model.RpcRequest;
import com.sjz.rpc.model.RpcResponse;

import java.util.concurrent.*;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class RpcFuture implements Future {

    private RpcRequest rpcRequest;

    private RpcResponse rpcResponse;

    private Sync sync;

    public RpcFuture(RpcRequest rpcRequest) {
        System.out.println("future 初始化 >>>>");
        this.rpcRequest = rpcRequest;
        this.sync = new Sync();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDone() {
        return sync.isDone();
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        System.out.println("future 同步阻塞返回数据 >>>>");
        sync.acquire(1);
        for(;;){
            if(isDone() && null!= getRpcResponse()){
                System.out.println("future 执行成功 >>>>");
                DataStoreSupport.removeFutureFromMap(getRpcRequest().getRequestId());
                return getRpcResponse().getData();
            }
            //休眠10毫秒
            TimeUnit.MILLISECONDS.sleep(10);
        }
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        sync.tryAcquireNanos(1, unit.toNanos(timeout));
        for(;;){
            if(isDone() && null!= getRpcResponse()){
                DataStoreSupport.removeFutureFromMap(getRpcRequest().getRequestId());
                return getRpcResponse().getData();
            }
            //休眠5毫秒
            TimeUnit.MILLISECONDS.sleep(5);
        }
    }

    /**
     * 执行完成
     * @param rpcResponse
     */
    public void done(RpcResponse rpcResponse) throws InterruptedException {

        for(;;){//自旋判断是否完成,防止出现在get之前，调用done方法
            if(!isDone() && null == getRpcResponse()){
                sync.release(1);
                System.out.println("future 获取到数据 >>>>");
                setRpcResponse(rpcResponse);
                return;
            }
            //休眠3毫秒
            TimeUnit.MILLISECONDS.sleep(3);
        }
    }


    static class Sync extends AbstractQueuedSynchronizer {

        public boolean isDone(){
            return getState() == 0;
        }

        @Override
        protected boolean tryAcquire(int arg) {
            if(compareAndSetState(0, 1)){
                System.out.println("===========acquire获取成功");
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int arg) {
            if(getState() == 0){
                return true;
            }
            System.out.println("===========Release释放成功");
            setState(0);
            setExclusiveOwnerThread(null);
            return true;
        }
    }

    public RpcRequest getRpcRequest() {
        return rpcRequest;
    }

    public void setRpcRequest(RpcRequest rpcRequest) {
        this.rpcRequest = rpcRequest;
    }

    public RpcResponse getRpcResponse() {
        return rpcResponse;
    }

    public void setRpcResponse(RpcResponse rpcResponse) {
        this.rpcResponse = rpcResponse;
    }
}
