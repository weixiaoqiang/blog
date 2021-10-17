package com.mszlu.blog.service;

import com.mszlu.blog.dao.pojo.SysUser;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.params.LoginParam;

import javax.servlet.http.HttpSession;

public interface LoginService {


     Result login(LoginParam loginParam);

    SysUser checkToken(String token);

    Result logout(String token);

    Result saveRegister(LoginParam loginParam);
}
