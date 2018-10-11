package rpc.client;

import com.sjz.api.HelloService;
import com.sjz.rpc.client.RpcClient;
import com.sjz.rpc.client.RpcInvoker;

public class RpcClientTest {
    public static void main(String[] args) throws InterruptedException {

        System.out.println("====================开始启动客户端");
        RpcClient rpcClient = RpcClient.getInstance();
        rpcClient.start();

        HelloService helloService = RpcInvoker.create(HelloService.class);
        String netty = helloService.hello("netty");
        System.out.println("======================" + netty);

        String abc = helloService.hello("abc");
        System.out.println("======================" + abc);

        while(true){
            //防止主线程kill
        }
    }
}
