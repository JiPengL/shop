package com.ixuxie;


import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.ixuxie.utils.eventbus.EventBusDispatcher;
import com.ixuxie.utils.eventbus.listener.EventBusListener;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@MapperScan("com.ixuxie.mappper")
public class ShopApplication {

    public static void main( String[] args ) {
        try {
            ConfigurableApplicationContext run = SpringApplication.run(ShopApplication.class, args);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Bean
    public EventBusDispatcher eventBusDispatcher(){
        return new EventBusDispatcher();
    }

    @Bean
    public EventBusListener eventBusListener(){
        return new EventBusListener();
    }
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}

