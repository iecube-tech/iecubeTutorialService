package com.iecube.iecubetutorial.model_user.points.service.impl;

import com.iecube.iecubetutorial.config.ThreadLocalUtil;
import com.iecube.iecubetutorial.model.materials.entity.MaterialEntity;
import com.iecube.iecubetutorial.model_admin.point.expire.service.APointExpireService;
import com.iecube.iecubetutorial.model_admin.price.service.PriceUnitService;
import com.iecube.iecubetutorial.model_user.account.entity.Account;
import com.iecube.iecubetutorial.model_user.organization_sec.entity.OrgSec;
import com.iecube.iecubetutorial.model_user.points.enmu.PointStatus;
import com.iecube.iecubetutorial.model_user.points.enmu.PointType;
import com.iecube.iecubetutorial.model_user.points.entity.Points;
import com.iecube.iecubetutorial.model_user.points.entity.PointsRecord;
import com.iecube.iecubetutorial.model_user.points.mapper.PointsMapper;
import com.iecube.iecubetutorial.model_user.points.mapper.PointsRecordMapper;
import com.iecube.iecubetutorial.model_user.points.service.PointsService;
import com.iecube.iecubetutorial.model_user.points.vo.ConsumePointVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;

@Service
public class PointsServiceImpl implements PointsService {

    @Autowired
    private PointsMapper pointsMapper;

    @Autowired
    private PointsRecordMapper pointsRecordMapper;

    @Autowired
    private APointExpireService aPointExpireService;

    @Autowired
    private PriceUnitService priceUnitService;

    @Override
    public void createPoints(OrgSec orgSec, double points, String creator) {
        int expireDays = aPointExpireService.getExpireDays();
        Points point = new Points();
        point.setOSecId(orgSec.getId());
        point.setAmount(points);
        point.setStatus(PointStatus.ACTIVE.name());
        point.setExpireDate(computeExpireDate(expireDays));
        point.setCreator(creator);
        point.setCreateTime(Instant.now());
        point.setLastOperateTime(Instant.now());
        point.setLastOperator(ThreadLocalUtil.getPhone());
        pointsMapper.createPoints(point);
        this.pointsRecord(PointType.RECHARGE.name(), orgSec.getId(), null, points, null);
    }

    @Override
    public void rechargePoints(OrgSec orgSec, double points, String creator) {
        Points point = pointsMapper.findValidPointsByOSecId(orgSec.getId());
        if(point == null) {
            createPoints(orgSec, points, creator);
        }else {
            int expireDays = aPointExpireService.getExpireDays();
            point.setAmount(point.getAmount() + points);
            point.setStatus(PointStatus.ACTIVE.name());
            point.setExpireDate(computeExpireDate(expireDays));
            point.setLastOperateTime(Instant.now());
            point.setLastOperator(ThreadLocalUtil.getPhone());
            pointsMapper.updatePoints(point);
            this.pointsRecord(PointType.RECHARGE.name(), orgSec.getId(), null, points,null);
        }
    }

    @Override
    public void consumePoints(Account account, MaterialEntity material) {
        double price = priceUnitService.GeneratePriceUnit();


    }

    @Override
    public Points getPointsValid(Long oSecId) {
        return pointsMapper.findValidPointsByOSecId(oSecId);
    }

    @Override
    public ConsumePointVo getConsumePoint(Long oSecId) {
        List<PointsRecord> ConsumeRecords = this.getSecPointsRecords(oSecId).stream()
                .filter(r-> PointType.CONSUME.name().equals(r.getType()))
                .toList();
        double total = 0.0;
        for(PointsRecord record : ConsumeRecords) {
            total += record.getPoints();
        }
        ConsumePointVo consumePointVo  = new ConsumePointVo();
        consumePointVo.setConsumeTotal(total);
        consumePointVo.setConsumedRecord(ConsumeRecords);
        return consumePointVo;
    }

    @Override
    public List<Points> getAllPoints(Long oSecId) {
        return pointsMapper.findAllPointsByOSecId(oSecId);
    }

    @Override
    public List<PointsRecord> getSecPointsRecords(Long oSecId) {
        return pointsRecordMapper.getByOSecId(oSecId);
    }

    @Override
    public List<PointsRecord> getAccountPointsRecords(Long accountId) {
        return pointsRecordMapper.getByAccount(accountId);
    }

    @Override
    public boolean pointsEnough(Account account) {
        Points point = pointsMapper.findValidPointsByOSecId(account.getId());
        double price = priceUnitService.GeneratePriceUnit();
        if(point == null) {
            return false;
        }
        return point.getAmount() > price;
    }

    @Override
    public void checkAndNotifyExpiringPoints() {

    }

    private void pointsRecord(String type, Long oSecId, Account account, double point, MaterialEntity material) {
        PointsRecord pointsRecord = new PointsRecord();
        pointsRecord.setOSecId(oSecId);
        pointsRecord.setType(type);
        pointsRecord.setAccountId(account==null?null:account.getId());
        pointsRecord.setPoints(point);
        pointsRecord.setMaterialId(material==null?null:material.getId());
        pointsRecord.setCreateTime(Instant.now());
        pointsRecordMapper.create(pointsRecord);
    }

    /**
     * 计算过期时间
     * @return Instant 过期日期的22点
     */
    private Instant computeExpireDate(int days) {
        // 获取当前本地日期，设置时间为22点，再加上180天
        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.systemDefault())
                .plusDays(days)
                .withHour(22)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        // 转换为 Instant（UTC 时间）
        return localDateTime
                .atZone(ZoneId.systemDefault())  // 转为本地时区的 ZonedDateTime
                .toInstant();                    // 提取 Instant（自动转换为 UTC）
    }
}
