package com.dxvalley.nedajpaymnetbackend.payment.nedaj.payloads;

import lombok.Data;

@Data
public class FundTransferResponse {
    private String transactionID;
    private String TRANSACTIONTYPE;
    private String DEBITACCTNO;
    private String DEBITCURRENCY;
    private String DEBITAMOUNT;
    private String DEBITVALUEDATE;
    private String DEBITTHEIRREF;
    private String CREDITTHEIRREF;
    private String CREDITACCTNO;
    private String CREDITCURRENCY;
    private String CREDITVALUEDATE;
    private String PROCESSINGDATE;
    private String TRANSACTION_DATE;
    private String STATUS;
}
