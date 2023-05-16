package com.dxvalley.nedajpaymnetbackend.userInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping(value = "/payment/services/v1/userinfo")
public class UserinfoController {
    private final UserInfoService userInfoService;

    @Autowired
    public UserinfoController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @PostMapping
    public ResponseEntity<String> userInfo(@RequestBody UserInfoRequest userInfoModel) {
        try {
            String res = userInfoService.userInfo(userInfoModel);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header("Content-Type", "application/json")
                    .body(res);
        } catch (Exception e) {
            throw e;
        }
    }
}
