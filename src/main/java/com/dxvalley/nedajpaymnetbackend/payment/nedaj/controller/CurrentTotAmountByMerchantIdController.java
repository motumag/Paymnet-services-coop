package com.dxvalley.nedajpaymnetbackend.payment.nedaj.controller;

import com.dxvalley.nedajpaymnetbackend.payment.nedaj.models.FundTransferModel;
import com.dxvalley.nedajpaymnetbackend.payment.nedaj.repo.FundtransferRepository;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/payment/services/v1/nedaj")
public class CurrentTotAmountByMerchantIdController {
    private final FundtransferRepository totalAmountRepo;

    public CurrentTotAmountByMerchantIdController(FundtransferRepository totalAmountRepo) {
        this.totalAmountRepo = totalAmountRepo;
    }

    @GetMapping("/currentTotalAmountByMerchantId/{merchantId}")
    public ResponseEntity<?> fetchByMerchantIdCurrentDate(@PathVariable String merchantId) {

        try {
            LocalDate dateObj = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String currentDatePicked = dateObj.format(formatter);

            List<FundTransferModel> resp = totalAmountRepo.
                    findPaymentByMerchantIdAndAndProccessingIntervalDate(merchantId,currentDatePicked);
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (FundTransferModel transaction : resp) {
                BigDecimal bd = new BigDecimal(transaction.getDEBITAMOUNT().trim());
                totalAmount = totalAmount.add(bd);
            }
            JSONObject responseBody = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("Status", "Success");
            data.put("totalAmount", totalAmount);
            responseBody.put("CurrentTotalAmountResponse", data);
            HttpHeaders resHeaders = new HttpHeaders();
            resHeaders.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<>(responseBody.toString(),resHeaders, HttpStatus.OK);
//            return ResponseEntity.ok(totalAmount);
        } catch (Exception e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);

            throw e;
        }
    }
}
