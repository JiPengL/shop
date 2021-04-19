package com.ixuxie.utils.eventbus.listener;

import com.ixuxie.utils.eventbus.EventBusDispatcher;
import com.ixuxie.utils.eventbus.EventBusHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collection;
import java.util.Map;

public class EventBusListener implements ApplicationContextAware, InitializingBean, DisposableBean {

    private ApplicationContext applicationContext;

    private EventBusDispatcher eventBusDispatcher;

    private Map<String, EventBusHandler> eventBusHandlers;

    @Override
    public void destroy() throws Exception {
        if (eventBusDispatcher == null){
            return;
        }
        if (eventBusHandlers != null && !eventBusHandlers.isEmpty()){
            Collection<EventBusHandler> ebhs = eventBusHandlers.values();
            for (EventBusHandler ebh : ebhs) {
                eventBusDispatcher.unregister(ebh);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (eventBusDispatcher == null){
            return;
        }
        eventBusHandlers = applicationContext.getBeansOfType(EventBusHandler.class);
        if (eventBusHandlers != null && !eventBusHandlers.isEmpty()){
            Collection<EventBusHandler> ebhs = eventBusHandlers.values();
            for (EventBusHandler ebh : ebhs) {
                eventBusDispatcher.register(ebh);
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        eventBusDispatcher = applicationContext.getBean(EventBusDispatcher.class);
    }
}
