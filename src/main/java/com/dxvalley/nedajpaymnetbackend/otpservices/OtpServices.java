package com.dxvalley.nedajpaymnetbackend.otpservices;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.Random;
import java.util.UUID;

@Service
public class OtpServices {
    private final OtpRepository repository;

    public OtpServices(OtpRepository repository) {
        this.repository = repository;
    }

    public String registerAndSendOTp(@RequestBody OtpRequest otpReq) throws OtpCustomeException {
        try {
            String generatedOtpNumber = generateAndRegisterOtp();
            OtpSendModel isOtpExist = repository.findByOtpNumber(generatedOtpNumber);
            ResponseEntity<String> res = null;
            if (isOtpExist != null) {
                String statusCheck = isOtpExist.getStatus();
                String failedDate = isOtpExist.getMobile();
                if (statusCheck.equals("Success")) {
                    throw new OtpCustomeException(409, "Invalid OTP");
                } else if (statusCheck.equals("Failure")) {
                    throw new OtpCustomeException(409, "Failed because of: " + "On Date of" + " " + failedDate);
                } else {
                    throw new OtpCustomeException(409, "Unknown error");
                }

            } else {
                String uri = "http://10.1.230.6:7081/v1/otp/";
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                otpReq.setText(generatedOtpNumber);
                HttpEntity<OtpRequest> request = new HttpEntity<OtpRequest>(otpReq, headers);
                System.out.println("Request part:" + request);
                res = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);
                System.out.println("Actual response is: " + res);
                JSONObject checkStatus = new JSONObject(res.getBody());
                String updateStatusConfirm = checkStatus.getString("status");
                OtpSendModel newRegister=new OtpSendModel();
                if (res.getStatusCode() == HttpStatus.OK && updateStatusConfirm.equals("Success")) {
                    newRegister.setOtpNumber(generatedOtpNumber);
                    newRegister.setStatus("Pending");
                    newRegister.setResponseCode("200");
                    newRegister.setMobile(otpReq.getMobile());
                    repository.save(newRegister);
                }else {
                    newRegister.setOtpNumber(generatedOtpNumber);
                    newRegister.setStatus("Failure");
                    newRegister.setResponseCode("400");
                    newRegister.setMobile(otpReq.getMobile());
                    repository.save(newRegister);
                    throw new OtpCustomeException(400,"Failure T24");
                }
            }
            JSONObject responseAfterSendingOtp = new JSONObject(res.getBody());
            System.out.println("The final response is:"+responseAfterSendingOtp);
            return responseAfterSendingOtp.toString();

        } catch (Exception e) {
            throw new OtpCustomeException(500, e.getMessage());
        }
    }

    public String generateAndRegisterOtp() {
//        HashSet<String> uuidSet = new HashSet<>();
//        while (true) {
//            String uuid = UUID.randomUUID().toString().substring(0, 6);
//            if (!uuidSet.contains(uuid)) {
//                uuidSet.add(uuid);
//                System.out.println("Generated otp is:" + uuid);
//            }
//        }
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format("%06d", number);
    }

}
