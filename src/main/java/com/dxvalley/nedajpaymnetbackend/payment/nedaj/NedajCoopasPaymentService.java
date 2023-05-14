package com.dxvalley.nedajpaymnetbackend.payment.nedaj;

import com.dxvalley.nedajpaymnetbackend.otpservices.exception.OtpCustomeException;
import com.dxvalley.nedajpaymnetbackend.otpservices.models.OtpSendModel;
import com.dxvalley.nedajpaymnetbackend.otpservices.payload.ConfirmationOtpRequest;
import com.dxvalley.nedajpaymnetbackend.otpservices.repo.OtpRepository;
import com.dxvalley.nedajpaymnetbackend.otpservices.services.ConfirmationOtpServices;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
public class NedajCoopasPaymentService {
    @Autowired
    private NedajPaymentRepository paymentRepo;
    @Autowired
    private OtpRepository otpRepository;
    private static final Logger logger = LoggerFactory.getLogger(NedajPaymentRepository.class);

    public String processPayment(NedajPaymentRequest payment) throws NedajCustomException {
        try {
            validatePayment(payment);
            confirmationOtpNedajPayment(payment);
            return checkDuplicateTransaction(payment);
        } catch (NedajCustomException pe) {
            // rollback transaction and throw exception
            logger.error("Payment exception: {}", pe.getMessage());
            throw new NedajCustomException(pe.getStatus(), pe.getMessage());
        } catch (Exception e) {
            // rollback transaction and throw exception
            throw new NedajCustomException(500, e.getMessage());
        }
    }
    public String confirmationOtpNedajPayment(NedajPaymentRequest request)throws OtpCustomeException{
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

    private void validatePayment(NedajPaymentRequest payment) throws NedajCustomException {
        // perform validation checks
        if (payment.getDebitAmount().compareTo(String.valueOf(BigDecimal.ZERO)) <= 0) {
            logger.info("Payment amount should be greater than zero");
            throw new NedajCustomException(500, "Payment amount should be greater than zero");

        }
        // check if credit and debit accounts are not the same
        if (payment.getDebitAccount().equals(payment.getCreditAccount())) {
            logger.info("Credit and debit accounts cannot be the same");
            throw new NedajCustomException(500, "Credit and debit accounts cannot be the same");
        }
    }

    // first check if transaction is already exist
    private String checkDuplicateTransaction(NedajPaymentRequest payment) throws NedajCustomException {
        NedajPaymentModel transactions = paymentRepo.findByMessageId(payment.getMessageId());
        if (transactions != null) {
            String statusCheck = transactions.getSTATUS();
            String failedDate = transactions.getTRANSACTION_DATE();
            String errorType = transactions.getErrorType();
            if (statusCheck.equals("Success")) {
                logger.info("Payment has been done with this OrderId");
                throw new NedajCustomException(409, "Payment has been done with this OrderId");
            } else if (statusCheck.equals("Pending")) {
               return fundtransferAppconnect(payment);
            } else if (statusCheck.equals("Failure")) {
                throw new NedajCustomException(409, "Failed because of: " + errorType + " " + "On Date of" + " " + failedDate);
            }
        } else {
            NedajPaymentModel paymentModel = new NedajPaymentModel();
            paymentModel.setMessageId(payment.getMessageId());
            paymentModel.setMerchantId(payment.getMerchantId());
            paymentModel.setAgentId(payment.getAgentId());
            paymentModel.setFuelType(payment.getFuelType());
            paymentModel.setDEBITACCTNO(payment.getDebitAccount());
            paymentModel.setCREDITCURRENCY(payment.getDebitCurrency());
            paymentModel.setDEBITAMOUNT(payment.getDebitAmount());
            paymentModel.setCREDITTHEIRREF(payment.getCreditNarrative());
            paymentModel.setSTATUS("Pending");
            paymentModel.setCREDITACCTNO(payment.getCreditAccount());
            paymentRepo.save(paymentModel);
            return fundtransferAppconnect(payment);
        }
        return null;
    }

    private void failureStatusUpdate(String messageId, String status, String errorType, String responseCode) throws NedajCustomException {
        NedajPaymentModel checkFirstToUpdate = paymentRepo.findByMessageId(messageId);

        if (checkFirstToUpdate == null) {
            throw new NedajCustomException(404, "No transaction with this message ID");
        } else {
            checkFirstToUpdate.setSTATUS(status);
            checkFirstToUpdate.setErrorType(errorType);
            checkFirstToUpdate.setResponseCode(responseCode);
            paymentRepo.save(checkFirstToUpdate);
        }

    }

    public void updateSuccessTransaction(String messageId,
                                         String status,
                                         String transactionId,
                                         String transactionType,
                                         String processingDate,
                                         String transactionDate,
                                         String responseCode) throws NedajCustomException {
        NedajPaymentModel updateAfterPayment = paymentRepo.findByMessageId(messageId);
        if (updateAfterPayment == null) {
            throw new NedajCustomException(404, "No transaction with this message ID");
        } else {
            updateAfterPayment.setSTATUS(status);
            updateAfterPayment.setTransactionID(transactionId);
            updateAfterPayment.setTRANSACTIONTYPE(transactionType);
            updateAfterPayment.setPROCESSINGDATE(processingDate);
            updateAfterPayment.setTRANSACTION_DATE(transactionDate);
            updateAfterPayment.setResponseCode(responseCode);
            paymentRepo.save(updateAfterPayment);
        }

    }

    private String fundtransferAppconnect(NedajPaymentRequest payment) throws NedajCustomException {
        ResponseEntity<String> res = null;
        try {
            String uri = "http://10.1.245.151:7080/v3/ft/";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<NedajPaymentRequest> request = new HttpEntity<NedajPaymentRequest>(payment, headers);
            res = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);
            System.out.println("Actual response is: " + res);
            JSONObject checkStatus = new JSONObject(res.getBody());
            String updateStatusConfirm = checkStatus.getString("STATUS");
            if (res.getStatusCode() == HttpStatus.OK && updateStatusConfirm.equals("Success")) {
                JSONObject jsonObject = new JSONObject(res.getBody());
                String transactionId = jsonObject.getString("transactionID");
                String transactionType = jsonObject.getString("TRANSACTIONTYPE");
                String processingDate = jsonObject.getString("PROCESSINGDATE");
                String transactionDate = jsonObject.getString("TRANSACTION_DATE");
                String status = jsonObject.getString("STATUS");
                String responseCode = "200";
                updateSuccessTransaction(payment.getMessageId(), status, transactionId, transactionType, processingDate, transactionDate, responseCode);
//            return transactionId;
            } else {
                JSONObject failureCheck = new JSONObject(res.getBody());
                String responseCode = failureCheck.getString("responseCode");
                String errorType = failureCheck.getString("errorType");
                logger.info(errorType);
                failureStatusUpdate(payment.getMessageId(), updateStatusConfirm, errorType, responseCode);
                throw new NedajCustomException(400, errorType);
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NedajCustomException(e.getStatusCode().value(),
                        "The document you requested can not be found on this server");
            }
        }
        JSONObject responseAfterPayment = new JSONObject(res.getBody());
        JSONObject filteredResponseToShow = new JSONObject();
        String txnType = responseAfterPayment.getString("TRANSACTIONTYPE");
        String debitAcc = responseAfterPayment.getString("DEBITACCTNO");
        String amount = responseAfterPayment.getString("DEBITAMOUNT");
        String txnDate = responseAfterPayment.getString("TRANSACTION_DATE");
        String txnId = responseAfterPayment.getString("transactionID");
        //Filter to show in response
        filteredResponseToShow.put("TRANSACTIONTYPE", txnType);
        filteredResponseToShow.put("DEBITACCTNO", debitAcc);
        filteredResponseToShow.put("DEBITAMOUNT", amount);
        filteredResponseToShow.put("TRANSACTION_DATE", txnDate);
        filteredResponseToShow.put("transactionID", txnId);
        System.out.println("Response of appconnect" + filteredResponseToShow);
        return responseAfterPayment.toString();
    }
}