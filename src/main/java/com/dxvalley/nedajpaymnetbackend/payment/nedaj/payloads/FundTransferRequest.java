package com.dxvalley.nedajpaymnetbackend.payment.nedaj.payloads;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FundTransferRequest {
    private Long id;
    private String merchantId;
    private String agentId;
    private String fuelType;
    private String messageId;
    private String debitAccount;
    private String creditAccount;
    private String debitAmount;
    private String debitCurrency="ETB";
    private String debitNarrative="nedaj-pay";
    private String creditNarrative="nedajpay";
    //for confirmation
    private String confirmationOtpNumber;
    private String phoneNumber;
}
