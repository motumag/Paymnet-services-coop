package com.dxvalley.nedajpaymnetbackend.otpservices.services;

import com.dxvalley.nedajpaymnetbackend.otpservices.exception.OtpCustomeException;
import com.dxvalley.nedajpaymnetbackend.otpservices.models.OtpSendModel;
import com.dxvalley.nedajpaymnetbackend.otpservices.payload.ConfirmationOtpRequest;
import com.dxvalley.nedajpaymnetbackend.otpservices.repo.OtpRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class ConfirmationOtpServices {
    private final OtpRepository otpRepository;

    public ConfirmationOtpServices(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }
    public String confirmationOtp(ConfirmationOtpRequest request) throws OtpCustomeException {
        try {
            OtpSendModel otpSendModel=new OtpSendModel();
            otpSendModel=otpRepository.findByOtpNumber(request.getConfirmationOtpNumber());
            if (otpSendModel==null){
                throw new OtpCustomeException(401,"OTP is not found");
            }
            String otpNumber=otpSendModel.getOtpNumber();
            String mobileNumber=otpSendModel.getMobile();
            String status=otpSendModel.getStatus();
            if (!otpNumber.equals(request.getConfirmationOtpNumber())){
                throw new OtpCustomeException(403,"Otp Mismatch");
            }
            if (!mobileNumber.equals(request.getPhoneNumber())){
                throw new OtpCustomeException(403,"Phone Number Mismatch");
            }
            if (status.equals("Confirmed")){
                throw new OtpCustomeException(409,"The OTP you provided has been already used");
            }
            if (status.equals("Failure")){
                throw new OtpCustomeException(403,"Forbidden to use this OTP");
            }
            if (status.equals("Pending")){
                System.out.println("momo");
                otpSendModel.setStatus("Confirmed");
                otpRepository.save(otpSendModel);
            }
            System.out.println("error?: "+otpSendModel);
            JSONObject resp=new JSONObject(otpSendModel);
            return resp.toString();
        }catch (Exception e){
            throw new OtpCustomeException(400,e.getMessage());
        }
    }
}
