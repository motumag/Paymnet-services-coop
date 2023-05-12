package com.dxvalley.nedajpaymnetbackend.security.auth;
import com.dxvalley.nedajpaymnetbackend.security.config.JwtService;
import com.dxvalley.nedajpaymnetbackend.security.exception.AuthenticationCustomeException;
import com.dxvalley.nedajpaymnetbackend.security.exception.TokenRefreshException;
import com.dxvalley.nedajpaymnetbackend.security.usermodel.RefreshToken;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    @Autowired
    private final AuthenticationService service;
    @Autowired
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) throws AuthenticationCustomeException {
try{
    if (service.findByEmail(request.getEmail()).isPresent()) {
        ErrorResponse errorResponse= new ErrorResponse("User is already exist");
        return ResponseEntity.status(HttpStatus.IM_USED)
                .header("Content-Type", "application/json")
                .body(errorResponse);
    }
    return ResponseEntity.status(HttpStatus.CREATED)
            .header("Content-Type", "application/json")
            .body(service.register(request));
//    return ResponseEntity.ok(service.register(request));
}
catch (AuthenticationCustomeException pe) {
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
            ErrorResponse errorResponse= new ErrorResponse("User Exist");
            return new ResponseEntity<>(errorResponse, HttpStatus.IM_USED);
        }
        return ResponseEntity.ok(service.registerAdmin(request));
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }
    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
//        System.out.println(requestRefreshToken+ "The incommig refresh token request");
        return service.findByToken(requestRefreshToken)
                .map(service::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    var jwtToken=jwtService.generateToken(user);
                    return ResponseEntity.ok(new TokenRefreshResponse(jwtToken, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }
    @Data
    class ErrorResponse{
        private String description;
        public ErrorResponse(String description) {
            this.description = description;
        }
    }

}
