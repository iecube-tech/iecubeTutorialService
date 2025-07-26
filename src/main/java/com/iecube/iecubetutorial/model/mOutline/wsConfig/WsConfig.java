package com.iecube.iecubetutorial.model.mOutline.wsConfig;

import com.iecube.iecubetutorial.model.mOutline.wsHandler.WsHandler;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocket
public class WsConfig implements WebSocketConfigurer {

    private final WsHandler wsHandler;

    public WsConfig(WsHandler wsHandler) {
        this.wsHandler = wsHandler;
    }

    @Override
    @Operation(summary = "修改html接口")
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(wsHandler, "/outline/receive/{mOutlineId}")
                .setAllowedOrigins("*");
    }  // todo 要给这个连接地址添加拦截器，确保用户和mOutlineId 可以相对应

    @Bean
    public ServletServerContainerFactoryBean createOutlineWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxBinaryMessageBufferSize(8192);
        return container;
    }
}