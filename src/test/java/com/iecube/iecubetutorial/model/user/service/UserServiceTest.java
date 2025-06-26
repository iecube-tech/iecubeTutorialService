package com.iecube.iecubetutorial.model.user.service;

import com.iecube.iecubetutorial.model.user.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Test
    public void addUserTest() {
//        User user =userService.addUser("XMU1", "654321", "评审专家");
        User user2 =userService.addUser("XMU2", "654321", "评审专家");
        User user3 =userService.addUser("XMU3", "654321", "评审专家");
        User user4 =userService.addUser("XMU4", "654321", "评审专家");
        User user5 =userService.addUser("XMU5", "654321", "评审专家");
    }
}
