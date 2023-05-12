package com.dxvalley.nedajpaymnetbackend.payment.nedaj;

import jakarta.validation.Valid;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/payment/services/v1/nedaj")
public class NedajPaymentController {
    @Autowired
    private NedajCoopasPaymentService paymentService;
    private static final Logger logger = LoggerFactory.getLogger(NedajPaymentRequest.class);

    @PostMapping
    public ResponseEntity<?> processPayment(@Valid @RequestBody NedajPaymentRequest payment) {
        try {
           String result =paymentService.processPayment(payment);
            JSONObject resultObject = new JSONObject(result);
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

            response.put("Message", "Successfully paid");
//            response.put("Result", new JSONObject(result));=>this one is to display all of it.
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header("Content-Type", "application/json")
                    .body(response.toString());
        } catch (NedajCustomException pe) {
            logger.error(pe.getMessage());
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("Message", pe.getMessage());
            return ResponseEntity.status(pe.getStatus())
                    .header("Content-Type", "application/json")
                    .body(errorResponse.toString());
        }
    }
}
