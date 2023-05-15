package com.dxvalley.nedajpaymnetbackend.payment.nedaj.controller;

import com.dxvalley.nedajpaymnetbackend.payment.nedaj.models.FundTransferModel;
import com.dxvalley.nedajpaymnetbackend.payment.nedaj.repo.FundtransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment/services/v1/nedaj")
public class GetDataByPageController {
    @Autowired
    private final FundtransferRepository repository;

    public GetDataByPageController(FundtransferRepository repository) {
        this.repository = repository;
    }

    @GetMapping(value = "/dataByPage", produces = "application/json")
//    @ExceptionHandler({InvalidInputException.class})
    public ResponseEntity<?> getTransactionDateByPage(@RequestParam int page,
                                                      @RequestParam(defaultValue = "10") int size,
                                                      @RequestParam(defaultValue = "id") String[] sortBy) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
            Page<FundTransferModel> response = repository.findAll(pageable);
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(response.getContent());

        } catch (Exception e) {
            throw e;

        }
    }
}
