package com.iecube.iecubetutorial.model_admin.operator.service;

import com.iecube.iecubetutorial.model_admin.approval.approval.entity.Approval;
import com.iecube.iecubetutorial.model_admin.operator.qo.AddUUserQo;
import com.iecube.iecubetutorial.model_admin.operator.qo.OrgSecQo;
import com.iecube.iecubetutorial.model_admin.operator.qo.RechargeQo;
import com.iecube.iecubetutorial.model_admin.operator.vo.OrganizationVo;
import com.iecube.iecubetutorial.model_admin.price.qo.PriceChangeQo;
import com.iecube.iecubetutorial.model_admin.user.entity.AUser;
import com.iecube.iecubetutorial.model_user.account.vo.AccountVo;
import com.iecube.iecubetutorial.model_user.organization_sec.entity.OrgSec;
import com.iecube.iecubetutorial.model_user.organization_top.entity.OrgTop;
import com.iecube.iecubetutorial.model_user.organization_top.qo.OrgTopQo;
import com.iecube.iecubetutorial.model_user.points.entity.Points;
import com.iecube.iecubetutorial.model_user.points.vo.ConsumePointVo;
import com.iecube.iecubetutorial.model_user.points.vo.YearMonthConsumptionResponse;

import java.util.List;

public interface OperatorService {
    /**
     * 查看系统ADMIN角色列表
     * @return ADMIN角色列表
     */
    List<AUser> getAdminUsers();

    /**
     *  获取一级组织列表
     * @return List<OrgTop> 一级组织列表
     */
    List<OrgTop> getOrgTops();

    /**
     * 更新一级组织
     * @param orgTop 一级组织实体类
     * @return orgTop 一级组织实体类
     */
    OrgTop updateOrgTop(OrgTop orgTop);

    /**
     * 删除一级组织
     * @param orgTopId 一级组织的Id
     * @return List<OrgTop> 一级组织列表
     */
    List<OrgTop> removeOrgTop(Long orgTopId);

    /**
     * 根据一级组织获取二级组织列表
     * @param orgTopId 一级组织的Id
     * @return List<OrgSec> 二级组织列表
     */
    List<OrgSec> getOrgSecs(Long orgTopId);

    /**
     * 获取所有组织的组织树
     * @return List<OrganizationVo>
     */
    List<OrganizationVo> getAllOrganizations();

    /**
     * 根据二级组织Id获取其有效积分
     * @param oSecId 级组织Id
     * @return Points 有效积分对象
     */
    Points getPointsValid(Long oSecId);

    /**
     * 根据二级组织Id获取其消费的所有积分
     * @param oSecId 级组织Id
     * @return ConsumePointVo 消费的积分对象 总计消费额 消费明细
     */
    ConsumePointVo getConsumePoint(Long oSecId);


    /**
     * 根据二级组织获取账单
     * @param oSecId 二级组织Id
     * @return 按年月分组的账单
     */
    YearMonthConsumptionResponse getOrgSecBill(Long oSecId);

    /**
     * 创建一级组织
     * @param orgTopQo 创建一级组织
     * @return List<OrganizationVo> 所有组织的组织树
     */
    List<OrganizationVo> createOrganizationTop(OrgTopQo orgTopQo);

    /**
     * 创建二级组织
     * @param orgSecQo orgSecQo
     * @return 提审表单
     */
    Approval createOrganizationSec(OrgSecQo orgSecQo);

    /**
     * 向二级组织添加人员
     * @param addUUserQo addUUserQo
     * @return 提审表单
     */
    Approval addUsersToOrgSec(AddUUserQo addUUserQo);

    /**
     * 查看二级组织下的账户
     * @param oSecId 二级组织id
     * @return 账户列表
     */
    List<AccountVo> getOSecAccountVos(Long oSecId);

    /**
     * 向二级组织充值积分
     * @param rechargeQo rechargeQo
     * @return 提审表单
     */
    Approval recharge(RechargeQo rechargeQo);

    /**
     * 计算**RMB可以兑换多少积分
     * @param rmb 金额
     * @return 可兑换的积分
     */
    double computePoints(double rmb);

    /**
     * 定价变更
     * @param priceChangeQo 新定价
     * @return 提审表单
     */
    Approval changePriceQo(PriceChangeQo priceChangeQo);
}
