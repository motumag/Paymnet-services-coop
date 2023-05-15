package com.dxvalley.nedajpaymnetbackend.security.auth;

import com.dxvalley.nedajpaymnetbackend.security.config.JwtService;
import com.dxvalley.nedajpaymnetbackend.security.exception.AuthenticationCustomeException;
import com.dxvalley.nedajpaymnetbackend.security.exception.TokenRefreshException;
import com.dxvalley.nedajpaymnetbackend.security.repository.RefreshTokenRepository;
import com.dxvalley.nedajpaymnetbackend.security.repository.UserRepository;
import com.dxvalley.nedajpaymnetbackend.security.usermodel.RefreshToken;
import com.dxvalley.nedajpaymnetbackend.security.usermodel.Role;
import com.dxvalley.nedajpaymnetbackend.security.usermodel.User;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    @Value("${jwt.refreshTokenDurationMs}")
    private Long refreshTokenDurationMs;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userRepository.findById(userId).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }

    public AuthenticationResponse register(RegisterRequest request) throws AuthenticationCustomeException {
        try {
            var user = User.builder()
                    .firstName(request.getFirstname())
                    .lastName(request.getLastname())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.USER)
                    .clientKey(getGeneratedClientId())
                    .secretKey(getGeneratedSecretKey())
                    .apiKey(getGeneratedApiKey())
                    .build();
            repository.save(user);
            var jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        } catch (Exception e) {
            throw new AuthenticationCustomeException(404, "Error while creating user");
        }
    }

    public AuthenticationResponse registerAdmin(RegisterRequest request) {
        var user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ADMIN)
                .build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            var user = repository.findByEmail(request.getEmail()).orElseThrow();

            var jwtToken = jwtService.generateToken(user);

            var userIdTofindRefreshToken = repository.findById(user.getId());
            var actualUserId = userIdTofindRefreshToken.get().getId();

            //==========refresh token is added===========//
            String refTokenReall = "";
            var refreshTokenForUser = refreshTokenRepository.findByUserId(actualUserId);
            if (Objects.isNull(refreshTokenForUser)) {
                RefreshToken refreshToken = createRefreshToken(user.getId());
//            refreshTokenRepository.save(refreshToken);
                refTokenReall = refreshToken.getToken();
            } else {
                JSONObject respObject = new JSONObject(refreshTokenForUser);

                refTokenReall = respObject.getString("token");
            }
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .refresToken(refTokenReall)
                    .build();
        } catch (Exception e) {
            String specificErorr = e.getStackTrace()[0].getLineNumber() + "";
            throw new AuthenticationCustomeException(400, "Error while authenticating user" + " " + specificErorr);
        }
    }

    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    //To generate clientId and secretKay
    public String getGeneratedClientId() {
        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString();
        return uuidAsString;
    }

    public String getGeneratedSecretKey() {
        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString();
        return uuidAsString;
    }

    public String getGeneratedApiKey() {
        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString();
        return uuidAsString;
    }
}
