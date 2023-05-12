package com.dxvalley.nedajpaymnetbackend.payment.crowdfund.services;

import com.dxvalley.nedajpaymnetbackend.payment.crowdfund.exception.CrowdFundCustomException;
import com.dxvalley.nedajpaymnetbackend.payment.crowdfund.models.CrowdFundModel;
import com.dxvalley.nedajpaymnetbackend.payment.crowdfund.payloads.CrowdFundRequest;
import com.dxvalley.nedajpaymnetbackend.payment.crowdfund.repository.CrowdFunRepository;
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
public class CrowdFundPaymentServices {

    @Autowired
    private CrowdFunRepository paymentRepo;
    private static final Logger logger = LoggerFactory.getLogger(CrowdFunRepository.class);

    public String processPayment(CrowdFundRequest payment) throws CrowdFundCustomException {
        try {
            validatePayment(payment);
            return checkDuplicateTransaction(payment);
            //Register here the request part as a pending status, then change to complete after we get success from app connect
//            defaultCreatePendingStatus(payment);
        } catch (CrowdFundCustomException pe) {
            // rollback transaction and throw exception
            logger.error("Payment exception: {}", pe.getMessage());
            throw new CrowdFundCustomException(pe.getStatus(), pe.getMessage());
        } catch (Exception e) {
            // rollback transaction and throw exception
            logger.error("Payment exception: {}", e.getMessage(), payment);
            throw new CrowdFundCustomException(500, e.getMessage());
        }
    }

    private void validatePayment(CrowdFundRequest payment) throws CrowdFundCustomException {
        // perform validation checks
        if (payment.getDebitAmount().compareTo(String.valueOf(BigDecimal.ZERO)) <= 0) {
            logger.info("Payment amount should be greater than zero");
            throw new CrowdFundCustomException(500, "Payment amount should be greater than zero");

        }
        // check if credit and debit accounts are not the same
        if (payment.getDebitAccount().equals(payment.getCreditAccount())) {
            logger.info("Credit and debit accounts cannot be the same");
            throw new CrowdFundCustomException(500, "Credit and debit accounts cannot be the same");
        }
    }

    // first check if transaction is already exist
    private String checkDuplicateTransaction(CrowdFundRequest payment) throws CrowdFundCustomException {
        CrowdFundModel transactions = paymentRepo.findByMessageId(payment.getMessageId());
        if (transactions != null) {
            String statusCheck = transactions.getSTATUS();
            String failedDate = transactions.getTRANSACTION_DATE();
            String errorType = transactions.getErrorType();
            if (statusCheck.equals("Success")) {
                logger.info("Payment has been done with this OrderId");
                throw new CrowdFundCustomException(409, "Payment has been done with this OrderId");
            } else if (statusCheck.equals("Pending")) {
                fundtransferAppconnect(payment);
            } else if (statusCheck.equals("Failure")) {
                throw new CrowdFundCustomException(409, "Failed because of: " + errorType + " " + "On Date of" + " " + failedDate);
            }
        } else {
            CrowdFundModel paymentModel = new CrowdFundModel();
            paymentModel.setMessageId(payment.getMessageId());
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

    private void addAmount(CrowdFundRequest payment) throws CrowdFundCustomException {
        // check first if the record[if exist do not write it again]
    }

    private void failureStatusUpdate(String messageId, String status, String errorType, String responseCode) throws CrowdFundCustomException {
        CrowdFundModel checkFirstToUpdate = paymentRepo.findByMessageId(messageId);

        if (checkFirstToUpdate == null) {
            throw new CrowdFundCustomException(404, "No transaction with this message ID");
        } else {
            checkFirstToUpdate.setSTATUS(status);
            checkFirstToUpdate.setErrorType(errorType);
            checkFirstToUpdate.setResponseCode(responseCode);
            paymentRepo.save(checkFirstToUpdate);
        }

    }
    public void updateSuccessTransaction(String messageId, String status, String transactionId, String transactionType,
                                         String processingDate, String transactionDate, String responseCode) throws CrowdFundCustomException {
        CrowdFundModel updateAfterPayment = paymentRepo.findByMessageId(messageId);
        if (updateAfterPayment == null) {
            throw new CrowdFundCustomException(404, "No transaction with this message ID");
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

    private String fundtransferAppconnect(CrowdFundRequest payment) throws CrowdFundCustomException {
        ResponseEntity<String> res = null;
        try {
            String uri = "http://10.1.245.151:7080/v3/ft/";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<CrowdFundRequest> request = new HttpEntity<CrowdFundRequest>(payment, headers);
            System.out.println("Request part:" + request);
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
            } else {
                JSONObject failureCheck = new JSONObject(res.getBody());
                String responseCode = failureCheck.getString("responseCode");
                String errorType = failureCheck.getString("errorType");
                logger.info(errorType);
                failureStatusUpdate(payment.getMessageId(), updateStatusConfirm, errorType, responseCode);
                throw new CrowdFundCustomException(400, errorType);
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new CrowdFundCustomException(e.getStatusCode().value(),
                        "The document you requested can not be found on this server");
            }
        }
        JSONObject responseAfterPayment = new JSONObject(res.getBody());
        return responseAfterPayment.toString();

    }
}
