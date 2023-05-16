package com.dxvalley.nedajpaymnetbackend.security.auth;

import com.dxvalley.nedajpaymnetbackend.security.config.JwtService;
import com.dxvalley.nedajpaymnetbackend.security.exception.AuthenticationCustomeException;
import com.dxvalley.nedajpaymnetbackend.security.exception.TokenRefreshException;
import com.dxvalley.nedajpaymnetbackend.security.repository.UserRepository;
import com.dxvalley.nedajpaymnetbackend.security.usermodel.RefreshToken;
import com.dxvalley.nedajpaymnetbackend.security.usermodel.User;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    @Autowired
    private final AuthenticationService service;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final UserRepository changePassRepo;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    @Operation(summary = "User Registration")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) throws AuthenticationCustomeException {
        try {
            if (service.findByEmail(request.getEmail()).isPresent()) {
                ErrorResponse errorResponse = new ErrorResponse("User is already exist");
                return ResponseEntity.status(HttpStatus.IM_USED)
                        .header("Content-Type", "application/json")
                        .body(errorResponse);
            }
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header("Content-Type", "application/json")
                    .body(service.register(request));
//    return ResponseEntity.ok(service.register(request));
        } catch (AuthenticationCustomeException pe) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("Message", pe.getMessage());
            return ResponseEntity.status(pe.getStatus())
                    .header("Content-Type", "application/json")
                    .body(errorResponse.toString());
        }
    }

    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody RegisterRequest request) {

        if (service.findByEmail(request.getEmail()).isPresent()) {
            ErrorResponse errorResponse = new ErrorResponse("User Exist");
            return new ResponseEntity<>(errorResponse, HttpStatus.IM_USED);
        }
        return ResponseEntity.ok(service.registerAdmin(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) throws Exception {
        try {
            return ResponseEntity.ok(service.authenticate(request));
        }catch (Exception e){
            throw e;
        }
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
//        System.out.println(requestRefreshToken+ "The incommig refresh token request");
        return service.findByToken(requestRefreshToken)
                .map(service::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    var jwtToken = jwtService.generateToken(user);
                    return ResponseEntity.ok(new TokenRefreshResponse(jwtToken, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) throws Exception {
        try {
            User user = changePassRepo.findByEmail(request.getEmail())
                    .orElseThrow(() -> new Exception("User not found"));

            String resetToken = UUID.randomUUID().toString();
            user.setRestToken(resetToken);
            changePassRepo.save(user);
            JSONObject response = new JSONObject();
            response.put("ResteToken:", resetToken);
//            emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
//            return ResponseEntity.ok().build();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header("Content-Type", "application/json")
                    .body(response.toString());
        } catch (Exception ex) {
            throw ex;
        }
    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String password) {
        try {
            User user = changePassRepo.findByRestToken(token).orElseThrow(() -> new AuthenticationException("Invalid token") {
                    });

            user.setPassword(passwordEncoder.encode(password));
            user.setRestToken(null);
            changePassRepo.save(user);
//            return ResponseEntity.ok().build();
            JSONObject response = new JSONObject();
            response.put("ResteToken:", "You reset password successfully");
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header("Content-Type", "application/json")
                    .body(response.toString());
        }  catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthenticationCustomeException(400, "Error resetting password"));
        }
    }



    @Data
    class ErrorResponse {
        private String description;

        public ErrorResponse(String description) {
            this.description = description;
        }
    }

}
