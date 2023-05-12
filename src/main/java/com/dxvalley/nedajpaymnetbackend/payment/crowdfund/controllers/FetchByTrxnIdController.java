package com.dxvalley.nedajpaymnetbackend.payment.crowdfund.controllers;

import com.dxvalley.nedajpaymnetbackend.payment.crowdfund.models.CrowdFundModel;
import com.dxvalley.nedajpaymnetbackend.payment.crowdfund.repository.CrowdFunRepository;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/payment/services/v1/crowdfund")
public class FetchByTrxnIdController {
    private final CrowdFunRepository repo;

    public FetchByTrxnIdController(CrowdFunRepository repo) {
        this.repo = repo;
    }
    @GetMapping("/fetchByTransactionId/{transactionId}")
    public ResponseEntity<?> findPaymentByTransactionId(@PathVariable String transactionId) {
        try {
            CrowdFundModel detailResponse = repo.findByTransactionID(transactionId);
            if (detailResponse == null) {
                FetchByMessageIdController.ErrorResponse resp = new FetchByMessageIdController.ErrorResponse("No transacion with this messageId");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
            } else {
                System.out.println("detail"+detailResponse);
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
            }
        } catch (Exception e) {
            FetchByMessageIdController.ErrorResponse resp = new FetchByMessageIdController.ErrorResponse("Failed to fetch payment by message ID: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
    }
}
