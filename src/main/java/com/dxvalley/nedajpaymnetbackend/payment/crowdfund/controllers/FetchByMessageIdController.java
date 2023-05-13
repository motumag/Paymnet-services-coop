package com.dxvalley.nedajpaymnetbackend.payment.crowdfund.controllers;

import com.dxvalley.nedajpaymnetbackend.payment.crowdfund.models.CrowdFundModel;
import com.dxvalley.nedajpaymnetbackend.payment.crowdfund.payloads.CrowdFundRequest;
import com.dxvalley.nedajpaymnetbackend.payment.crowdfund.repository.CrowdFunRepository;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/payment/services/v1/crowdfund")
public class FetchByMessageIdController {
    private final CrowdFunRepository repo;

    public FetchByMessageIdController(CrowdFunRepository repo) {
        this.repo = repo;
    }
   @GetMapping("/fetchByMessageId/{messageId}")
    public ResponseEntity<?> findPaymentByMessageId(@PathVariable String messageId) {
        try {
            CrowdFundModel detailResponse = repo.findByMessageId(messageId);
            if (detailResponse == null) {
                ErrorResponse resp = new ErrorResponse("No transacion with this messageId");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
            } else {
                System.out.println("Detail resp is: "+detailResponse);
                JSONObject resultObject = new JSONObject(detailResponse);
                String txnId = resultObject.getString("transactionID");
                String txnType = resultObject.getString("TRANSACTIONTYPE");
                String debitAcc = resultObject.getString("DEBITACCTNO");
                String credAcc = resultObject.getString("CREDITACCTNO");
                String amount = resultObject.getString("DEBITAMOUNT");
                String txnDate = resultObject.getString("TRANSACTION_DATE");
                String msgId = resultObject.getString("messageId");
                String narative = resultObject.getString("CREDITTHEIRREF");

                JSONObject response = new JSONObject();
                response.put("transactionID", txnId);
                response.put("TRANSACTIONTYPE", txnType);
                response.put("DEBITACCTNO", debitAcc);
                response.put("CREDITACCTNO", credAcc);
                response.put("DEBITAMOUNT", amount);
                response.put("messageId", msgId);
                response.put("CREDITTHEIRREF", narative);
                response.put("TRANSACTION_DATE", txnDate);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .header("Content-Type", "application/json")
                        .body(response.toString());
//                return ResponseEntity.ok(detailResponse);
            }
        } catch (Exception e) {
            ErrorResponse resp = new ErrorResponse("Failed to fetch payment by message ID: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
    }
    public static class ErrorResponse {
        private final String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
