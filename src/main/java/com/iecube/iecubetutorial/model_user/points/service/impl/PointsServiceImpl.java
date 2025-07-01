package com.iecube.iecubetutorial.model_user.points.service.impl;

import com.iecube.iecubetutorial.config.ThreadLocalUtil;
import com.iecube.iecubetutorial.model.materials.entity.MaterialEntity;
import com.iecube.iecubetutorial.model.sms.service.SmsService;
import com.iecube.iecubetutorial.model_admin.point.expire.service.APointExpireService;
import com.iecube.iecubetutorial.model_admin.price.service.PriceUnitService;
import com.iecube.iecubetutorial.model_user.account.entity.Account;
import com.iecube.iecubetutorial.model_user.account.service.AccountService;
import com.iecube.iecubetutorial.model_user.account.vo.AccountVo;
import com.iecube.iecubetutorial.model_user.organization_sec.entity.OrgSec;
import com.iecube.iecubetutorial.model_user.points.enmu.PointStatus;
import com.iecube.iecubetutorial.model_user.points.enmu.PointType;
import com.iecube.iecubetutorial.model_user.points.entity.Points;
import com.iecube.iecubetutorial.model_user.points.entity.PointsRecord;
import com.iecube.iecubetutorial.model_user.points.exception.PointsNotEnoughException;
import com.iecube.iecubetutorial.model_user.points.mapper.PointsMapper;
import com.iecube.iecubetutorial.model_user.points.mapper.PointsRecordMapper;
import com.iecube.iecubetutorial.model_user.points.service.PointsService;
import com.iecube.iecubetutorial.model_user.points.vo.ConsumePointVo;
import com.iecube.iecubetutorial.model_user.points.vo.PointRecordVo;
import com.iecube.iecubetutorial.model_user.points.vo.YearMonthConsumptionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
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

    @Autowired
    private AccountService accountService;

    @Autowired
    private SmsService smsService;

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
        Points point = pointsMapper.findValidPointsByOSecId(account.getOSecId());
        double price = priceUnitService.GeneratePriceUnit();
        point.setAmount(point.getAmount() - price);
        pointsMapper.updatePoints(point);
        this.pointsRecord(PointType.CONSUME.name(), account.getOSecId(), account, price,material);
    }

    @Override
    public Points getPointsValid(Long oSecId) {
        return pointsMapper.findValidPointsByOSecId(oSecId);
    }

    @Override
    public Points getPointsValidByAccount() {
        Account account = accountService.getAccount(ThreadLocalUtil.getAccountId());
        return getPointsValid(account.getOSecId());
    }

    @Override
    public ConsumePointVo getConsumePoint(Long oSecId) {
        List<PointRecordVo> ConsumeRecords = this.getSecPointsRecords(oSecId).stream()
                .filter(r-> PointType.CONSUME.name().equals(r.getType()))
                .toList();
        double total = 0.0;
        for(PointRecordVo record : ConsumeRecords) {
            total += record.getPoints();
        }
        ConsumePointVo consumePointVo  = new ConsumePointVo();
        consumePointVo.setConsumeTotal(total);
        consumePointVo.setConsumedRecord(ConsumeRecords);
        return consumePointVo;
    }

    @Override
    public ConsumePointVo getConsumePointByAccount() {
        Account account = accountService.getAccount(ThreadLocalUtil.getAccountId());
        return getConsumePoint(account.getOSecId());
    }

    @Override
    public List<Points> getAllPoints(Long oSecId) {
        return pointsMapper.findAllPointsByOSecId(oSecId);
    }

    @Override
    public List<PointRecordVo> getSecPointsRecords(Long oSecId) {
        List<PointRecordVo> consume = pointsRecordMapper.oSecConsume(oSecId);
        List<PointRecordVo> recharge = pointsRecordMapper.oSecRecharge(oSecId);
        List<PointRecordVo> all = new ArrayList<>();
        all.addAll(recharge);
        all.addAll(consume);
        return all.stream()
                .sorted(Comparator.comparing(PointRecordVo::getCreateTime).reversed())
                .toList();
    }

    @Override
    public List<PointRecordVo> getAccountPointsRecords() {
        Long accountId = ThreadLocalUtil.getAccountId();
        Account account = accountService.getAccount(accountId);
        return getSecPointsRecords(account.getOSecId());
    }

    @Override
    public boolean pointsEnough(Account account) {
        Points point = pointsMapper.findValidPointsByOSecId(account.getOSecId());
        double price = priceUnitService.GeneratePriceUnit();
        if(point == null) {
            throw new PointsNotEnoughException("余额不足");
        }
        if(point.getAmount() < price){
            throw new PointsNotEnoughException("余额不足");
        }
        return true;
    }

    @Override
    public YearMonthConsumptionResponse getAllConsumptionsGroupedByYearMonth(Long oSecId) {
        List<PointRecordVo> sortedAll = getSecPointsRecords(oSecId);
        YearMonthConsumptionResponse response = new YearMonthConsumptionResponse();
        // 按年月分组
        for (PointRecordVo record : sortedAll) {
            response.addRecord(record);
        }
        return response;
    }

    @Override
    public YearMonthConsumptionResponse getAllConsumptionsGroupedByYearMonth() {
        Account account = accountService.getAccount(ThreadLocalUtil.getAccountId());
        List<PointRecordVo> sortedAll = getSecPointsRecords(account.getOSecId());
        YearMonthConsumptionResponse response = new YearMonthConsumptionResponse();
        // 按年月分组
        for (PointRecordVo record : sortedAll) {
            response.addRecord(record);
        }
        return response;
    }

    @Override
    public void notifyExpiringPoints() {
        //每天的18点执行，通知用户60/30/1天后到期
        Instant currentDate = LocalDateTime.now(ZoneId.systemDefault()).atZone(ZoneId.systemDefault()).toInstant();
        List<Points> willExpiredPoints = pointsMapper.willExpiredIn60Days(currentDate);
        if(willExpiredPoints.isEmpty()) {
            return;
        }
        willExpiredPoints.forEach(point ->{
            //根据二级组织查询管理员
            List<AccountVo> accountVoList = accountService.getOrgSecUserMByOrgSecId(point.getOSecId());
            Instant expireTime = point.getExpireDate();
            LocalDate expireTimeL = expireTime.atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate currentTime = LocalDate.now(ZoneId.systemDefault());
            long daysDifference = ChronoUnit.DAYS.between(currentTime, expireTimeL);
            accountVoList.forEach(accountVo -> {
                smsService.sendExpireDaysNotifySms(accountVo.getPhone(), accountVo.getOSecName(),daysDifference-1);
            });
        });
    }

    @Override
    public void expirePoints() {
        Instant currentTime = LocalDateTime.now(ZoneId.systemDefault())
                .withSecond(30)
                .atZone(ZoneId.systemDefault())
                .toInstant();
        pointsMapper.expiredPoints(currentTime);
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
        // 获取当前本地日期，设置时间为22点，再加上181天
        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.systemDefault())
                .plusDays(days+1)
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
