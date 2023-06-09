package com.dxvalley.nedajpaymnetbackend.otpservices.controller;
import com.dxvalley.nedajpaymnetbackend.otpservices.exception.OtpCustomeException;
import com.dxvalley.nedajpaymnetbackend.otpservices.payload.SendingOtpRequest;
import com.dxvalley.nedajpaymnetbackend.otpservices.services.SendingOtpServices;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/payment/services/v1")
public class SendOtpController {
    @Autowired
    private SendingOtpServices otpServices;
    @PostMapping(value = "/sendOtp")
    public ResponseEntity<?> sendingOtpToUser(@RequestBody SendingOtpRequest otpRequest){
        try {
            String resultOtpSending =otpServices.registerAndSendOTp(otpRequest);

            JSONObject resultObject = new JSONObject(resultOtpSending);
            String respDesc = resultObject.getString("Response");
            String status = resultObject.getString("status");
            String respCode = resultObject.getString("responseCode");
            //Filter to show in response
            JSONObject response = new JSONObject();
            response.put("Response", respDesc);
            response.put("status", status);
            response.put("responseCode", respCode);
            response.put("Message", "OTP sent successfully done");
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(response.toString());
        }catch (OtpCustomeException pe) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("Message", pe.getMessage());
            return ResponseEntity.status(pe.getStatus())
                    .header("Content-Type", "application/json")
                    .body(errorResponse.toString());
        }
    }
}
