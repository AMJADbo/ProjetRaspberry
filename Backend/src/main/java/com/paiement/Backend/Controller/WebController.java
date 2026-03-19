package com.paiement.Backend.Controller;

import com.paiement.Backend.dto.CreditRequest;
import com.paiement.Backend.dto.LoginRequest;
import com.paiement.Backend.dto.PaymentRequest;
import com.paiement.Backend.entity.Transactions;
import com.paiement.Backend.entity.User;
import com.paiement.Backend.repository.TransactionsRepository;
import com.paiement.Backend.service.AuthService;
import com.paiement.Backend.service.PaymentService;
import com.paiement.Backend.service.UserService;
import com.paiement.Backend.dto.CreateUserRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class WebController {

    private final AuthService authService;
    private final UserService userService;
    private final PaymentService paymentService;
    private final TransactionsRepository transactionRepository;

    public WebController(AuthService authService, UserService userService,
                         PaymentService paymentService, TransactionsRepository transactionRepository) {
        this.authService = authService;
        this.userService = userService;
        this.paymentService = paymentService;
        this.transactionRepository = transactionRepository;
    }

    // LOGIN
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String motDePasse,
                        HttpSession session, Model model) {
        LoginRequest req = new LoginRequest();
        req.setEmail(email);
        req.setMotDePasse(motDePasse);

        return authService.login(req).map(user -> {
            session.setAttribute("sessionUser", user);
            if (user.getRole() == User.Role.ADMIN) return "redirect:/admin/users";
            if (user.getRole() == User.Role.MERCHANT) return "redirect:/merchant/payment";
            return "redirect:/user/dashboard";
        }).orElseGet(() -> {
            model.addAttribute("error", "Email ou mot de passe incorrect");
            return "login";
        });
    }

    // LOGOUT
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // ADMIN
    @GetMapping("/admin/users")
    public String adminUsers(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        model.addAttribute("sessionUser", session.getAttribute("sessionUser"));
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @PostMapping("/admin/users")
    public String createUser(@RequestParam String nom, @RequestParam String email,
                             @RequestParam String motDePasse, @RequestParam String role,
                             HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        CreateUserRequest req = new CreateUserRequest();
        req.setNom(nom);
        req.setEmail(email);
        req.setMotDePasse(motDePasse);
        req.setRole(User.Role.valueOf(role));
        userService.createUser(req);
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/{id}/toggle")
    public String toggleUser(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        userService.toggleActif(id);
        return "redirect:/admin/users";
    }

    // USER
    @GetMapping("/user/dashboard")
    public String userDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("sessionUser");
        if (user == null) return "redirect:/login";
        // Rafraîchir le solde depuis la BDD
        userService.getAllUsers().stream()
            .filter(u -> u.getId().equals(user.getId()))
            .findFirst().ifPresent(u -> session.setAttribute("sessionUser", u));
        model.addAttribute("sessionUser", session.getAttribute("sessionUser"));
        List<Transactions> transactions = transactionRepository.findByUserIdOrderByDateDesc(user.getId());
        model.addAttribute("transactions", transactions);
        return "user/dashboard";
    }

    @PostMapping("/user/credit")
    public String credit(@RequestParam BigDecimal montant, HttpSession session) {
        User user = (User) session.getAttribute("sessionUser");
        if (user == null) return "redirect:/login";
        CreditRequest req = new CreditRequest();
        req.setUserId(user.getId());
        req.setMontant(montant);
        paymentService.credit(req);
        return "redirect:/user/dashboard";
    }

    // MERCHANT
    @GetMapping("/merchant/payment")
    public String merchantPage(HttpSession session, Model model) {
        if (!isMerchant(session)) return "redirect:/login";
        model.addAttribute("sessionUser", session.getAttribute("sessionUser"));
        return "merchant/payment";
    }

    @PostMapping("/merchant/payment")
    public String merchantPayment(@RequestParam String uidRfid,
                                  @RequestParam BigDecimal montant,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        if (!isMerchant(session)) return "redirect:/login";
        User merchant = (User) session.getAttribute("sessionUser");
        try {
            PaymentRequest req = new PaymentRequest();
            req.setUidRfid(uidRfid);
            req.setMontant(montant);
            req.setMerchantId(merchant.getId());
            paymentService.pay(req);
            redirectAttributes.addFlashAttribute("success", "Paiement accepté !");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/merchant/payment";
    }

    // Helpers
    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("sessionUser");
        return user != null && user.getRole() == User.Role.ADMIN;
    }

    private boolean isMerchant(HttpSession session) {
        User user = (User) session.getAttribute("sessionUser");
        return user != null && user.getRole() == User.Role.MERCHANT;
    }

    @GetMapping("/")
public String home() {
    return "redirect:/login";
}
}