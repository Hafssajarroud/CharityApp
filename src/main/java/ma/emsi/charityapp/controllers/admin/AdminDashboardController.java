package ma.emsi.charityapp.controllers.admin;

import ma.emsi.charityapp.services.DonService;
import ma.emsi.charityapp.services.OrganisationService;
import ma.emsi.charityapp.services.UserService;
import ma.emsi.charityapp.services.ActionChariteService;
import ma.emsi.charityapp.entities.Don;
import ma.emsi.charityapp.entities.Organisation;
import ma.emsi.charityapp.entities.ActionCharite;
import ma.emsi.charityapp.entities.User;
import ma.emsi.charityapp.dtos.ActionChariteDto;
import ma.emsi.charityapp.dtos.OrganisationDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminDashboardController {

    private final OrganisationService organisationService;
    private final UserService userService;
    private final DonService donService;
    private final ActionChariteService actionChariteService;

    public AdminDashboardController(OrganisationService organisationService,
                                    UserService userService,
                                    DonService donService,
                                    ActionChariteService actionChariteService) {
        this.organisationService = organisationService;
        this.userService = userService;
        this.donService = donService;
        this.actionChariteService = actionChariteService;
    }

    @GetMapping("/dashboard")
    public String getDashboard(Model model) {
        // Statistiques
        long organisationsCount = organisationService.countAllOrganisations();
        long actionsCount = actionChariteService.countAllActions();
        long usersCount = userService.countAllUsers();
        double totalDonations = donService.getTotalDonations();

        // Derniers dons
        List<Don> recentDons = donService.getRecentDons();

        // Dernières organisations
        List<Organisation> recentOrganisations = organisationService.getRecentOrganisations();

        // Dernières actions
        List<ActionCharite> recentActions = actionChariteService.getRecentActions();

        // Ajouter les données au modèle
        model.addAttribute("stats", new DashboardStats(organisationsCount, actionsCount, usersCount, totalDonations));
        model.addAttribute("recentDons", recentDons);
        model.addAttribute("recentOrganisations", recentOrganisations);
        model.addAttribute("recentActions", recentActions);

        return "admin/dashboard";
    }

    @GetMapping("/organisations")
    public String gestionOrganisations(Model model) {
        List<Organisation> organisations = organisationService.getAllOrganisations();
        model.addAttribute("organisations", organisations);
        return "admin/organisations";
    }

    @GetMapping("/organisations/{id}")
    public String voirOrganisation(@PathVariable int id, Model model) {
        Organisation organisation = organisationService.getOrganisationById(id)
                .orElseThrow(() -> new RuntimeException("Organisation non trouvée"));
        model.addAttribute("organisation", organisation);
        return "admin/organisation-details";
    }

    @PostMapping("/organisations/{id}/validate")
    public String validateOrganisation(@PathVariable int id, RedirectAttributes redirectAttributes) {
        System.out.println("Validation de l'organisation demandée pour l'ID: " + id);
        try {
            organisationService.validateOrganisation(id);
            System.out.println("Organisation validée avec succès");
            redirectAttributes.addFlashAttribute("success", "L'organisation a été validée avec succès");
            return "redirect:/admin/organisations";
        } catch (Exception e) {
            System.out.println("Erreur lors de la validation: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Une erreur est survenue lors de la validation de l'organisation");
            return "redirect:/admin/organisations";
        }
    }

    @PostMapping("/organisations/{id}/delete")
    public String deleteOrganisation(@PathVariable int id) {
        organisationService.deleteOrganisation(id);
        return "redirect:/admin/organisations";
    }

    @GetMapping("/organisations/{id}/edit")
    public String showEditOrganisationForm(@PathVariable int id, Model model) {
        Organisation organisation = organisationService.getOrganisationById(id)
                .orElseThrow(() -> new RuntimeException("Organisation non trouvée"));

        OrganisationDto dto = new OrganisationDto();
        dto.setId(organisation.getId());
        dto.setNom(organisation.getNom());
        dto.setAdresse(organisation.getAdresse());
        dto.setNumeroFiscal(organisation.getNumeroFiscal());
        dto.setContactPrincipal(organisation.getContactPrincipal());
        dto.setTelephone(organisation.getTelephone());
        dto.setDescription(organisation.getDescription());

        model.addAttribute("organisation", dto);
        return "admin/organisation-form";
    }

    @PostMapping("/organisations/{id}")
    public String updateOrganisation(@PathVariable int id,
                                     @Valid @ModelAttribute("organisation") OrganisationDto organisationDto,
                                     BindingResult result,
                                     Authentication authentication) throws IOException {
        if (result.hasErrors()) {
            return "admin/organisation-form";
        }

        Organisation organisation = new Organisation();
        organisation.setNom(organisationDto.getNom());
        organisation.setAdresse(organisationDto.getAdresse());
        organisation.setNumeroFiscal(organisationDto.getNumeroFiscal());
        organisation.setContactPrincipal(organisationDto.getContactPrincipal());
        organisation.setTelephone(organisationDto.getTelephone());
        organisation.setDescription(organisationDto.getDescription());

        if (organisationDto.getLogo() != null && !organisationDto.getLogo().isEmpty()) {
            String logoPath = organisationService.saveLogo(organisationDto.getLogo());
            organisation.setLogo(logoPath);
        }

        organisationService.updateOrganisation(id, organisation);
        return "redirect:/admin/organisations";
    }

    @GetMapping("/actions")
    public String gestionActions(Model model) {
        List<ActionCharite> actions = actionChariteService.getAllActions();
        model.addAttribute("actions", actions);

        // Initialiser les messages s'ils sont null
        if (!model.containsAttribute("success")) {
            model.addAttribute("success", "");
        }
        if (!model.containsAttribute("error")) {
            model.addAttribute("error", "");
        }

        // Ajouter les statistiques
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalActions", actionChariteService.countAllActions());
        stats.put("activeActions", actions.stream().filter(a -> !a.isArchived()).count());
        stats.put("archivedActions", actions.stream().filter(ActionCharite::isArchived).count());
        stats.put("averageDonation", donService.getAverageDonation());
        model.addAttribute("stats", stats);

        return "admin/actions";
    }

    @GetMapping("/actions/new")
    public String showCreateActionForm(Model model) {
        ActionChariteDto actionDto = new ActionChariteDto();
        actionDto.setSommeActuelle(0.0);
        model.addAttribute("action", actionDto);
        model.addAttribute("organisations", organisationService.getAllOrganisations());
        return "admin/action-form";
    }

    @PostMapping("/actions")
    public String createAction(@Valid @ModelAttribute("action") ActionChariteDto actionDto,
                               @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        System.out.println("Début de la création d'action");
        System.out.println("Action DTO: " + actionDto);

        if (result.hasErrors()) {
            System.out.println("Erreurs de validation: " + result.getAllErrors());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.action", result);
            redirectAttributes.addFlashAttribute("action", actionDto);
            redirectAttributes.addFlashAttribute("organisations", organisationService.getAllOrganisations());
            redirectAttributes.addFlashAttribute("error", "Veuillez corriger les erreurs dans le formulaire");
            return "redirect:/admin/actions/new";
        }

        try {
            // Vérifier que tous les champs obligatoires sont présents
            if (actionDto.getTitre() == null || actionDto.getTitre().trim().isEmpty()) {
                throw new IllegalArgumentException("Le titre est obligatoire");
            }
            if (actionDto.getDescription() == null || actionDto.getDescription().trim().isEmpty()) {
                throw new IllegalArgumentException("La description est obligatoire");
            }
            if (actionDto.getDateDebut() == null) {
                throw new IllegalArgumentException("La date de début est obligatoire");
            }
            if (actionDto.getDateFin() == null) {
                throw new IllegalArgumentException("La date de fin est obligatoire");
            }
            if (actionDto.getLieu() == null || actionDto.getLieu().trim().isEmpty()) {
                throw new IllegalArgumentException("Le lieu est obligatoire");
            }
            if (actionDto.getObjectifCollecte() == null || actionDto.getObjectifCollecte() <= 0) {
                throw new IllegalArgumentException("L'objectif de collecte doit être supérieur à 0");
            }
            if (actionDto.getSommeActuelle() == null || actionDto.getSommeActuelle() < 0) {
                throw new IllegalArgumentException("La somme actuelle ne peut pas être négative");
            }
            if (actionDto.getOrganisationId() == null) {
                throw new IllegalArgumentException("L'organisation est obligatoire");
            }
            if (actionDto.getCategorie() == null) {
                throw new IllegalArgumentException("La catégorie est obligatoire");
            }

            System.out.println("Tentative de sauvegarde du logo");
            // Sauvegarder le logo s'il existe
            if (logoFile != null && !logoFile.isEmpty()) {
                String logoPath = actionChariteService.saveLogo(logoFile);
                actionDto.setLogo(logoPath);
                System.out.println("Logo sauvegardé: " + logoPath);
            }

            System.out.println("Tentative de création de l'action");
            // Créer l'action
            actionChariteService.createAction(actionDto);
            System.out.println("Action créée avec succès");

            redirectAttributes.addFlashAttribute("success", "L'action a été créée avec succès");
            return "redirect:/admin/actions";
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur d'argument: " + e.getMessage());
            redirectAttributes.addFlashAttribute("action", actionDto);
            redirectAttributes.addFlashAttribute("organisations", organisationService.getAllOrganisations());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/actions/new";
        } catch (Exception e) {
            System.out.println("Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("action", actionDto);
            redirectAttributes.addFlashAttribute("organisations", organisationService.getAllOrganisations());
            redirectAttributes.addFlashAttribute("error", "Une erreur est survenue lors de la création de l'action : " + e.getMessage());
            return "redirect:/admin/actions/new";
        }
    }

    @GetMapping("/actions/{id}/edit")
    public String showEditActionForm(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            ActionCharite action = actionChariteService.getActionById(id)
                    .orElseThrow(() -> new IllegalArgumentException("L'action n'existe pas"));

            // Convertir l'entité en DTO
            ActionChariteDto actionDto = new ActionChariteDto();
            actionDto.setId(action.getId());
            actionDto.setTitre(action.getTitre());
            actionDto.setDescription(action.getDescription());
            actionDto.setDateDebut(action.getDateDebut());
            actionDto.setDateFin(action.getDateFin());
            actionDto.setLieu(action.getLieu());
            actionDto.setObjectifCollecte(action.getObjectifCollecte());
            actionDto.setSommeActuelle(action.getSommeActuelle());
            actionDto.setOrganisationId(action.getOrganisation().getId());
            actionDto.setCategorie(action.getCategorie());
            actionDto.setLogo(action.getLogo());

            model.addAttribute("action", actionDto);
            model.addAttribute("organisations", organisationService.getAllOrganisations());
            return "admin/action-form";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/actions";
        }
    }

    @PostMapping("/actions/{id}")
    public String updateAction(@PathVariable int id,
                               @ModelAttribute("action") ActionChariteDto actionDto,
                               @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Veuillez corriger les erreurs dans le formulaire");
            return "redirect:/admin/actions/" + id + "/edit";
        }

        try {
            // Vérifier que l'action existe
            if (!actionChariteService.getActionById(id).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "L'action n'existe pas");
                return "redirect:/admin/actions";
            }

            // Si un nouveau logo est fourni, le sauvegarder
            if (logoFile != null && !logoFile.isEmpty()) {
                String logoPath = actionChariteService.saveLogo(logoFile);
                actionDto.setLogo(logoPath);
            }

            // Mettre à jour l'action
            actionChariteService.updateAction(id, actionDto);
            redirectAttributes.addFlashAttribute("success", "L'action a été mise à jour avec succès");
            return "redirect:/admin/actions";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/actions/" + id + "/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Une erreur est survenue lors de la mise à jour de l'action : " + e.getMessage());
            return "redirect:/admin/actions/" + id + "/edit";
        }
    }

    @PostMapping("/actions/{id}/archive")
    public String archiveAction(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            actionChariteService.archiveAction(id);
            redirectAttributes.addFlashAttribute("success", "L'action a été " +
                    (actionChariteService.getActionById(id).get().isArchived() ? "archivée" : "désarchivée") + " avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Une erreur est survenue lors de l'archivage de l'action");
        }
        return "redirect:/admin/actions";
    }

    @PostMapping("/actions/{id}/delete")
    public String deleteAction(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            actionChariteService.deleteAction(id);
            redirectAttributes.addFlashAttribute("success", "L'action a été supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Une erreur est survenue lors de la suppression de l'action");
        }
        return "redirect:/admin/actions";
    }

    @GetMapping("/actions/statistics")
    @ResponseBody
    public Map<String, Object> getActionStatistics() {
        List<ActionCharite> actions = actionChariteService.getAllActions();
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalActions", actionChariteService.countAllActions());
        stats.put("activeActions", actions.stream().filter(a -> !a.isArchived()).count());
        stats.put("archivedActions", actions.stream().filter(ActionCharite::isArchived).count());
        stats.put("totalDonations", donService.getTotalDonations());
        stats.put("averageDonation", donService.getAverageDonation());
        return stats;
    }

    @GetMapping("/utilisateurs")
    public String gestionUtilisateurs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = userService.getAllUsers(pageable);

        // Statistiques
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userService.countAllUsers());
        stats.put("activeUsers", userService.countActiveUsers());
        stats.put("totalDonations", donService.getTotalDonations());
        stats.put("averageDonation", donService.getAverageDonation());

        model.addAttribute("users", usersPage.getContent());
        model.addAttribute("page", usersPage);
        model.addAttribute("stats", stats);

        return "admin/utilisateurs";
    }

    @GetMapping("/utilisateurs/{id}")
    public String voirUtilisateur(@PathVariable int id, Model model) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Récupérer l'historique des dons
        List<Don> userDons = donService.getDonsByUser(id);

        // Statistiques de l'utilisateur
        Map<String, Object> userStats = new HashMap<>();
        userStats.put("totalDonations", donService.getTotalDonationsByUser(id));
        userStats.put("donationCount", userDons.size());
        userStats.put("lastDonation", userDons.isEmpty() ? null : userDons.get(0));

        model.addAttribute("user", user);
        model.addAttribute("dons", userDons);
        model.addAttribute("stats", userStats);

        return "admin/utilisateur-details";
    }

    @PostMapping("/utilisateurs/{id}/toggle")
    public String toggleUserStatus(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            userService.toggleUserStatus(id);
            redirectAttributes.addFlashAttribute("success", "Le statut de l'utilisateur a été modifié avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Une erreur est survenue lors de la modification du statut");
        }
        return "redirect:/admin/utilisateurs";
    }

    @PostMapping("/utilisateurs/{id}/role")
    public String updateUserRole(@PathVariable int id, @RequestParam String role, RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserRole(id, role);
            redirectAttributes.addFlashAttribute("success", "Le rôle de l'utilisateur a été mis à jour avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Une erreur est survenue lors de la mise à jour du rôle");
        }
        return "redirect:/admin/utilisateurs";
    }

    @PostMapping("/utilisateurs/{id}/delete")
    public String deleteUser(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "L'utilisateur a été supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Une erreur est survenue lors de la suppression de l'utilisateur");
        }
        return "redirect:/admin/utilisateurs";
    }

    // Classe interne pour les statistiques
    public static class DashboardStats {
        private long organisationsCount;
        private long actionsCount;
        private long usersCount;
        private double totalDonations;

        public DashboardStats(long organisationsCount, long actionsCount, long usersCount, double totalDonations) {
            this.organisationsCount = organisationsCount;
            this.actionsCount = actionsCount;
            this.usersCount = usersCount;
            this.totalDonations = totalDonations;
        }

        public long getOrganisationsCount() {
            return organisationsCount;
        }

        public long getActionsCount() {
            return actionsCount;
        }

        public long getUsersCount() {
            return usersCount;
        }

        public double getTotalDonations() {
            return totalDonations;
        }
    }
}