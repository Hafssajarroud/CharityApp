package ma.emsi.charityapp.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.emsi.charityapp.dtos.OrganisationDto;
import ma.emsi.charityapp.entities.Organisation;
import ma.emsi.charityapp.entities.User;
import ma.emsi.charityapp.entities.Role;
import ma.emsi.charityapp.repositories.UserRepository;
import ma.emsi.charityapp.services.OrganisationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/organisation")
@RequiredArgsConstructor
public class OrganisationController {

    private final OrganisationService organisationService;
    private final UserRepository userRepository;

    @GetMapping
    public String listOrganisations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false) String status,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Organisation> organisationsPage;

        if (status != null && !status.isEmpty()) {
            boolean isValid = status.equals("valid");
            organisationsPage = organisationService.getOrganisationsByStatus(isValid, pageable);
        } else {
            organisationsPage = organisationService.getAllOrganisations(pageable);
        }

        model.addAttribute("organisationsPage", organisationsPage);
        return "organisation";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("organisation", new OrganisationDto());
        return "organisation-form";
    }

    @PostMapping("/new")
    public String createOrganisation(@Valid @ModelAttribute("organisation") OrganisationDto organisationDto,
                                     BindingResult result,
                                     Authentication authentication) throws IOException {
        System.out.println("=== Début de la création d'organisation ===");
        System.out.println("Organisation DTO: " + organisationDto);

        if (result.hasErrors()) {
            System.out.println("Erreurs de validation: " + result.getAllErrors());
            return "organisation-form";
        }

        try {
            // Récupérer l'utilisateur connecté
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Créer l'organisation
            Organisation organisation = new Organisation();
            organisation.setNom(organisationDto.getNom());
            organisation.setAdresse(organisationDto.getAdresse());
            organisation.setNumeroFiscal(organisationDto.getNumeroFiscal());
            organisation.setContactPrincipal(organisationDto.getContactPrincipal());
            organisation.setTelephone(organisationDto.getTelephone());
            organisation.setDescription(organisationDto.getDescription());
            organisation.setAdministrateur(user);

            System.out.println("Création de l'organisation: " + organisation);

            if (organisationDto.getLogo() != null && !organisationDto.getLogo().isEmpty()) {
                String logoPath = organisationService.saveLogo(organisationDto.getLogo());
                organisation.setLogo(logoPath);
                System.out.println("Logo sauvegardé: " + logoPath);
            }

            // Sauvegarder l'organisation
            organisation = organisationService.createOrganisation(organisation, user);
            System.out.println("Organisation sauvegardée avec l'ID: " + organisation.getId());

            System.out.println("=== Création d'organisation terminée avec succès ===");
            return "redirect:/organisation/dashboard";
        } catch (Exception e) {
            System.out.println("ERREUR lors de la création de l'organisation: " + e.getMessage());
            e.printStackTrace();
            return "organisation-form";
        }
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ROLE_ORGANISATION')")
    public String showEditForm(@PathVariable int id, Model model) {
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
        return "organisation-form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ORGANISATION')")
    public String updateOrganisation(@PathVariable int id,
                                     @Valid @ModelAttribute("organisation") OrganisationDto organisationDto,
                                     BindingResult result) throws IOException {
        if (result.hasErrors()) {
            return "organisation-form";
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
        return "redirect:/organisation";
    }

    @PostMapping("/{id}/validate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String validateOrganisation(@PathVariable int id) {
        organisationService.validateOrganisation(id);
        return "redirect:/organisation";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteOrganisation(@PathVariable int id) {
        organisationService.deleteOrganisation(id);
        return "redirect:/organisation";
    }
}
