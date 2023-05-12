package com.dxvalley.nedajpaymnetbackend.userInfo;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserInfoRequest {
//    @NotBlank(message = "PhoneNumber is required")
    private String phoneNumber;
}
