package com.dxvalley.nedajpaymnetbackend.otpservices;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpRequest {
    @JsonProperty("Mobile")
    private String Mobile;
    @JsonProperty("Text")
    private String Text;
}
