package com.dxvalley.nedajpaymnetbackend.security.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest extends Exception{
    private String firstname;
    private String lastname;
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email address")
    private String email;
    private String password;
    private String clientKey;
}
