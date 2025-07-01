package com.iecube.iecubetutorial.model_user.points.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PointsServiceTest {

    @Autowired
    private PointsService pointsService;

    @Test
    public void notifyTest(){
        pointsService.notifyExpiringPoints();
    }
}
