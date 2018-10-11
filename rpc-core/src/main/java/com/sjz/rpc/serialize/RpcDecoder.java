package com.sjz.rpc.serialize;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> genericClass;

    public RpcDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception {
        if(in.readableBytes() < 4){ //偏移量长度为4
            return;
        }

        in.markWriterIndex();

        //按照顺序读取数据
        int dataLength = in.readInt();

        if(in.readableBytes() < dataLength){
            in.resetReaderIndex();
            return;
        }

        byte[] bytes = new byte[dataLength];
        in.readBytes(bytes);

        Object data = SerializationUtil.deserialize(bytes, genericClass);

        list.add(data);
    }
}
