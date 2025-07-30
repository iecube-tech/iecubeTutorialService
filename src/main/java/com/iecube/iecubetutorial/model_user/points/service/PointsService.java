package com.iecube.iecubetutorial.model_user.points.service;

import com.iecube.iecubetutorial.model.materials.entity.MaterialEntity;
import com.iecube.iecubetutorial.model_user.account.entity.Account;
import com.iecube.iecubetutorial.model_user.organization_sec.entity.OrgSec;
import com.iecube.iecubetutorial.model_user.points.dto.TokenUsed;
import com.iecube.iecubetutorial.model_user.points.entity.Points;
import com.iecube.iecubetutorial.model_user.points.exception.PointsNotEnoughException;
import com.iecube.iecubetutorial.model_user.points.vo.ConsumePointVo;
import com.iecube.iecubetutorial.model_user.points.vo.PointRecordVo;
import com.iecube.iecubetutorial.model_user.points.vo.YearMonthConsumptionResponse;

import java.util.List;

public interface PointsService {

    /**创建积分条目
     * 当该组织积分记录中没有有效的积分时，需要新建一个有效的积分条目
     * @param orgSec 二级组织
     * @param points 积分
     * @param creator 提审人
     */
    void createPoints(OrgSec orgSec, double points, String creator);

    /** 充值积分
     * 根据二级组织以及积分数量创建，积分数量由创建提审时的定价和金额计算而来
     * 当账户中没有有效积分时，充值时会新建一条有效积分
     * @param orgSec 二级组织
     * @param points 积分
     * @param creator 提审人
     */
    void rechargePoints(OrgSec orgSec, double points, String creator);

    /**
     * 扣除积分
     * @param accountId  账户Id
     * @param tokenUsed 扣费的条目
     */
    void consumePoints(Long accountId, TokenUsed tokenUsed, String type) throws PointsNotEnoughException;

    /**
     * 获取组织的剩余积分
     * @param oSecId 组织id
     * @return Points
     */
    Points getPointsValid(Long oSecId);


    Points getPointsValidByAccount();

    /**
     * 获取组织消费的总积分 及消耗明细
     * @param oSecId 组织Id
     * @return ConsumePointVo
     */
    ConsumePointVo getConsumePoint(Long oSecId);

    /**
     * 用户 获取组织消费的总积分 及消耗明细
     * @return ConsumePointVo
     */
    ConsumePointVo getConsumePointByAccount();

    /**
     * 获取组织所有有效 失效 总积分
     * @param oSecId 组织id
     * @return List<Points>
     */
    List<Points> getAllPoints(Long oSecId);

    /**
     * 获取组织账单
     * @param oSecId 组织id
     * @return List<PointsRecord>
     */
    List<PointRecordVo> getSecPointsRecords(Long oSecId);

    /**
     * 账户获取组织账单
     * @return List<PointsRecord>
     */
    List<PointRecordVo> getAccountPointsRecords();

    /**
     * 积分是否充足
     * @param account 账户
     * @return 充足 true 不够 抛出异常
     */
    boolean pointsEnough(Account account);

    /**
     * 二级组织按年月返回 账单
     * @param oSecId 二级组织id
     * @return YearMonthConsumptionResponse
     */
    YearMonthConsumptionResponse getAllConsumptionsGroupedByYearMonth(Long oSecId);

    /**
     * 账户按年月返回 账单
     * @return YearMonthConsumptionResponse
     */
    YearMonthConsumptionResponse getAllConsumptionsGroupedByYearMonth();

    //todo 通知即将到期的积分

    /**
     * 每天18点执行， 通知用户积分60天后到期
     */
    void notifyExpiringPoints();

    // todo 过期积分

    /**
     * 每天22点01执行，过期积分
     */
    void expirePoints();
}
