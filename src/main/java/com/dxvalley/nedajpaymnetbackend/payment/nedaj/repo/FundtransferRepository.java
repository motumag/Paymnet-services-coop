package com.dxvalley.nedajpaymnetbackend.payment.nedaj.repo;

import com.dxvalley.nedajpaymnetbackend.payment.nedaj.models.FundTransferModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FundtransferRepository extends JpaRepository<FundTransferModel, Long> {
    FundTransferModel findByMessageId(String messageId);

    List<FundTransferModel> findByProccessingIntervalDateBetweenAndAgentId(String startDate, String endDate, String agentId);

    List<FundTransferModel> findPaymentByAgentId(String AgentId);
    List<FundTransferModel> findPaymentByMerchantId(String merchantId);
    List<FundTransferModel> findPaymentByMerchantIdAndAndProccessingIntervalDate
            (String merchantId,String currentDate);
}
