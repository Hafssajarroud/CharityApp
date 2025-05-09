package ma.emsi.charityapp.controllers.organisation;

import lombok.RequiredArgsConstructor;
import ma.emsi.charityapp.dtos.ActionChariteDto;
import ma.emsi.charityapp.entities.ActionCharite;
import ma.emsi.charityapp.entities.Organisation;
import ma.emsi.charityapp.entities.User;
import ma.emsi.charityapp.services.ActionChariteService;
import ma.emsi.charityapp.services.OrganisationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/organisation/dashboard/actions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ORGANISATION')")
@org.springframework.context.annotation.Primary
public class ActionChariteController {

    private final ActionChariteService actionChariteService;
    private final OrganisationService organisationService;

    @GetMapping("/new")
    public String showNewActionForm(Authentication authentication, Model model) {
        User user = (User) authentication.getPrincipal();
        Organisation organisation = organisationService.getOrganisationByAdminId(user.getId())
                .orElseThrow(() -> new RuntimeException("Organisation non trouvée"));

        ActionChariteDto actionDto = new ActionChariteDto();
        actionDto.setOrganisationId(organisation.getId());
        model.addAttribute("action", actionDto);
        model.addAttribute("organisation", organisation);
        return "organisation/actions/new";
    }

    @PostMapping
    public String createAction(@ModelAttribute ActionChariteDto actionDto,
                               @RequestParam(required = false) MultipartFile logoFile,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            User user = (User) authentication.getPrincipal();
            Organisation organisation = organisationService.getOrganisationByAdminId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Organisation non trouvée"));

            actionDto.setOrganisationId(organisation.getId());

            // Gérer le logo si un fichier est fourni
            if (logoFile != null && !logoFile.isEmpty()) {
                String logoPath = actionChariteService.saveLogo(logoFile);
                actionDto.setLogo(logoPath);
            }

            actionChariteService.createAction(actionDto);

            redirectAttributes.addFlashAttribute("success", "Action créée avec succès");
            return "redirect:/organisation/dashboard/actions";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création de l'action: " + e.getMessage());
            return "redirect:/organisation/dashboard/actions/new";
        }
    }

    @GetMapping("/{id}")
    public String showAction(@PathVariable int id, Authentication authentication, Model model) {
        ActionCharite action = actionChariteService.getActionById(id)
                .orElseThrow(() -> new RuntimeException("Action non trouvée"));

        // Récupérer l'organisation associée à l'utilisateur connecté
        User user = (User) authentication.getPrincipal();
        Organisation organisation = organisationService.getOrganisationByAdminId(user.getId())
                .orElseThrow(() -> new RuntimeException("Organisation non trouvée"));

        // Vérifier si l'utilisateur est l'organisation propriétaire de l'action
        if (action.getOrganisation().getId() != organisation.getId()) {
            return "redirect:/organisation/actions";
        }

        model.addAttribute("action", action);
        return "organisation/actions/details";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable int id, Authentication authentication, Model model) {
        User user = (User) authentication.getPrincipal();
        Organisation organisation = organisationService.getOrganisationByAdminId(user.getId())
                .orElseThrow(() -> new RuntimeException("Organisation non trouvée"));

        ActionCharite action = actionChariteService.getActionByIdAndOrganisation(id, organisation.getId())
                .orElseThrow(() -> new RuntimeException("Action non trouvée"));

        ActionChariteDto actionDto = convertToDto(action);
        model.addAttribute("action", actionDto);
        model.addAttribute("organisation", organisation);
        return "organisation/actions/edit";
    }

    @PostMapping("/{id}")
    public String updateAction(@PathVariable int id,
                               @ModelAttribute ActionChariteDto actionDto,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            User user = (User) authentication.getPrincipal();
            Organisation organisation = organisationService.getOrganisationByAdminId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Organisation non trouvée"));

            actionDto.setOrganisationId(organisation.getId());
            actionChariteService.updateAction(id, actionDto);
            redirectAttributes.addFlashAttribute("success", "Action mise à jour avec succès");
            return "redirect:/organisation/dashboard/actions";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour de l'action: " + e.getMessage());
            return "redirect:/organisation/dashboard/actions/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/archive")
    public String archiveAction(@PathVariable int id,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            User user = (User) authentication.getPrincipal();
            Organisation organisation = organisationService.getOrganisationByAdminId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Organisation non trouvée"));

            actionChariteService.archiveAction(id, organisation.getId());
            redirectAttributes.addFlashAttribute("success", "Action archivée avec succès");
            return "redirect:/organisation/dashboard/actions";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'archivage de l'action: " + e.getMessage());
            return "redirect:/organisation/dashboard/actions";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteAction(@PathVariable int id,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            User user = (User) authentication.getPrincipal();
            Organisation organisation = organisationService.getOrganisationByAdminId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Organisation non trouvée"));

            actionChariteService.deleteAction(id, organisation.getId());
            redirectAttributes.addFlashAttribute("success", "Action supprimée avec succès");
            return "redirect:/organisation/dashboard/actions";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression de l'action: " + e.getMessage());
            return "redirect:/organisation/dashboard/actions";
        }
    }

    private ActionChariteDto convertToDto(ActionCharite action) {
        ActionChariteDto dto = new ActionChariteDto();
        dto.setId(action.getId());
        dto.setTitre(action.getTitre());
        dto.setDescription(action.getDescription());
        dto.setDateDebut(action.getDateDebut());
        dto.setDateFin(action.getDateFin());
        dto.setLieu(action.getLieu());
        dto.setObjectifCollecte(action.getObjectifCollecte());
        dto.setSommeActuelle(action.getSommeActuelle());
        dto.setOrganisationId(action.getOrganisation().getId());
        dto.setCategorie(action.getCategorie());
        dto.setLogo(action.getLogo());
        return dto;
    }
}