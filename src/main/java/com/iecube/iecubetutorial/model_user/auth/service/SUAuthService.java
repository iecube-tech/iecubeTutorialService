package com.iecube.iecubetutorial.model_user.auth.service;

import com.iecube.iecubetutorial.model_user.account.vo.AccountVo;
import com.iecube.iecubetutorial.model_user.auth.dto.AuthDto;
import com.iecube.iecubetutorial.model_user.organization_sec.entity.OrgSec;
import com.iecube.iecubetutorial.model_user.user.entity.UUser;

import java.util.List;

public interface SUAuthService {

    void sendCode(String phone);

    AuthDto login(String phone, String code);

    AuthDto reLogin(Long orgSecId);

    List<OrgSec> accountOrgSecList();

    AuthDto refreshToken(String refreshToken);

    List<AccountVo> getUserListByAccount();
}
