package com.dxvalley.nedajpaymnetbackend.otpservices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends JpaRepository<OtpSendModel, Long> {
    OtpSendModel findByOtpNumber(String otpNumber);
}
