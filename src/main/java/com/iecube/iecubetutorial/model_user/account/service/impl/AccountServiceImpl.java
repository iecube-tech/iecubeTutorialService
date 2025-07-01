package com.iecube.iecubetutorial.model_user.account.service.impl;

import com.iecube.iecubetutorial.exception.InsertException;
import com.iecube.iecubetutorial.model_user.account.entity.Account;
import com.iecube.iecubetutorial.model_user.account.mapper.AccountMapper;
import com.iecube.iecubetutorial.model_user.account.service.AccountService;
import com.iecube.iecubetutorial.model_user.account.vo.AccountVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountMapper accountMapper;

    @Override
    public Account createAccount(Account account) {
        int res = accountMapper.createAccount(account);
        if(res!=0){
            throw new InsertException("新增数据异常");
        }
        return account;
    }

    @Override
    public void createAccountBatch(List<Account> accounts) {
        // 检查已经存在的情况
        // todo 替换批量查询
        List<Account> needCreateAccounts = new ArrayList<>();
        accounts.forEach(account -> {
            Account exitsAccount = accountMapper.getAccount(account.getPhone(), account.getOSecId());
            if(exitsAccount==null){
                needCreateAccounts.add(account);
            }
        });
        if(!needCreateAccounts.isEmpty()){
            int res = accountMapper.createAccountBatch(needCreateAccounts);
            if(res!=needCreateAccounts.size()){
                throw new InsertException("新增数据异常");
            }
        }
    }

    @Override
    public List<AccountVo> getAccountsByOSecId(Long oSecId) {
        return accountMapper.getAccountVosByOSecId(oSecId);
    }

    @Override
    public List<Account> getAccountListByUser(String phone) {
        return accountMapper.getAccountListByUser(phone);
    }

    @Override
    public Account getAccount(String phone, Long oSecId) {
        return accountMapper.getAccount(phone, oSecId);
    }

    @Override
    public Account getAccount(Long id) {
        return accountMapper.getById(id);
    }

    @Override
    public List<AccountVo> getOrgSecUserMByOrgSecId(Long oSecId) {
        return accountMapper.getOrgSecUserMByOrgSecId(oSecId);
    }
}
