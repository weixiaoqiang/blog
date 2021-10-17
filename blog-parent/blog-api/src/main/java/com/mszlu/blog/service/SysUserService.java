package com.mszlu.blog.service;

import com.mszlu.blog.dao.pojo.SysUser;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.UserVo;

public interface SysUserService {


    SysUser findUser(String account, String password);

    Result findUserToken(String token);

    SysUser findNickName(String account);

    UserVo findUserVoById(Long articleId);

}
