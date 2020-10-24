package com.ncst.transport.client;

import com.ncst.rpc.entity.RpcResponse;
import com.ncst.rpc.enumeration.RpcError;
import com.ncst.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/21/15:23
 * @Description:
 */
public class UnprocessedRequests {

    private static final Logger logger = LoggerFactory.getLogger(UnprocessedRequests.class);
    private static ConcurrentHashMap<String, CompletableFuture<RpcResponse>> unprocessedMap =
            new ConcurrentHashMap<>();

    public void put (String requestId , CompletableFuture<RpcResponse> future) {

        try {
            unprocessedMap.put(requestId,future);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void remove (String requestId) {
        unprocessedMap.remove(requestId);
    }

    public void complete (RpcResponse rpcResponse) {
        CompletableFuture<RpcResponse> future = unprocessedMap.remove(rpcResponse.getRequestId());
        if (future != null) {
            future.complete(rpcResponse);
        } else {
            logger.error("...rpc not complete...");
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
    }

}
