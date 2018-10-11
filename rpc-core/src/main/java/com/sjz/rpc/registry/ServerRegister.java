package com.sjz.rpc.registry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO 注册中心，需要使用zookeeper替换
 */
@Slf4j
public class ServerRegister {

    //单机服务
    private static Map<String, InetSocketAddress> registerServerMap = Maps.newHashMap();

    //注册的bean服务，key/value -- serverName/serverBean
    private static ConcurrentHashMap<String,Object> beanMap = new ConcurrentHashMap<>();

    static{
        registerServerMap.put("rpc-api", new InetSocketAddress("127.0.0.1", 8080));
    }
    /**
     * 注册服务
     * @param serverName 服务名称
     * @param ip
     * @param port
     */
    public static void registerServer(String serverName, String ip, int port){
        InetSocketAddress inetSocketAddress = registerServerMap.get(serverName);
        if(inetSocketAddress == null){
            inetSocketAddress = new InetSocketAddress(ip, port);
            registerServerMap.put(serverName, inetSocketAddress);
        }else{
            log.info("server={} has register",serverName);
        }
    }

    /**
     * 删除服务
     * @param serverName
     */
    public static void removeServer(String serverName){
        registerServerMap.remove(serverName);
    }

    /**
     * 订阅服务
     * @return
     */
    public static List<InetSocketAddress> subscribeServer(){
        Collection<InetSocketAddress> socketAddresses = registerServerMap.values();
        if(socketAddresses == null){
            return Collections.EMPTY_LIST;
        }
        return Lists.newArrayList(socketAddresses);
    }

    /**
     * 注册bean
     * @param bean
     */
    public static void registerBean(String beanName, Object bean){
        beanMap.put(beanName, bean);
    }

    /**
     * 获取bean
     * @param beanName
     * @return
     */
    public static Object getBean(String beanName){
        return beanMap.get(beanName);
    }
}
