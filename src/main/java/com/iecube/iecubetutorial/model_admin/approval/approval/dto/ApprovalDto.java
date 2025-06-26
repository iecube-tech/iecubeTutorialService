package com.iecube.iecubetutorial.model_admin.approval.approval.dto;

import com.iecube.iecubetutorial.model_admin.operator.qo.AddUUserQo;
import com.iecube.iecubetutorial.model_admin.operator.qo.OrgSecQo;
import com.iecube.iecubetutorial.model_admin.operator.qo.RechargeQo;
import com.iecube.iecubetutorial.model_admin.operator.qo.UUserQo;
import com.iecube.iecubetutorial.model_admin.price.qo.PriceChangeQo;
import com.iecube.iecubetutorial.model_admin.user.entity.AUser;
import com.iecube.iecubetutorial.model_user.organization_sec.entity.OrgSec;
import com.iecube.iecubetutorial.model_user.organization_top.entity.OrgTop;
import lombok.Data;

import java.util.List;

@Data
public class ApprovalDto {
    private AUser creator;
    private AUser approver;
    private OrgTop orgTop;
    private OrgSec orgSec;
    private OrgSecQo orgSecQo;
    private AddUUserQo addUUserQo;
    private RechargeQo rechargeQo;
    private PriceChangeQo priceChangeQo;
}
