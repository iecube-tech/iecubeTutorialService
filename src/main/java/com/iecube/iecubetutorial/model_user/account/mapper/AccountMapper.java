package com.iecube.iecubetutorial.model_user.account.mapper;

import com.iecube.iecubetutorial.model_user.account.entity.Account;
import com.iecube.iecubetutorial.model_user.account.vo.AccountVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AccountMapper {
    int createAccount(Account account);

    int createAccountBatch(List<Account> list);

    Account getAccount(String phone, Long oSecId);

    List<AccountVo> getAccountVosByOSecId(Long oSecId);

    List<Account> getAccountListByUser(String phone);

    Account getById(Long id);

    List<AccountVo> getOrgSecUserMByOrgSecId(Long oSecId);
}
