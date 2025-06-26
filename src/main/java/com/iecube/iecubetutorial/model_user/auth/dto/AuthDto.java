package com.iecube.iecubetutorial.model_user.auth.dto;

import com.iecube.iecubetutorial.model_user.account.entity.Account;
import com.iecube.iecubetutorial.model_user.organization_sec.entity.OrgSec;
import com.iecube.iecubetutorial.model_user.user.entity.UUser;
import lombok.Data;

import java.util.List;

@Data
public class AuthDto {
    private boolean login;
    private List<OrgSec> orgSecList;
    private UUser user;
    private OrgSec orgSec;
    private Account account;
    private String accessToken;
    private String refreshToken;
}
