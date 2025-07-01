package com.iecube.iecubetutorial.config;

import com.iecube.iecubetutorial.model_user.points.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class ExpiredConfig {
    @Autowired
    private PointsService pointsService;

    // 每天18点检查即将到期的积分
    @Scheduled(cron = "0 0 18 * * ?")
    public void notifyExpiringPoints() {
        pointsService.notifyExpiringPoints();
    }

    // 每天22点检查即将到期的积分
    @Scheduled(cron = "0 0 22 * * ?")
    public void ExpiringPoints() {
        pointsService.expirePoints();
    }
}
