package ma.emsi.charityapp.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.emsi.charityapp.dtos.DonDto;
import ma.emsi.charityapp.entities.Don;
import ma.emsi.charityapp.entities.ActionCharite;
import ma.emsi.charityapp.entities.User;
import ma.emsi.charityapp.services.DonService;
import ma.emsi.charityapp.services.ActionChariteService;
import ma.emsi.charityapp.services.PaymentService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/dons")
@RequiredArgsConstructor
public class DonController {

    private final DonService donService;
    private final ActionChariteService actionChariteService;
    private final PaymentService paymentService;

    @GetMapping
    public String showDonForm(@RequestParam(required = false) Integer actionId, Model model) {
        // Vérifier si l'utilisateur est connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/auth/login?redirect=/dons" + (actionId != null ? "?actionId=" + actionId : "");
        }

        List<ActionCharite> actions = actionChariteService.getActiveActions();
        model.addAttribute("actions", actions);

        DonDto donDto = new DonDto();
        if (actionId != null) {
            // Vérifier si l'action existe et est active
            ActionCharite action = actionChariteService.getActionById(actionId)
                    .orElseThrow(() -> new RuntimeException("Action non trouvée"));
            if (!action.isActive()) {
                return "redirect:/actions?error=Action non active";
            }
            donDto.setActionId(actionId);
        }

        // Pré-remplir l'ID de l'utilisateur
        User user = (User) auth.getPrincipal();
        donDto.setUserId(user.getId());

        model.addAttribute("don", donDto);
        return "don";
    }

    @PostMapping
    public String processDon(@Valid @ModelAttribute("don") DonDto donDto,
                             BindingResult result,
                             Authentication authentication,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            // Recharger la liste des actions pour le formulaire
            List<ActionCharite> actions = actionChariteService.getActiveActions();
            redirectAttributes.addFlashAttribute("actions", actions);
            return "don";
        }

        try {
            // Vérifier si l'utilisateur est connecté
            if (authentication == null || !authentication.isAuthenticated()) {
                return "redirect:/auth/login?redirect=/dons";
            }

            // Vérifier si l'action existe et est active
            ActionCharite action = actionChariteService.getActionById(donDto.getActionId())
                    .orElseThrow(() -> new RuntimeException("Action non trouvée"));
            if (!action.isActive()) {
                redirectAttributes.addFlashAttribute("error", "L'action sélectionnée n'est plus disponible.");
                return "redirect:/actions";
            }

            // Vérifier si le montant est valide
            if (donDto.getMontant() <= 0) {
                redirectAttributes.addFlashAttribute("error", "Le montant du don doit être supérieur à 0.");
                return "redirect:/dons?actionId=" + donDto.getActionId();
            }

            // Stocker les données du don dans la session
            session.setAttribute("don", donDto);

            // Créer l'intention de paiement
            String clientSecret = paymentService.createPaymentIntent(donDto);
            redirectAttributes.addFlashAttribute("clientSecret", clientSecret);

            return "redirect:/dons/paiement";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Une erreur est survenue : " + e.getMessage());
            return "redirect:/dons?actionId=" + donDto.getActionId();
        }
    }

    @GetMapping("/paiement")
    public String showPaymentPage(Model model, HttpSession session) {
        if (!model.containsAttribute("clientSecret")) {
            return "redirect:/dons";
        }

        // Récupérer l'action depuis la session
        DonDto donDto = (DonDto) session.getAttribute("don");
        if (donDto != null) {
            ActionCharite action = actionChariteService.getActionById(donDto.getActionId())
                    .orElseThrow(() -> new RuntimeException("Action non trouvée"));
            model.addAttribute("action", action);
        }

        return "paiement";
    }

    @PostMapping("/confirmer-paiement")
    @ResponseBody
    public String confirmPayment(@RequestParam String paymentIntentId) {
        try {
            boolean success = paymentService.confirmPayment(paymentIntentId);
            if (success) {
                return "success";
            } else {
                return "error";
            }
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    @GetMapping("/confirmation/{paymentIntentId}")
    public String showConfirmation(@PathVariable String paymentIntentId, Model model, Authentication authentication, HttpSession session) {
        // Vérifier si l'utilisateur est connecté
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        try {
            // Vérifier si le paiement a réussi
            boolean paymentSuccess = paymentService.confirmPayment(paymentIntentId);
            if (!paymentSuccess) {
                return "redirect:/dons?error=Paiement non confirmé";
            }

            // Récupérer les données du don depuis la session
            DonDto donDto = (DonDto) session.getAttribute("don");
            if (donDto == null) {
                return "redirect:/dons?error=Données de don non trouvées";
            }

            // Créer le don
            User user = (User) authentication.getPrincipal();
            donDto.setUserId(user.getId());
            Don don = donService.createDon(donDto);

            // Nettoyer la session
            session.removeAttribute("don");

            model.addAttribute("don", don);
            return "don-confirmation";
        } catch (Exception e) {
            return "redirect:/dons?error=" + e.getMessage();
        }
    }

    @GetMapping("/historique")
    public String showDonHistory(Authentication authentication, Model model) {
        // Vérifier si l'utilisateur est connecté
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        User user = (User) authentication.getPrincipal();
        List<Don> dons = donService.getDonsByUser(user.getId());
        model.addAttribute("dons", dons);
        return "don-history";
    }

    @PostMapping("/store-don-data")
    @ResponseBody
    public String storeDonData(@RequestBody DonDto donDto, HttpSession session) {
        session.setAttribute("don", donDto);
        return "success";
    }
}
