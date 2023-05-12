package com.dxvalley.nedajpaymnetbackend.payment.equb;

import com.dxvalley.nedajpaymnetbackend.payment.crowdfund.exception.CrowdFundCustomException;
import com.dxvalley.nedajpaymnetbackend.payment.crowdfund.services.CrowdFundPaymentServices;
import com.dxvalley.nedajpaymnetbackend.payment.crowdfund.payloads.CrowdFundRequest;
import jakarta.validation.Valid;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment/services/v1/equb")
public class EqubPaymentController {
    @Autowired
    private CrowdFundPaymentServices paymentService;
    private static final Logger logger = LoggerFactory.getLogger(CrowdFundRequest.class);

    @PostMapping
    public ResponseEntity<?> processPayment(@Valid @RequestBody CrowdFundRequest payment) {
        try {
            String resultToCrowdFund =paymentService.processPayment(payment);
            JSONObject resultObject = new JSONObject(resultToCrowdFund);
            String txnId = resultObject.getString("transactionID");
            String txnType = resultObject.getString("TRANSACTIONTYPE");
            String debitAcc = resultObject.getString("DEBITACCTNO");
            String amount = resultObject.getString("DEBITAMOUNT");
            String txnDate = resultObject.getString("TRANSACTION_DATE");
            //Filter to show in response
            JSONObject response = new JSONObject();
            response.put("transactionID", txnId);
            response.put("TRANSACTIONTYPE", txnType);
            response.put("DEBITACCTNO", debitAcc);
            response.put("DEBITAMOUNT", amount);
            response.put("TRANSACTION_DATE", txnDate);
//            paymentService.processPayment(payment);
//            JSONObject response = new JSONObject();
            response.put("Message", "Payment successfully done");
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header("Content-Type", "application/json")
                    .body(response.toString());

        } catch (CrowdFundCustomException pe) {
            logger.error(pe.getMessage());
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("Message", pe.getMessage());
            return ResponseEntity.status(pe.getStatus())
                    .header("Content-Type", "application/json")
                    .body(errorResponse.toString());
//            return ResponseEntity.status(pe.getStatus()).body((pe.getMessage()));
        }
    }
}
