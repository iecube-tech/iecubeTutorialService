package com.iecube.iecubetutorial.model.ai.aiMiddlware;

import com.iecube.iecubetutorial.model.ai.consumer.ConnectToW6;
import com.iecube.iecubetutorial.model.ai.consumer.ParseArtefactAndUpdateMaterial;
import com.iecube.iecubetutorial.model.ai.dto.ParseArtefactDto;
import com.iecube.iecubetutorial.model.materials.entity.MaterialChat;
import com.iecube.iecubetutorial.model.materials.entity.MaterialEntity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.web.socket.WebSocketSession;

@Configuration
public class WebSocketSessionManage {

    @Bean
    public ConcurrentHashMap<String, WebSocketSession> ChatIdToSession(){
        return new ConcurrentHashMap<>();
    }

    @Bean
    public ConcurrentHashMap<String, String>SessionIdToChatId(){
        return new ConcurrentHashMap<>();
    }

    @Bean
    public ConcurrentHashMap<String, MaterialEntity>ChatIdToMaterial(){
        return new ConcurrentHashMap<>();
    }

    /**
     * 需要建立的Websocket连接
     * @return 队列
     */
    @Bean
    public BlockingQueue<MaterialChat> NewConnectTask(){
        return new LinkedBlockingQueue<>();
    }

    @Bean
    public CommandLineRunner ConnectToW6Consumer(ConnectToW6 connectToW6) {
        return args -> {
            Thread thread =  new Thread(connectToW6);
            thread.setName("w6-connect");
            thread.start();
        };
    }

    @Bean
    public BlockingQueue<ParseArtefactDto> NewParseTask(){
        return new LinkedBlockingQueue<>();
    }

    @Bean
    public CommandLineRunner ParseArtefactIdConsumer(ParseArtefactAndUpdateMaterial parseArtefactAndUpdateMaterial){
        return args -> {
            Thread thread = new Thread(parseArtefactAndUpdateMaterial);
            thread.setName("parse-artefactId");
            thread.start();
        };
    }



}
