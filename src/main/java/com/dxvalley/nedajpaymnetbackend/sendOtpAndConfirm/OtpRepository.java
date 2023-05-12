package com.dxvalley.nedajpaymnetbackend.sendOtpAndConfirm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpModel,String> {

  Optional<OtpModel> findByTextAndMobile(String text, String mobile);

}
