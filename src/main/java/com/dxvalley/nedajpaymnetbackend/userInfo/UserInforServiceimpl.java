package com.dxvalley.nedajpaymnetbackend.userInfo;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
@Service
public class UserInforServiceimpl implements UserInfoService{
    public String userInfo(UserInfoRequest userInfoModel){
        try{
            RestTemplate restTemplate = new RestTemplate();
            String uri = "http://10.1.245.150:7081/userInfo";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<UserInfoRequest> httpEntity = new HttpEntity<>(userInfoModel,headers);
            ResponseEntity<String> newPostEntity = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);

            String response = newPostEntity.getBody();
            return response;
        }catch (Exception e){
            return e.getMessage();
        }
    }
}
