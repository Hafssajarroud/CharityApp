package ma.emsi.charityapp.controllers;

import lombok.RequiredArgsConstructor;
import ma.emsi.charityapp.entities.User;
import ma.emsi.charityapp.entities.Don;
import ma.emsi.charityapp.services.UserService;
import ma.emsi.charityapp.services.DonService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;
    private final DonService donService;

    @GetMapping
    public String showProfile(Authentication authentication, Model model) {
        User user = (User) authentication.getPrincipal();

        // Récupérer les statistiques de l'utilisateur
        double totalDons = donService.getTotalDonationsByUser(user.getId());
        int nombreActions = donService.getDonsByUser(user.getId()).size();
        LocalDateTime dernierDon = donService.getDonsByUser(user.getId())
                .stream()
                .findFirst()
                .map(Don::getDate)
                .orElse(null);

        model.addAttribute("user", user);
        model.addAttribute("totalDons", totalDons);
        model.addAttribute("nombreActions", nombreActions);
        model.addAttribute("dernierDon", dernierDon);

        return "profil/profile";
    }

    @GetMapping("/edit")
    public String editProfile(Authentication authentication, Model model) {
        User user = (User) authentication.getPrincipal();
        model.addAttribute("user", user);
        return "profil/profile-edit";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute User user,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            // Ne garder que l'ID de l'utilisateur actuel
            user.setId(currentUser.getId());

            // Mettre à jour l'utilisateur
            User updatedUser = userService.updateUser(currentUser.getId(), user);

            // Mettre à jour l'objet d'authentification avec les nouvelles données
            currentUser.setNom(updatedUser.getNom());
            currentUser.setTelephone(updatedUser.getTelephone());

            redirectAttributes.addFlashAttribute("success", "Profil mis à jour avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour du profil: " + e.getMessage());
        }
        return "redirect:/profile";
    }
} 