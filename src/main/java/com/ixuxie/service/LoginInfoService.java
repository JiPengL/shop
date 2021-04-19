package com.ixuxie.service;


import com.ixuxie.dto.LoginInfoDto;

public interface LoginInfoService {

    public LoginInfoDto findById(String id);

    public LoginInfoDto insert(LoginInfoDto loginInfoDto);

    public LoginInfoDto login(Long uid, String loginName, String loginToken);

}
