package ma.emsi.charityapp.controllers.organisation;

import lombok.RequiredArgsConstructor;
import ma.emsi.charityapp.entities.ActionCharite;
import ma.emsi.charityapp.entities.Don;
import ma.emsi.charityapp.entities.Organisation;
import ma.emsi.charityapp.entities.User;
import ma.emsi.charityapp.services.ActionChariteService;
import ma.emsi.charityapp.services.DonService;
import ma.emsi.charityapp.services.OrganisationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Controller
@RequestMapping("/organisation/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ORGANISATION')")
public class OrganisationDashboardController {

    private final OrganisationService organisationService;
    private final ActionChariteService actionChariteService;
    private final DonService donService;

    @GetMapping
    public String getDashboard(Authentication authentication, Model model) {
        User user = (User) authentication.getPrincipal();
        Optional<Organisation> organisationOpt = organisationService.getOrganisationByAdminId(user.getId());

        if (organisationOpt.isEmpty()) {
            return "redirect:/organisation/new";
        }

        Organisation organisation = organisationOpt.get();

        // Statistiques générales
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalActions", actionChariteService.countActionsByOrganisation(organisation.getId()));
        stats.put("activeActions", actionChariteService.countActiveActionsByOrganisation(organisation.getId()));
        stats.put("totalDons", donService.getTotalDonationsByOrganisation(organisation.getId()));
        stats.put("averageDonation", donService.getAverageDonationByOrganisation(organisation.getId()));

        // Dernières actions
        List<ActionCharite> recentActions = actionChariteService.getRecentActionsByOrganisation(organisation.getId());

        // Derniers dons
        List<Don> recentDons = donService.getRecentDonsByOrganisation(organisation.getId());

        model.addAttribute("organisation", organisation);
        model.addAttribute("stats", stats);
        model.addAttribute("recentActions", recentActions);
        model.addAttribute("recentDons", recentDons);

        return "organisation/dashboard";
    }

    @GetMapping("/actions")
    public String getActions(Authentication authentication, Model model) {
        User user = (User) authentication.getPrincipal();
        Optional<Organisation> organisationOpt = organisationService.getOrganisationByAdminId(user.getId());

        if (organisationOpt.isEmpty()) {
            return "redirect:/organisation/new";
        }

        Organisation organisation = organisationOpt.get();
        List<ActionCharite> actions = actionChariteService.getActionsByOrganisation(organisation.getId());
        model.addAttribute("actions", actions);
        model.addAttribute("organisation", organisation);

        return "organisation/actions/list";
    }

    @GetMapping("/dons")
    public String getDons(Authentication authentication, Model model) {
        User user = (User) authentication.getPrincipal();
        Optional<Organisation> organisationOpt = organisationService.getOrganisationByAdminId(user.getId());

        if (organisationOpt.isEmpty()) {
            return "redirect:/organisation/new";
        }

        Organisation organisation = organisationOpt.get();
        List<Don> dons = donService.getDonsByOrganisation(organisation.getId());
        model.addAttribute("dons", dons);
        model.addAttribute("organisation", organisation);

        return "organisation/dons";
    }

    @GetMapping("/profile")
    public String getProfile(Authentication authentication, Model model) {
        User user = (User) authentication.getPrincipal();
        Optional<Organisation> organisationOpt = organisationService.getOrganisationByAdminId(user.getId());

        if (organisationOpt.isEmpty()) {
            return "redirect:/organisation/new";
        }

        Organisation organisation = organisationOpt.get();
        model.addAttribute("organisation", organisation);
        return "organisation/profile";
    }

    @GetMapping("/profile/edit")
    public String showEditProfileForm(Authentication authentication, Model model) {
        User user = (User) authentication.getPrincipal();
        Optional<Organisation> organisationOpt = organisationService.getOrganisationByAdminId(user.getId());

        if (organisationOpt.isEmpty()) {
            return "redirect:/organisation/new";
        }

        Organisation organisation = organisationOpt.get();
        model.addAttribute("organisation", organisation);
        return "organisation/profile-edit";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@ModelAttribute Organisation organisation,
                                @RequestParam(required = false) MultipartFile logoFile,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            User user = (User) authentication.getPrincipal();
            Organisation existingOrganisation = organisationService.getOrganisationByAdminId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Organisation non trouvée"));

            // Mettre à jour les champs
            existingOrganisation.setNom(organisation.getNom());
            existingOrganisation.setContactPrincipal(organisation.getContactPrincipal());
            existingOrganisation.setAdresse(organisation.getAdresse());
            existingOrganisation.setTelephone(organisation.getTelephone());
            existingOrganisation.setDescription(organisation.getDescription());

            // Gérer le logo si un nouveau est fourni
            if (logoFile != null && !logoFile.isEmpty()) {
                String logoPath = organisationService.saveLogo(logoFile);
                existingOrganisation.setLogo(logoPath);
            }

            organisationService.updateOrganisation(existingOrganisation.getId(), existingOrganisation);
            redirectAttributes.addFlashAttribute("success", "Profil mis à jour avec succès");
            return "redirect:/organisation/dashboard/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour du profil: " + e.getMessage());
            return "redirect:/organisation/dashboard/profile/edit";
        }
    }
}