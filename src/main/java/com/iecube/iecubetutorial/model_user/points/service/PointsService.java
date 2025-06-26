package com.iecube.iecubetutorial.model_user.points.service;

import com.iecube.iecubetutorial.model.materials.entity.MaterialEntity;
import com.iecube.iecubetutorial.model_user.account.entity.Account;
import com.iecube.iecubetutorial.model_user.organization_sec.entity.OrgSec;
import com.iecube.iecubetutorial.model_user.points.entity.Points;
import com.iecube.iecubetutorial.model_user.points.entity.PointsRecord;
import com.iecube.iecubetutorial.model_user.points.vo.ConsumePointVo;

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

    // 消费积分
    void consumePoints(Account account, MaterialEntity material);

    // 获取组织有效积分余额
    Points getPointsValid(Long oSecId);

    // 获取组织消费的总积分 及消耗明细
    ConsumePointVo getConsumePoint(Long oSecId);

    // 获取组织所有积分记录
    List<Points> getAllPoints(Long oSecId);

    // 获取组织账单
    List<PointsRecord> getSecPointsRecords(Long oSecId);

    // 获取账户账单
    List<PointsRecord> getAccountPointsRecords(Long accountId);

    boolean pointsEnough(Account account);

    // 检查并通知即将到期的积分
    void checkAndNotifyExpiringPoints();
}
