package com.iecube.iecubetutorial.model_user.user.service.impl;

import com.iecube.iecubetutorial.exception.InsertException;
import com.iecube.iecubetutorial.model_user.user.entity.UUser;
import com.iecube.iecubetutorial.model_user.user.mapper.UUserMapper;
import com.iecube.iecubetutorial.model_user.user.service.UUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UUserServiceImpl implements UUserService {

    @Autowired
    private UUserMapper uUserMapper;

    @Override
    public UUser createUser(UUser user) {
        int res = uUserMapper.createUUser(user);
        if(res!=1){
            throw new InsertException("新增数据异常");
        }
        return user;

    }

    @Override
    public List<UUser> batchCreateUsers(List<UUser> users) {
        // 检查已经存在的情况
        // todo 优化 替换批量查询手机号
        List<UUser> exitsUsers = new ArrayList<UUser>();
        List<UUser> needCreateUsers = new ArrayList<>();
        users.forEach(user -> {
            UUser exitsUser = uUserMapper.getUUserByPhone(user.getPhone());
            if(exitsUser==null){
                needCreateUsers.add(user);
            }else {
                exitsUsers.add(exitsUser);
            }
        });

        if(!needCreateUsers.isEmpty()){
            int res = uUserMapper.createUUserBatch(needCreateUsers);
            if(res!=needCreateUsers.size()){
                throw new InsertException("新增数据异常");
            }
            needCreateUsers.forEach(user->{
                exitsUsers.add(uUserMapper.getUUserByPhone(user.getPhone()));
            });
        }
        return exitsUsers;
    }

    @Override
    public UUser getUserByPhone(String phone) {
        return uUserMapper.getUUserByPhone(phone);
    }
}
