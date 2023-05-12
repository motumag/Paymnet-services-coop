package com.dxvalley.nedajpaymnetbackend.sendOtpAndConfirm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.Random;
@Service
@Configuration
class OtpServiceimpl implements OtpService {

  private final OtpRepository otpRepository;
  private    OtpService service;

  @Autowired
  public OtpServiceimpl(OtpRepository otpRepository) {
    this.otpRepository = otpRepository;
  }

  public String getRandomNumberString() {
    Random rnd = new Random();
    int number = rnd.nextInt(999999);
    return String.format("%06d", number);
  }

  public HttpEntity<createUserResponse> sendOtp(OtpModel otpModel){

    String res = null;
  String otps = getRandomNumberString();
try {

  RestTemplate restTemplate = new RestTemplate();
  String uri = "http://10.1.230.6:7081/v1/otp/";
  HttpHeaders headers = new HttpHeaders();
  otpModel.getMobile();
  headers.setContentType(MediaType.APPLICATION_JSON);
  HttpEntity<OtpModel> httpEntity = new HttpEntity<>(otpModel, headers);
  otpModel.setText(otps);
  System.out.println("postmodel req" + otpModel);
  System.out.println(HttpStatus.OK);

  ResponseEntity<String> newPostEntity = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);
  System.out.println("the status code is: " + newPostEntity.getStatusCode());
  if (newPostEntity.getStatusCode() == HttpStatus.OK && newPostEntity.getBody().contains("Success")) {
    otpRepository.save(otpModel);

    createUserResponse response = new createUserResponse("success",
      "Message sent to your phone number.");
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
  else {
    createUserResponse response = new createUserResponse("error",
      "Please inter valid Mobile number!");
    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  } catch (Exception e) {
    e.printStackTrace();
    createUserResponse response = new createUserResponse("error",
      "Please insert valid Mobile number!!");
    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }
  }
  @Override
  public HttpEntity<ApiResponse> confirmOtp(OtpModel otpModel) {

    Optional<OtpModel>  otpModel1 = otpRepository.findByTextAndMobile(otpModel.getText(),otpModel.getMobile());
    System.out.println(otpModel1);
    if(otpModel1.isPresent())
    {    ApiResponse response = new ApiResponse("success", "Otp Confirmed.");
      return new ResponseEntity<>(response, HttpStatus.OK);
    }
    ApiResponse response = new ApiResponse("error", "Cannot confirm the otp provided.");
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }
}
