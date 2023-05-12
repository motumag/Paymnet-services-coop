package com.dxvalley.nedajpaymnetbackend.sendOtpAndConfirm;


import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

@Service
public interface OtpService {

 public HttpEntity<createUserResponse> sendOtp(OtpModel otpModel);

  HttpEntity<ApiResponse> confirmOtp(OtpModel otpModel);
}
