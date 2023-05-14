package com.dxvalley.nedajpaymnetbackend.otpservices.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmationOtpRequest {
    private String confirmationOtpNumber;
    private String phoneNumber;
}
