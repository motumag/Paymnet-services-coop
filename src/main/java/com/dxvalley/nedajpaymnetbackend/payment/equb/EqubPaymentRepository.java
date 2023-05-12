package com.dxvalley.nedajpaymnetbackend.payment.equb;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EqubPaymentRepository extends JpaRepository<EqubPaymentModel,Long> {
    EqubPaymentModel findByMessageId(String messageId);
}
