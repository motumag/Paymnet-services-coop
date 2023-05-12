package com.dxvalley.nedajpaymnetbackend.userInfo;

import org.springframework.stereotype.Service;

@Service
public interface UserInfoService {
    String userInfo(UserInfoRequest userInfo);
}
