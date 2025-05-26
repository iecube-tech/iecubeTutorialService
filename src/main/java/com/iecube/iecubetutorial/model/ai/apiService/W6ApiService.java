package com.iecube.iecubetutorial.model.ai.apiService;


import com.fasterxml.jackson.databind.JsonNode;

public interface W6ApiService {

    String genChat();

    void usePageMaker(String chatId, String title, String knowledgePoints, String instruction);

    JsonNode getJsonRes(String artefactId);
}
