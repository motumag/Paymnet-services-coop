package com.dxvalley.nedajpaymnetbackend.payment.nedaj.controller;

import com.dxvalley.nedajpaymnetbackend.payment.nedaj.models.FundTransferModel;
import com.dxvalley.nedajpaymnetbackend.payment.nedaj.repo.FundtransferRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/payment/services/v1/nedaj")
public class GetDataWithAGivenDateController {
    private final FundtransferRepository repository;

    public GetDataWithAGivenDateController(FundtransferRepository repository) {
        this.repository = repository;
    }
    @GetMapping(value = "/givenDateRecordByAgentId")
  public List<FundTransferModel> getRecordsByDateRangeAgent(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam String agentId) {
        try {
            String startDateConverted = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String endDateConverted = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            //Check if the record is not null first here
            return repository.findByProccessingIntervalDateBetweenAndAgentId
                    (startDateConverted, endDateConverted,agentId);

        }catch (Exception e) {
            throw e;

        }
    }
}
