package com.dxvalley.nedajpaymnetbackend.payment.crowdfund.repository;

import com.dxvalley.nedajpaymnetbackend.payment.crowdfund.models.CrowdFundModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrowdFunRepository extends JpaRepository<CrowdFundModel,Long> {
    CrowdFundModel findByMessageId(String messageId);
    CrowdFundModel findByTransactionID(String messageId);
}
