package com.iecube.iecubetutorial.model.sms.servcie;

import com.iecube.iecubetutorial.model.sms.service.SmsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SmsServiceTest {
    @Autowired
    private SmsService smsService;

    @Test
    public void testSend() {
        smsService.sendLoginSms("18792505903","123456");
    }
}
