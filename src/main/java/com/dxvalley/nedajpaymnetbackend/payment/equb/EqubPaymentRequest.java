package com.dxvalley.nedajpaymnetbackend.payment.equb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EqubPaymentRequest {
    private String messageId;
    private String debitAccount;
    private String creditAccount;
    private String debitAmount;
    private final String debitCurrency="ETB";
    private final String debitNarrative="CrowdFund Pay";
    private final String creditNarrative="CrowdFund Pay";
}
