package com.yisa.morrowind.core;

/**
 * Created by Yisa on 2017/7/28.
 */

import com.yisa.morrowind.AppConfig;
import com.yisa.morrowind.proto.Request;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.context.ApplicationContext;

/**
 * 进程上下文执行环境
 */
public class ServerContext {


    static final ThreadLocal<Context> localContext = new ThreadLocal<Context>();
    public RequestMapping mapping;
    public AppConfig config;
    private ApplicationContext context;


    public ServerContext(AppConfig config, ApplicationContext context) {
        this.config = config;
        this.context = context;
        this.mapping = new RequestMapping(config, context);
    }

    /**
     * 获取业务执行的Request所有信息
     *
     * @return
     */
    public static Request getRequest() {
        return getContext().getRequest();
    }

    static Context getContext() {
        Context context = localContext.get();
        if (context == null) {
            throw new RuntimeException("Please apply " + ServerContext.class.getName()
                    + " to any request which uses servlet scopes.");
        }
        return context;
    }


    /**
     * 将上下文环境暴露给业务使用方
     * 将方法执行的上线文放入ThreadLocal中，便于业务逻辑中需要时获取对应的执行环境
     *
     * @param request 请求消息
     * @param ctx     netty执行上下文环境
     */
    public void setLocalContext(Request request, ChannelHandlerContext ctx) {
        localContext.set(new Context(request, ctx));
    }

    public void removeLocalContext() {
        localContext.remove();
    }

    public ActionMethod tackAction(String uri) {
        return mapping.tack(uri);
    }



    static class Context{
        final Request request;
        final ChannelHandlerContext response;

        Context(Request request , ChannelHandlerContext response){
            this.request = request;
            this.response = response;
        }

        public Request getRequest() {
            return request;
        }

        public ChannelHandlerContext getResponse() {
            return response;
        }
    }
}