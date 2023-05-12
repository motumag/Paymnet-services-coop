package com.dxvalley.nedajpaymnetbackend.payment.nedaj;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NedajPaymentRepository extends JpaRepository<NedajPaymentModel,Long> {
    NedajPaymentModel findByMessageId(String messageId);
}
