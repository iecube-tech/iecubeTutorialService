package com.iecube.iecubetutorial.model.htmlEditAi.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class RegisterServiceTest {
    @Autowired
    private RegisterService registerService;

    @Test
    public void test(){
        System.out.println(registerService.register());
    }
}
