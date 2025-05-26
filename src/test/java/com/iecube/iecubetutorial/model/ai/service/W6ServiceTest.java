package com.iecube.iecubetutorial.model.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.iecube.iecubetutorial.model.ai.apiService.W6ApiService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class W6ServiceTest {

    @Autowired
    private W6ApiService w6ApiService;

    @Test
    public void test() {
        JsonNode res = w6ApiService.getJsonRes("1r7gvOqRkbzXSMm5WOpxBa");
        System.out.println(res);
    }
}
