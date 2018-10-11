package rpc.server;

import com.sjz.api.HelloService;
import com.sjz.provider.HelloServiceImpl;
import com.sjz.rpc.registry.ServerRegister;
import com.sjz.rpc.server.RpcServer;

public class RpcServerTest {
    public static void main(String[] args) throws InterruptedException {
        ServerRegister.registerBean(HelloService.class.getName(), new HelloServiceImpl());
        new RpcServer("127.0.0.1:8080").start();

        while(true){
            //防止主线程kill
        }
    }
}
