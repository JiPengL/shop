package com.ixuxie.utils.eventbus;

import com.google.common.eventbus.AsyncEventBus;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * EventBus事件分发器
 * eventBusDispatcher.register(new MallHandler()
 *      @Subscribe
 *     public void handler(TestEvent te){
 *         System.out.println(te);
 *     }
 * );
 * eventBusDispatcher.dispatcher(new TestEvent());
 *
 */
public class EventBusDispatcher {

    private final AsyncEventBus asyncEventBus;


    public EventBusDispatcher(){
        asyncEventBus = new AsyncEventBus(new ThreadPoolExecutor(2,4,50, TimeUnit.MICROSECONDS,new LinkedBlockingDeque(500)));
    }


    public EventBusDispatcher(int coreThreadSize, int maxThreadSize, int queueSize){
        asyncEventBus = new AsyncEventBus(new ThreadPoolExecutor(coreThreadSize,maxThreadSize,50, TimeUnit.MICROSECONDS,new LinkedBlockingDeque(queueSize)));
    }

    /**
     * 触发事件
     */
    public void dispatcher(Event event){
        asyncEventBus.post(event);
    }

    /**
     * 注册处理器
     * @param eventBusHandler
     */
    public void register(EventBusHandler eventBusHandler){
        asyncEventBus.register(eventBusHandler);
    }

    /**
     * 注销处理器
     * @param eventBusHandler
     */
    public void unregister(EventBusHandler eventBusHandler){
        asyncEventBus.unregister(eventBusHandler);
    }

}
