package com.dxvalley.nedajpaymnetbackend.otpservices.controller;

import com.dxvalley.nedajpaymnetbackend.otpservices.payload.ConfirmationOtpRequest;
import com.dxvalley.nedajpaymnetbackend.otpservices.services.ConfirmationOtpServices;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment/services/v1")
public class ConfirmationOtpController {
    @Autowired
    private final ConfirmationOtpServices confirmationOtpServices;

    public ConfirmationOtpController(ConfirmationOtpServices confirmationOtpServices) {
        this.confirmationOtpServices = confirmationOtpServices;
    }
    @PostMapping(value = "/otpVerification")
    public ResponseEntity<?> confirmOtp(@RequestBody ConfirmationOtpRequest confirmRequest){
        try {
            String resultOtpConfirmation =confirmationOtpServices.confirmationOtp(confirmRequest);
            JSONObject resultObject = new JSONObject(resultOtpConfirmation);
            String phone = resultObject.getString("mobile");
            String otpNumber = resultObject.getString("otpNumber");
            String status = resultObject.getString("status");
            JSONObject response = new JSONObject();
            response.put("mobile", phone);
            response.put("otpNumber", otpNumber);
            response.put("status", status);
//            String respDesc = resultObject.getString("otpNumber");
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(response.toString());


        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Content-Type", "application/json")
                    .body(e.getMessage());
        }
    }
}
