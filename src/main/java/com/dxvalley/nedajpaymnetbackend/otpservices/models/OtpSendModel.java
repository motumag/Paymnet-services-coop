package com.dxvalley.nedajpaymnetbackend.otpservices.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "otp_service")
public class OtpSendModel {
    @Id
    @SequenceGenerator(
            name = "otp_Sequence",
            sequenceName = "otp_Sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "otp_Sequence"
    )
    private  Long id;
    private  String otpNumber;
    private  String mobile;
    private  String status;
    private  String responseCode;
}
