package com.dxvalley.nedajpaymnetbackend.payment.crowdfund.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrowdFundRequest {
    private Long id;
    private String messageId;
    private String clientId;
    private String debitAccount;
    private String creditAccount;
    private String debitAmount;
    private String debitCurrency="ETB";
    private String debitNarrative="CrowdFund Pay";
    private String creditNarrative="CrowdFund Pay";
}
