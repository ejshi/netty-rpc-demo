package com.sjz.rpc.client;

import java.util.concurrent.ConcurrentHashMap;

public class DataStoreSupport {

    private static ConcurrentHashMap<String, RpcFuture> futureMap = new ConcurrentHashMap<>();

    public static void setFutureToMap(RpcFuture rpcFuture){
        futureMap.put(rpcFuture.getRpcRequest().getRequestId(), rpcFuture);
    }

    public static RpcFuture getFutureFromMap(String requestId){
        return futureMap.get(requestId);
    }

    public static void removeFutureFromMap(String requestId){
        futureMap.remove(requestId);
    }
}
