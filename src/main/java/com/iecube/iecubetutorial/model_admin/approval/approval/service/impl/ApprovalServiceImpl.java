package com.iecube.iecubetutorial.model_admin.approval.approval.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.iecubetutorial.config.ThreadLocalUtil;
import com.iecube.iecubetutorial.exception.AuthException;
import com.iecube.iecubetutorial.exception.InsertException;
import com.iecube.iecubetutorial.exception.JsonException;
import com.iecube.iecubetutorial.exception.UpdateException;
import com.iecube.iecubetutorial.model_admin.approval.approval.dto.ApprovalDto;
import com.iecube.iecubetutorial.model_admin.approval.approval.entity.Approval;
import com.iecube.iecubetutorial.model_admin.approval.approval.mapper.ApprovalMapper;
import com.iecube.iecubetutorial.model_admin.approval.approval.service.ApprovalService;
import com.iecube.iecubetutorial.model_admin.approval.approvalLog.entity.ApprovalLog;
import com.iecube.iecubetutorial.model_admin.approval.approvalLog.service.ApprovalLogService;
import com.iecube.iecubetutorial.model_admin.approval.enmu.ApprovalLogAction;
import com.iecube.iecubetutorial.model_admin.operator.qo.AddUUserQo;
import com.iecube.iecubetutorial.model_admin.operator.qo.OrgSecQo;
import com.iecube.iecubetutorial.model_admin.operator.qo.RechargeQo;
import com.iecube.iecubetutorial.model_admin.operator.qo.UUserQo;
import com.iecube.iecubetutorial.model_admin.point.expire.service.impl.APointExpireServiceImpl;
import com.iecube.iecubetutorial.model_admin.price.entity.PriceUnit;
import com.iecube.iecubetutorial.model_admin.price.mapper.PriceUnitMapper;
import com.iecube.iecubetutorial.model_admin.price.qo.PriceChangeQo;
import com.iecube.iecubetutorial.model_user.account.entity.Account;
import com.iecube.iecubetutorial.model_user.account.service.AccountService;
import com.iecube.iecubetutorial.model_user.enmu.UserStatus;
import com.iecube.iecubetutorial.model_user.organization_sec.entity.OrgSec;
import com.iecube.iecubetutorial.model_user.organization_sec.service.OrgSecService;
import com.iecube.iecubetutorial.model_user.points.service.PointsService;
import com.iecube.iecubetutorial.model_user.user.entity.UUser;
import com.iecube.iecubetutorial.model_user.user.service.UUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ApprovalServiceImpl implements ApprovalService {

    @Autowired
    private ApprovalMapper approvalMapper;

    @Autowired
    private ApprovalLogService approvalLogService;

    @Autowired
    private OrgSecService orgSecService;

    @Autowired
    private UUserService uUserService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PointsService pointsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PriceUnitMapper priceUnitMapper;

    @Autowired
    private APointExpireServiceImpl pointExpireService;

    @Override
    public List<Approval> getByApprover() {
        List<Approval> approvals = approvalMapper.getByApprover(ThreadLocalUtil.getPhone());
        return replenishApprovalRes(approvals);
    }

    @Override
    public List<Approval> getByCreator() {
        List<Approval> approvals = approvalMapper.getByCreator(ThreadLocalUtil.getPhone());
        return replenishApprovalRes(approvals);
    }

    private List<Approval> replenishApprovalRes(List<Approval> approvals) {
        List<Long> idList = new ArrayList<>();
        approvals.forEach(approval -> idList.add(approval.getId()));
        if(approvals.isEmpty()){
            return approvals;
        }
        List<ApprovalLog> approvalLogs = approvalLogService.getApprovalLogsByApprovalLogIds(idList);
        for(Approval approval : approvals) {
            List<ApprovalLog> approvalLogList = new ArrayList<>();
            for(ApprovalLog approvalLog : approvalLogs) {
                if(approval.getId().equals(approvalLog.getApprovalId())){
                    approvalLogList.add(approvalLog);
                }
            }
            approval.setApprovalLogs(approvalLogList);
        }
        return approvals;
    }

    @Override
    @Transactional
    public Approval createApproval(Approval approval) {
        approval.setCreateTime(Instant.now());
        approval.setCreator(ThreadLocalUtil.getPhone());
        approval.setLastOperator(ThreadLocalUtil.getPhone());
        approval.setLastOperateTime(Instant.now());
        int res = approvalMapper.createApproval(approval);
        if(res!=1){
            throw new InsertException("新增数据异常");
        }
        approvalLogService.recordLog(approval, ThreadLocalUtil.getPhone(), ApprovalLogAction.SUBMIT.name(),"提交审批");
        return approval;
    }

    @Override
    @Transactional
    public Approval approval(Long id, String remark) {
        Approval approval = approvalMapper.getApproval(id);
        if(ApprovalStatusCanNotApprove(approval)){
            return null;
        }
        approval.setStatus("APPROVED");
        approval.setLastOperator(ThreadLocalUtil.getPhone());
        approval.setLastOperateTime(Instant.now());
        approvalMapper.updateApproval(approval);
        approvalLogService.recordLog(approval, ThreadLocalUtil.getPhone(), ApprovalLogAction.APPROVE.name(), remark);
        executeApprovedAction(approval);
        List<ApprovalLog> approvalLogs = approvalLogService.getApprovalLogsByApprovalLogId(approval.getId());
        approval.setApprovalLogs(approvalLogs);
        return approval;
    }

    @Override
    @Transactional
    public Approval reject(Long id, String remark) {
        Approval approval = approvalMapper.getApproval(id);
        if(ApprovalStatusCanNotApprove(approval)){
            return null;
        }
        approval.setStatus("REJECTED");
        approval.setLastOperator(ThreadLocalUtil.getPhone());
        approval.setLastOperateTime(Instant.now());
        approvalMapper.updateApproval(approval);
        approvalLogService.recordLog(approval, ThreadLocalUtil.getPhone(), ApprovalLogAction.REJECT.name(), remark);
        List<ApprovalLog> approvalLogs = approvalLogService.getApprovalLogsByApprovalLogId(approval.getId());
        approval.setApprovalLogs(approvalLogs);
        return approval;
    }

    @Override
    @Transactional
    public void executeApprovedAction(Approval approval) {
        try {
            switch (approval.getApprovalType()) {
                case "ORG_SEC_ADD":
                    // 二级组织创建
                    handleOrgSecAdd(approval);
                    break;
                case "ACCOUNT_ADD":
                    handleAddUsersToOSec(approval);
                    break;
                case "RECHARGE":
                    handleRechargeApproval(approval);
                    break;
                case "PRICE_CHANGE":
                    handlePricingApproval(approval);
                    break;
            }
        } catch (Exception e) {
            // 记录异常，但不影响事务提交
            log.error("审批环境出现异常",e);
        }
    }

    private void handleOrgSecAdd(Approval approval) {
        // 创建二级组织 创建用户 创建账户
        try {
            ApprovalDto approvalDto = objectMapper.readValue(approval.getContent(),ApprovalDto.class);
            OrgSecQo orgSecQo = approvalDto.getOrgSecQo();
            // 二级组织
            OrgSec orgSec = new OrgSec();
            orgSec.setName(orgSecQo.getName());
            orgSec.setType(orgSecQo.getType());
            orgSec.setPId(orgSecQo.getOrgTop());
            orgSec.setLimit(orgSecQo.getLimit());
            orgSec.setStatus(UserStatus.ENABLED.name());
            orgSec.setCreator(approval.getCreator());
            orgSec.setCreateTime(Instant.now());
            orgSec.setLastOperator(ThreadLocalUtil.getPhone());
            orgSec.setLastOperateTime(Instant.now());
            OrgSec orgSecRes = orgSecService.createOrgSec(orgSec);
            // 积分系统
            pointsService.createPoints(orgSecRes, orgSecQo.getGiftPoints(), approval.getCreator());
            // 用户&账户
            createUserAndAccount(orgSecQo.getUUserQoList(),orgSecRes.getId(),approval.getCreator());
        }catch (JsonProcessingException e){
            log.error("objectMapper.readValue Exception", e);
            throw new JsonException("json序列化异常");
        }
    }

    private void handleAddUsersToOSec(Approval approval){
        try{
            ApprovalDto approvalDto = objectMapper.readValue(approval.getContent(),ApprovalDto.class);
            AddUUserQo addUUserQo = approvalDto.getAddUUserQo();
            createUserAndAccount(addUUserQo.getUsers(), addUUserQo.getOSecId(),approval.getCreator());
        }catch (JsonProcessingException e){
            log.error("objectMapper.readValue Exception", e);
            throw new JsonException("json序列化异常");
        }
    }

    private void handleRechargeApproval(Approval approval) {
        try{
            ApprovalDto approvalDto = objectMapper.readValue(approval.getContent(),ApprovalDto.class);
            RechargeQo rechargeQo = approvalDto.getRechargeQo();
            pointsService.rechargePoints(approvalDto.getOrgSec(),rechargeQo.getPointsComputed(),approval.getCreator());
        }catch (JsonProcessingException e){
            log.error("objectMapper.readValue Exception", e);
            throw new JsonException("json序列化异常");
        }
    }

    private void handlePricingApproval(Approval approval) {
        try{
            ApprovalDto approvalDto = objectMapper.readValue(approval.getContent(),ApprovalDto.class);
            PriceChangeQo priceChangeQo = approvalDto.getPriceChangeQo();
            priceUnitMapper.disableAll();
            PriceUnit recharge = new PriceUnit();
            recharge.setType("RECHARGE");
            recharge.setTypeCn("充值");
            recharge.setTarget(1);
            recharge.setTargetUnits("point");
            recharge.setTargetUnitsCn("积分");
            recharge.setNeed(priceChangeQo.getHowRmbToOnePoint());
            recharge.setNeedUnits("RMB");
            recharge.setNeedUnitsCn("人民币");
            recharge.setActive(1);
            recharge.setRemoved(0);
            recharge.setCreateTime(Instant.now());
            recharge.setCreator(approval.getCreator());
            recharge.setLastOperateTime(Instant.now());
            recharge.setLastOperator(priceChangeQo.getApprover());
            priceUnitMapper.createPrice(recharge);
            PriceUnit consume = new PriceUnit();
            consume.setType("CONSUME");
            consume.setTypeCn("消费");
            consume.setTarget(1);
            consume.setTargetUnits("generate");
            consume.setTargetUnitsCn("生成");
            consume.setNeed(priceChangeQo.getHowPointsToOneGenerate());
            consume.setNeedUnits("point");
            consume.setNeedUnitsCn("积分");
            consume.setActive(1);
            consume.setRemoved(0);
            consume.setCreateTime(Instant.now());
            consume.setCreator(approval.getCreator());
            consume.setLastOperateTime(Instant.now());
            consume.setLastOperator(priceChangeQo.getApprover());
            priceUnitMapper.createPrice(consume);
            pointExpireService.changeExpireDays(priceChangeQo.getExpireDays(), approval.getCreator(),  priceChangeQo.getApprover());
        }catch (JsonProcessingException e){
            log.error("objectMapper.readValue Exception", e);
            throw new JsonException("json序列化异常");
        }
    }

    private boolean ApprovalStatusCanNotApprove(Approval approval) {
        if(approval==null){
            return true;
        }
        if(!Objects.equals(approval.getApproverPhone(), ThreadLocalUtil.getPhone())){
            throw new AuthException("审批人不符");
        }
        if(!"PENDING".equals(approval.getStatus())){
            throw new UpdateException("审批状态不符");
        }
        return false;
    }

    private void createUserAndAccount(List<UUserQo> uUserQoList, Long OSecId, String creator){
        // 用户
        List<UUser> uUserList = new ArrayList<>();
        List<UUser> uUserManagerList = new ArrayList<>();
        uUserQoList.forEach(uUserQo -> {
            UUser uUser = new UUser();
            uUser.setPhone(uUserQo.getPhone());
            uUser.setEmail(uUserQo.getEmail());
            uUser.setName(uUserQo.getName());
            uUser.setStatus(UserStatus.ENABLED.name());
            uUser.setRemoved(0);
            uUser.setCreator(creator);
            uUser.setCreateTime(Instant.now());
            uUser.setLastOperator(ThreadLocalUtil.getPhone());
            uUser.setLastOperateTime(Instant.now());
            if(uUserQo.getRole().equals("USER_M")){
                uUserManagerList.add(uUser);
            }else {
                uUserList.add(uUser);
            }
        });
        List<UUser> uUserListRes = uUserService.batchCreateUsers(uUserList);
        List<UUser> uUserManagerListRes = uUserService.batchCreateUsers(uUserManagerList);
        // 账户
        List<Account> accountList = new ArrayList<>();
        uUserListRes.forEach(uUser -> {
            Account account = new Account();
            account.setPhone(uUser.getPhone());
            account.setOSecId(OSecId);
            account.setRole("USER");
            account.setStatus(UserStatus.ENABLED.name());
            account.setCreator(creator);
            account.setCreateTime(Instant.now());
            account.setLastOperator(ThreadLocalUtil.getPhone());
            account.setLastOperateTime(Instant.now());
            accountList.add(account);
        });
        uUserManagerListRes.forEach(uUser -> {
            Account account = new Account();
            account.setPhone(uUser.getPhone());
            account.setOSecId(OSecId);
            account.setRole("USER_M");
            account.setStatus(UserStatus.ENABLED.name());
            account.setCreator(creator);
            account.setCreateTime(Instant.now());
            account.setLastOperator(ThreadLocalUtil.getPhone());
            account.setLastOperateTime(Instant.now());
            accountList.add(account);
        });
        accountService.createAccountBatch(accountList);
    }


}
