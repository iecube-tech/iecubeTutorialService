package com.iecube.iecubetutorial.model_admin.operator.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.iecubetutorial.config.ThreadLocalUtil;
import com.iecube.iecubetutorial.exception.DeleteException;
import com.iecube.iecubetutorial.exception.JsonException;
import com.iecube.iecubetutorial.exception.ServiceException;
import com.iecube.iecubetutorial.model_admin.approval.approval.dto.ApprovalDto;
import com.iecube.iecubetutorial.model_admin.approval.approval.entity.Approval;
import com.iecube.iecubetutorial.model_admin.approval.approval.service.ApprovalService;
import com.iecube.iecubetutorial.model_admin.operator.qo.AddUUserQo;
import com.iecube.iecubetutorial.model_admin.operator.qo.OrgSecQo;
import com.iecube.iecubetutorial.model_admin.operator.qo.RechargeQo;
import com.iecube.iecubetutorial.model_admin.operator.service.OperatorService;
import com.iecube.iecubetutorial.model_admin.operator.vo.OrgSecVO;
import com.iecube.iecubetutorial.model_admin.operator.vo.OrganizationVo;
import com.iecube.iecubetutorial.model_admin.price.qo.PriceChangeQo;
import com.iecube.iecubetutorial.model_admin.price.service.PriceUnitService;
import com.iecube.iecubetutorial.model_admin.user.entity.AUser;
import com.iecube.iecubetutorial.model_admin.user.service.AUserService;
import com.iecube.iecubetutorial.model_user.account.service.AccountService;
import com.iecube.iecubetutorial.model_user.account.vo.AccountVo;
import com.iecube.iecubetutorial.model_user.enmu.UserStatus;
import com.iecube.iecubetutorial.model_user.organization_sec.entity.OrgSec;
import com.iecube.iecubetutorial.model_user.organization_sec.service.OrgSecService;
import com.iecube.iecubetutorial.model_user.organization_top.entity.OrgTop;
import com.iecube.iecubetutorial.model_user.organization_top.qo.OrgTopQo;
import com.iecube.iecubetutorial.model_user.organization_top.service.OrgTopService;
import com.iecube.iecubetutorial.model_user.points.entity.Points;
import com.iecube.iecubetutorial.model_user.points.service.PointsService;
import com.iecube.iecubetutorial.model_user.points.vo.ConsumePointVo;
import com.iecube.iecubetutorial.model_user.points.vo.YearMonthConsumptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OperatorServiceImpl implements OperatorService {

    @Autowired
    private AUserService userService;

    @Autowired
    private OrgTopService orgTopService;  // 一级组织

    @Autowired
    private OrgSecService orgSecService;  // 二级组织

    @Autowired
    private ApprovalService approvalService; // 审批系统

    @Autowired
    private PointsService pointsService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PriceUnitService priceUnitService;


    @Override
    public List<AUser> getAdminUsers() {
        return userService.GetAdminUsers();
    }

    @Override
    public List<OrgTop> getOrgTops() {
        return orgTopService.getOrgTop();
    }

    @Override
    public OrgTop updateOrgTop(OrgTop orgTop) {
        return orgTopService.updateOrgTop(orgTop);
    }

    @Override
    public List<OrgTop> removeOrgTop(Long orgTopId) {
        List<OrgSec> orgSecList = this.getOrgSecs(orgTopId);
        if(orgSecList==null || orgSecList.isEmpty()){
            return orgTopService.deleteOrgTop(orgTopId);
        }else {
            throw new DeleteException("该组织的子组织不为空，无法删除");
        }
    }

    @Override
    public List<OrgSec> getOrgSecs(Long orgTopId) {
        return orgSecService.getOrgSecByTop(orgTopId);
    }

    @Override
    public List<OrganizationVo> getAllOrganizations() {
        List<OrgTop> orgTopList = orgTopService.getOrgTop();
        List<OrgSec> orgSecList = orgSecService.getOrgSecs();
        List<OrganizationVo> organizationVoList = new ArrayList<OrganizationVo>();
        orgTopList.forEach(
                orgTop -> {
                    // 获取list
                    List<OrgSecVO> orgSecVOList = new ArrayList<>();
                    if(orgSecList!=null && !orgSecList.isEmpty()) {
                        orgSecList.forEach(orgSec -> {
                            if(orgSec.getPId().equals(orgTop.getId())){
                                OrgSecVO orgSecVO = new OrgSecVO();
                                orgSecVO.setId(orgSec.getId());
                                orgSecVO.setName(orgSec.getName());
                                orgSecVO.setType(orgSec.getType());
                                orgSecVO.setStatus(orgSec.getStatus());
                                orgSecVO.setLimit(orgSec.getLimit());
                                orgSecVO.setRemoved(0);
                                orgSecVO.setCreateTime(orgSec.getCreateTime());
                                orgSecVO.setCreator(orgSec.getCreator());
                                orgSecVO.setLastOperateTime(orgSec.getLastOperateTime());
                                orgSecVO.setLastOperator(orgSec.getLastOperator());
                                orgSecVOList.add(orgSecVO);
//                                orgSecList.remove(orgSec);
                            }
                        });
                    }
                    // orgVO
                    OrganizationVo organizationVo = new OrganizationVo();
                    organizationVo.setId(orgTop.getId());
                    organizationVo.setName(orgTop.getName());
                    organizationVo.setType(orgTop.getType());
                    organizationVo.setStatus(orgTop.getStatus());
                    organizationVo.setOSecList(orgSecVOList);
                    organizationVoList.add(organizationVo);
                }
        );
        return organizationVoList;
    }

    @Override
    public Points getPointsValid(Long oSecId) {
        return pointsService.getPointsValid(oSecId);
    }

    @Override
    public ConsumePointVo getConsumePoint(Long oSecId) {
        return pointsService.getConsumePoint(oSecId);
    }

    @Override
    public YearMonthConsumptionResponse getOrgSecBill(Long oSecId) {
        return pointsService.getAllConsumptionsGroupedByYearMonth(oSecId);
    }

    @Override
    public List<OrganizationVo> createOrganizationTop(OrgTopQo orgTopQo) {
        OrgTop orgTop = new OrgTop();
        orgTop.setName(orgTopQo.getName());
        orgTop.setType(orgTopQo.getType());
        orgTop.setStatus(UserStatus.ENABLED.name());
        orgTop.setCreator(ThreadLocalUtil.getPhone());
        orgTop.setCreateTime(Instant.now());
        orgTop.setLastOperator(ThreadLocalUtil.getPhone());
        orgTop.setLastOperateTime(Instant.now());
        orgTopService.createOrgTop(orgTop);
        return getAllOrganizations();
    }

    @Override
    public Approval createOrganizationSec(OrgSecQo orgSecQo) {
        //创建审批流程
        AUser createUser = userService.getUserByPhone(ThreadLocalUtil.getPhone());
        AUser approver = userService.getUserByPhone(orgSecQo.getApprover());
        OrgTop orgTop = orgTopService.getById(orgSecQo.getOrgTop());
        ApprovalDto approvalDto = new ApprovalDto();
        approvalDto.setApprover(approver);
        approvalDto.setCreator(createUser);
        approvalDto.setOrgTop(orgTop);
        approvalDto.setOrgSecQo(orgSecQo);
        try{
            Approval approval = new Approval();
            approval.setApprovalType("ORG_SEC_ADD");
            approval.setApproverPhone(orgSecQo.getApprover());
            approval.setStatus("PENDING");
            approval.setContent(objectMapper.writeValueAsString(approvalDto));
            return approvalService.createApproval(approval);
        }catch (JsonProcessingException e) {
            log.error("orgSecQo序列化异常：{}", e.getMessage());
            throw new JsonException("序列化异常");
        }
    }

    @Override
    public Approval addUsersToOrgSec(AddUUserQo addUUserQo) {
        //创建审批流程
        AUser createUser = userService.getUserByPhone(ThreadLocalUtil.getPhone());
        AUser approver = userService.getUserByPhone(addUUserQo.getApprover());
        OrgSec orgSec = orgSecService.getById(addUUserQo.getOSecId());
        OrgTop orgTop = orgTopService.getById(orgSec.getPId());
        ApprovalDto approvalDto = new ApprovalDto();
        approvalDto.setApprover(approver);
        approvalDto.setCreator(createUser);
        approvalDto.setOrgTop(orgTop);
        approvalDto.setOrgSec(orgSec);
        approvalDto.setAddUUserQo(addUUserQo);
        try{
            Approval approval = new Approval();
            approval.setApprovalType("ACCOUNT_ADD");
            approval.setApproverPhone(addUUserQo.getApprover());
            approval.setStatus("PENDING");
            approval.setContent(objectMapper.writeValueAsString(approvalDto));
            return approvalService.createApproval(approval);
        }catch (JsonProcessingException e) {
            log.error("addUUserQo序列化异常：{}", e.getMessage());
            throw new JsonException("序列化异常");
        }
    }

    @Override
    public Approval recharge(RechargeQo rechargeQo) {
        //创建审批流程
        rechargeQo.setPointsComputed(this.computePoints(rechargeQo.getRmb()));
        AUser createUser = userService.getUserByPhone(ThreadLocalUtil.getPhone());
        AUser approver = userService.getUserByPhone(rechargeQo.getApprover());
        OrgSec orgSec = orgSecService.getById(rechargeQo.getOSecId());
        OrgTop orgTop = orgTopService.getById(orgSec.getPId());
        ApprovalDto approvalDto = new ApprovalDto();
        approvalDto.setApprover(approver);
        approvalDto.setCreator(createUser);
        approvalDto.setOrgTop(orgTop);
        approvalDto.setOrgSec(orgSec);
        approvalDto.setRechargeQo(rechargeQo);
        try{
            Approval approval = new Approval();
            approval.setApprovalType("RECHARGE");
            approval.setApproverPhone(rechargeQo.getApprover());
            approval.setStatus("PENDING");
            approval.setContent(objectMapper.writeValueAsString(approvalDto));
            return approvalService.createApproval(approval);
        }catch (JsonProcessingException e) {
            log.error("rechargeQo序列化异常：{}", e.getMessage());
            throw new JsonException("序列化异常");
        }
    }

    @Override
    public List<AccountVo> getOSecAccountVos(Long oSecId) {
        return accountService.getAccountsByOSecId(oSecId);
    }

    @Override
    public double computePoints(double rmb) {
        double rechargePriceUnit=priceUnitService.RechargePriceUnit();
        if( rechargePriceUnit==0 ){
            throw new ServiceException("充值定价为0，无法计算");
        }
        return rmb/rechargePriceUnit;
    }

    @Override
    public Approval changePriceQo(PriceChangeQo priceChangeQo) {
        AUser createUser = userService.getUserByPhone(ThreadLocalUtil.getPhone());
        AUser approver = userService.getUserByPhone(priceChangeQo.getApprover());
        ApprovalDto approvalDto = new ApprovalDto();
        approvalDto.setApprover(approver);
        approvalDto.setCreator(createUser);
        approvalDto.setPriceChangeQo(priceChangeQo);
        try{
            Approval approval = new Approval();
            approval.setApprovalType("PRICE_CHANGE");
            approval.setApproverPhone(priceChangeQo.getApprover());
            approval.setStatus("PENDING");
            approval.setContent(objectMapper.writeValueAsString(approvalDto));
            return approvalService.createApproval(approval);
        }catch (JsonProcessingException e) {
            log.error("orgSecQo序列化异常：{}", e.getMessage());
            throw new JsonException("序列化异常");
        }
    }
}
