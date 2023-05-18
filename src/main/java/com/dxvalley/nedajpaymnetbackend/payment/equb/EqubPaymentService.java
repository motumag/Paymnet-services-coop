package com.dxvalley.nedajpaymnetbackend.payment.equb;
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
public class EqubPaymentService {

    @Autowired
    private EqubPaymentRepository paymentRepo;
    private static final Logger logger = LoggerFactory.getLogger(EqubPaymentRepository.class);

    public String processPayment(EqubPaymentRequest payment) throws EqubCustomException {
        try {
            validatePayment(payment);
            return checkDuplicateTransaction(payment);
        } catch (EqubCustomException pe) {
            // rollback transaction and throw exception
            logger.error("Payment exception: {}", pe.getMessage());
            throw new EqubCustomException(pe.getStatus(), pe.getMessage());
        } catch (Exception e) {
            // rollback transaction and throw exception
            logger.error("Payment exception: {}", e.getMessage(), payment);
            throw new EqubCustomException(500, e.getMessage());
        }
    }

    private void validatePayment(EqubPaymentRequest payment) throws EqubCustomException {
        // perform validation checks
        if (payment.getDebitAmount().compareTo(String.valueOf(BigDecimal.ZERO)) <= 0) {
            logger.info("Payment amount should be greater than zero");
            throw new EqubCustomException(500, "Payment amount should be greater than zero");

        }
        // check if credit and debit accounts are not the same
        if (payment.getDebitAccount().equals(payment.getCreditAccount())) {
            logger.info("Credit and debit accounts cannot be the same");
            throw new EqubCustomException(500, "Credit and debit accounts cannot be the same");
        }
        if (payment.getMessageId()==null) {
            logger.info("MessageId must have a value");
            throw new EqubCustomException(500, "MessageId must have a value");
        }
        if (payment.getDebitAccount()==null) {
            logger.info("Debit Account should not be null");
            throw new EqubCustomException(500, "Debit Account should not be null");
        }
        if (payment.getCreditAccount()==null) {
            logger.info("Credit Account should not be null");
            throw new EqubCustomException(500, "Credit Account should not be null");
        }
    }
    // first check if transaction is already exist
    private String checkDuplicateTransaction(EqubPaymentRequest payment) throws EqubCustomException {
        EqubPaymentModel transactions = paymentRepo.findByMessageId(payment.getMessageId());
        if (transactions != null) {
            String statusCheck = transactions.getSTATUS();
            String failedDate = transactions.getTRANSACTION_DATE();
            String errorType = transactions.getErrorType();
            if (statusCheck.equals("Success")) {
                logger.info("Payment has been done with this OrderId");
                throw new EqubCustomException(409, "Payment has been done with this OrderId");
            } else if (statusCheck.equals("Pending")) {
                fundtransferAppconnect(payment);
            } else if (statusCheck.equals("Failure")) {
                throw new EqubCustomException(409, "Failed because of: " + errorType + " " + "On Date of" + " " + failedDate);
            }
        } else {
            EqubPaymentModel paymentModel = new EqubPaymentModel();
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
    private void failureStatusUpdate(String messageId, String status, String errorType, String responseCode) throws EqubCustomException {
        EqubPaymentModel checkFirstToUpdate = paymentRepo.findByMessageId(messageId);

        if (checkFirstToUpdate == null) {
            throw new EqubCustomException(404, "No transaction with this message ID");
        } else {
            checkFirstToUpdate.setSTATUS(status);
            checkFirstToUpdate.setErrorType(errorType);
            checkFirstToUpdate.setResponseCode(responseCode);
            paymentRepo.save(checkFirstToUpdate);
        }

    }
    public void updateSuccessTransaction(String messageId, String status, String transactionId, String transactionType,
                                         String processingDate, String transactionDate, String responseCode) throws EqubCustomException {
        EqubPaymentModel updateAfterPayment = paymentRepo.findByMessageId(messageId);
        if (updateAfterPayment == null) {
            throw new EqubCustomException(404, "No transaction with this message ID");
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

    private String fundtransferAppconnect(EqubPaymentRequest payment) throws EqubCustomException {
        ResponseEntity<String> res = null;
        try {
            String uri = "http://10.1.245.151:7080/v3/ft/";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<EqubPaymentRequest> request = new HttpEntity<EqubPaymentRequest>(payment, headers);
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
                throw new EqubCustomException(400, errorType);
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new EqubCustomException(e.getStatusCode().value(),
                        "The document you requested can not be found on this server");
            }
        }
        JSONObject responseAfterPayment = new JSONObject(res.getBody());
        return responseAfterPayment.toString();

    }
}
