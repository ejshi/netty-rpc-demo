package com.sjz.rpc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse {

    private String requestId;
    private boolean result;
    private Object data;
    private String message;


}
