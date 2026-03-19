package com.paiement.Backend.Controller;

import com.paiement.Backend.dto.CreditRequest;
import com.paiement.Backend.dto.PaymentRequest;
import com.paiement.Backend.service.PaymentService;
import org.springframework.http.ResponseEntity;
import com.paiement.Backend.Controller.AuthController;
import com.paiement.Backend.repository.BadgeRepository;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class PaymentController {

    private final PaymentService paymentService;
        private final BadgeRepository badgeRepository;


public PaymentController(PaymentService paymentService , BadgeRepository badgeRepository) {
    this.paymentService = paymentService;
    this.badgeRepository = badgeRepository;
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

  @PostMapping("/payment-by-badge")
public ResponseEntity<?> payByBadge(@RequestBody Map<String, String> body) {
    try {
        String uidMerchant = body.get("uidMerchant");
        var merchantBadge = badgeRepository.findByUidRfidAndActifTrue(uidMerchant)
            .orElseThrow(() -> new RuntimeException("Badge commerçant introuvable"));

        PaymentRequest req = new PaymentRequest();
        req.setUidRfid(body.get("uidRfid"));
        req.setMontant(new java.math.BigDecimal(body.get("montant")));
        req.setMerchantId(merchantBadge.getUser().getId());
        return ResponseEntity.ok(paymentService.pay(req));
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

@PostMapping("/account/credit-by-badge")
public ResponseEntity<?> creditByBadge(@RequestBody Map<String, String> body) {
    try {
        var badge = badgeRepository.findByUidRfidAndActifTrue(body.get("uidRfid"))
            .orElseThrow(() -> new RuntimeException("Badge introuvable"));
        CreditRequest req = new CreditRequest();
        req.setUserId(badge.getUser().getId());
        req.setMontant(new java.math.BigDecimal(body.get("montant")));
        return ResponseEntity.ok(paymentService.credit(req));
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
}