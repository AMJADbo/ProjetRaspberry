package com.paiement.Backend.dto;

import java.math.BigDecimal;

public class PaymentRequest {
    private String uidRfid;
    private BigDecimal montant;
    private Long merchantId;

    public String getUidRfid() { return uidRfid; }
    public void setUidRfid(String uidRfid) { this.uidRfid = uidRfid; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }
}