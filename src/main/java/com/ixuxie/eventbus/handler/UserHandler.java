package com.ixuxie.eventbus.handler;

import com.google.common.eventbus.Subscribe;
import com.ixuxie.eventbus.event.UserEvent;
import com.ixuxie.service.UserService;
import com.ixuxie.utils.eventbus.EventBusHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserHandler implements EventBusHandler {

    private static final Logger logger = LoggerFactory.getLogger(UserHandler.class);

    @Autowired
    private UserService userService;


    /**
     * 注册通知：1、给邀请人发优惠券，2、给注册人发送优惠券
     * StringUtils.leftPad(Hex36.encode(userDto.getId()),6, "0")
     * @param user
     */
    @Subscribe
    public void register(UserEvent user){


        }

}
