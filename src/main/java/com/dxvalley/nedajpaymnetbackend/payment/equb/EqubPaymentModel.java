package com.dxvalley.nedajpaymnetbackend.payment.equb;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "equb_payment")
@Data
public class EqubPaymentModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "messageId",
            nullable = false,
            unique = true)
    private String messageId;
    private String transactionID;
    @JsonProperty("TRANSACTIONTYPE")
    private String TRANSACTIONTYPE;
    @JsonProperty("DEBITACCTNO")
    private String DEBITACCTNO;
    @JsonProperty("DEBITAMOUNT")
    private String DEBITAMOUNT;
    @JsonProperty("CREDITTHEIRREF")
    private String CREDITTHEIRREF;
    @JsonProperty("CREDITACCTNO")
    private String CREDITACCTNO;
    @JsonProperty("CREDITCURRENCY")
    private String CREDITCURRENCY;
    @JsonProperty("PROCESSINGDATE")
    private String PROCESSINGDATE;
    @JsonProperty("TRANSACTION_DATE")
    private String TRANSACTION_DATE;
    @JsonProperty("STATUS")
    private String STATUS;
    private String responseCode;
    private String errorType;
}
