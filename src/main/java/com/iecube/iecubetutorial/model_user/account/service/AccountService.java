package com.iecube.iecubetutorial.model_user.account.service;

import com.iecube.iecubetutorial.model_user.account.entity.Account;
import com.iecube.iecubetutorial.model_user.account.vo.AccountVo;

import java.util.List;

public interface AccountService {

    Account createAccount(Account account);

    void createAccountBatch(List<Account> accounts);

    List<AccountVo> getAccountsByOSecId(Long oSecId);

    List<Account> getAccountListByUser(String phone);

    Account getAccount(String phone, Long oSecId);

    Account getAccount(Long id);

    List<AccountVo> getOrgSecUserMByOrgSecId(Long oSecId);
}
