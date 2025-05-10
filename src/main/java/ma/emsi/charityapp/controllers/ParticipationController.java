package ma.emsi.charityapp.controllers;

import lombok.RequiredArgsConstructor;
import ma.emsi.charityapp.entities.Participation;
import ma.emsi.charityapp.entities.User;
import ma.emsi.charityapp.services.ParticipationService;
import ma.emsi.charityapp.services.ActionChariteService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/participations")
@RequiredArgsConstructor
public class ParticipationController {

    private final ParticipationService participationService;
    private final ActionChariteService actionChariteService;

    @GetMapping("/mes-participations")
    public String mesParticipations(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        User user = (User) authentication.getPrincipal();
        model.addAttribute("participations", participationService.getParticipationsByUser(user.getId()));
        return "participations/list";
    }

    @PostMapping("/action/{actionId}")
    public String participerAction(@PathVariable int actionId,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("error", "Vous devez être connecté pour participer à une action");
            return "redirect:/login";
        }

        try {
            User user = (User) authentication.getPrincipal();

            // Vérifier si l'utilisateur participe déjà
            if (participationService.hasUserParticipated(user.getId(), actionId)) {
                redirectAttributes.addFlashAttribute("error", "Vous participez déjà à cette action !");
                return "redirect:/actions";
            }

            // Créer une nouvelle participation
            Participation participation = new Participation();
            participation.setUtilisateur(user);
            participation.setActionCharite(actionChariteService.getActionById(actionId)
                    .orElseThrow(() -> new RuntimeException("Action non trouvée")));
            participation.setDateParticipation(LocalDateTime.now());

            participationService.createParticipation(participation);

            redirectAttributes.addFlashAttribute("success", "Vous participez maintenant à cette action !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la participation : " + e.getMessage());
        }

        return "redirect:/actions";
    }

    @PostMapping("/annuler/{participationId}")
    public String annulerParticipation(@PathVariable int participationId,
                                       Authentication authentication,
                                       RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("error", "Vous devez être connecté pour annuler une participation");
            return "redirect:/login";
        }

        try {
            User user = (User) authentication.getPrincipal();
            Participation participation = participationService.getParticipationById(participationId)
                    .orElseThrow(() -> new RuntimeException("Participation non trouvée"));

            // Vérifier que l'utilisateur est bien le propriétaire de la participation
            if (participation.getUtilisateur().getId() != user.getId()) {
                redirectAttributes.addFlashAttribute("error", "Vous n'êtes pas autorisé à annuler cette participation");
                return "redirect:/participations/mes-participations";
            }

            participationService.deleteParticipation(participationId);
            redirectAttributes.addFlashAttribute("success", "Participation annulée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'annulation : " + e.getMessage());
        }

        return "redirect:/participations/mes-participations";
    }
}
