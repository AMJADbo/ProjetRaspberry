package com.paiement.Backend.service;

import com.paiement.Backend.dto.CreditRequest;
import com.paiement.Backend.dto.PaymentRequest;
import com.paiement.Backend.entity.Transactions;
import com.paiement.Backend.entity.User;
import com.paiement.Backend.repository.BadgeRepository;
import com.paiement.Backend.repository.TransactionsRepository;
import com.paiement.Backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PaymentService {

    private final BadgeRepository badgeRepository;
    private final UserRepository userRepository;
    private final TransactionsRepository transactionRepository;

    public PaymentService(BadgeRepository badgeRepository, UserRepository userRepository, TransactionsRepository transactionRepository) {
    this.badgeRepository = badgeRepository;
    this.userRepository = userRepository;
    this.transactionRepository = transactionRepository;
}

    @Transactional
    public Transactions pay(PaymentRequest request) {
        // 1. Trouver l'utilisateur via son badge RFID
        var badge = badgeRepository.findByUidRfidAndActifTrue(request.getUidRfid())
            .orElseThrow(() -> new RuntimeException("Badge introuvable ou désactivé"));

        User user = badge.getUser();

        // 2. Vérifier que le solde est suffisant
        if (user.getSolde().compareTo(request.getMontant()) < 0) {
            throw new RuntimeException("Solde insuffisant");
        }

        // 3. Débiter
        user.setSolde(user.getSolde().subtract(request.getMontant()));
        userRepository.save(user);

        // 4. Trouver le commerçant
        User merchant = userRepository.findById(request.getMerchantId())
            .orElseThrow(() -> new RuntimeException("Commerçant introuvable"));

        // 5. Enregistrer la transaction
        Transactions transaction = new Transactions();
        transaction.setUser(user);
        transaction.setMontant(request.getMontant());
        transaction.setType(Transactions.TypeTransaction.PAYMENT);
        transaction.setMerchant(merchant);
        transaction.setDescription("Paiement chez " + merchant.getNom());
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transactions credit(CreditRequest request) {
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Créditer le compte
        user.setSolde(user.getSolde().add(request.getMontant()));
        userRepository.save(user);

        // Enregistrer la transaction
        Transactions transaction = new Transactions();
        transaction.setUser(user);
        transaction.setMontant(request.getMontant());
        transaction.setType(Transactions.TypeTransaction.CREDIT);
        transaction.setDescription("Rechargement du compte");
        return transactionRepository.save(transaction);
    }
}