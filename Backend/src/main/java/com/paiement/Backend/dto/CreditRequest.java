package com.paiement.Backend.dto;

import java.math.BigDecimal;

public class CreditRequest {
    private Long userId;
    private BigDecimal montant;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
}