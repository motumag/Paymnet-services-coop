package com.dxvalley.nedajpaymnetbackend.sendOtpAndConfirm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*/")
@RestController
@RequestMapping(value = "/payment/services/v1/")
public class OptController {
private  final OtpRepository otpRepository;
  private  final  OtpService otpService;

  @Autowired
  public OptController(OtpRepository otpRepository, OtpService otpService) {
    this.otpRepository = otpRepository;
    this.otpService = otpService;
  }

  @PostMapping("sendOtp")
  public HttpEntity<createUserResponse> sendOtp(@RequestBody OtpModel otp){
    HttpEntity<createUserResponse> res = otpService.sendOtp(otp);
    return res;
  }
  @PostMapping("otpVerification")

 public HttpEntity<ApiResponse> confirmOtp(@RequestBody OtpModel otpModel)

  {
    {
   HttpEntity<ApiResponse> res = otpService.confirmOtp(otpModel);
    return res;
  }
  }
}
