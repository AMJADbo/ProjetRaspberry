package com.paiement.Backend.Controller;

import com.paiement.Backend.dto.CreditRequest;
import com.paiement.Backend.dto.PaymentRequest;
import com.paiement.Backend.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PaymentController {

    private final PaymentService paymentService;

public PaymentController(PaymentService paymentService) {
    this.paymentService = paymentService;
}

    @PostMapping("/payment")
    public ResponseEntity<?> pay(@RequestBody PaymentRequest request) {
        try {
            return ResponseEntity.ok(paymentService.pay(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/account/credit")
    public ResponseEntity<?> credit(@RequestBody CreditRequest request) {
        try {
            return ResponseEntity.ok(paymentService.credit(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}