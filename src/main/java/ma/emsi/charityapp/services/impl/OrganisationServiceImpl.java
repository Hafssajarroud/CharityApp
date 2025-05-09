package ma.emsi.charityapp.services.impl;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import ma.emsi.charityapp.entities.Organisation;
import ma.emsi.charityapp.entities.User;
import ma.emsi.charityapp.repositories.OrganisationRepository;
import ma.emsi.charityapp.repositories.UserRepository;
import ma.emsi.charityapp.services.OrganisationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrganisationServiceImpl implements OrganisationService {

    private final OrganisationRepository organisationRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;
    private final String uploadDir = "uploads/organisations";

    @Override
    @Transactional
    public Organisation createOrganisation(Organisation organisation, User admin) {
        // Configurer l'organisation
        organisation.setAdministrateur(admin);
        organisation.setStatutValidation(true); // Valider automatiquement l'organisation

        // Sauvegarder l'organisation
        return organisationRepository.save(organisation);
    }

    @Override
    public Page<Organisation> getAllOrganisations(Pageable pageable) {
        return organisationRepository.findAll(pageable);
    }

    @Override
    public Page<Organisation> getOrganisationsByStatus(boolean isValid, Pageable pageable) {
        return organisationRepository.findByStatutValidation(isValid, pageable);
    }

    @Override
    public Optional<Organisation> getOrganisationById(int id) {
        return organisationRepository.findById(id);
    }

    @Override
    @Transactional
    public Organisation updateOrganisation(int id, Organisation organisationDetails) {
        Organisation organisation = organisationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organisation non trouvée"));

        organisation.setNom(organisationDetails.getNom());
        organisation.setAdresse(organisationDetails.getAdresse());
        organisation.setNumeroFiscal(organisationDetails.getNumeroFiscal());
        organisation.setContactPrincipal(organisationDetails.getContactPrincipal());
        organisation.setTelephone(organisationDetails.getTelephone());
        organisation.setDescription(organisationDetails.getDescription());

        return organisationRepository.save(organisation);
    }

    @Override
    @Transactional
    public void deleteOrganisation(int id) {
        organisationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void validateOrganisation(int id) {
        System.out.println("=== Début de la validation de l'organisation ===");
        System.out.println("ID de l'organisation à valider: " + id);

        try {
            // Vérifier si l'organisation existe
            if (!organisationRepository.existsById(id)) {
                System.out.println("ERREUR: Organisation non trouvée avec l'ID: " + id);
                throw new RuntimeException("Organisation non trouvée");
            }

            // Mettre à jour directement le statut avec une requête native
            organisationRepository.updateValidationStatus(id, true);

            System.out.println("=== Validation terminée avec succès ===");
        } catch (Exception e) {
            System.out.println("ERREUR lors de la validation: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public String saveLogo(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Créer le dossier s'il n'existe pas
        Path uploadDir = Paths.get("uploads/organisations");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // Utiliser le nom original du fichier
        String originalFilename = file.getOriginalFilename();
        Path filePath = uploadDir.resolve(originalFilename);

        // Sauvegarder le fichier
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Retourner uniquement le nom du fichier
        return originalFilename;
    }

    @Override
    public List<Organisation> getAllOrganisations() {
        return organisationRepository.findAll();
    }

    @Override
    public long countAllOrganisations() {
        return organisationRepository.count();
    }

    @Override
    public List<Organisation> getRecentOrganisations() {
        return organisationRepository.findTop10ByOrderByCreatedAtDesc();
    }

    @Override
    public Optional<Organisation> getOrganisationByAdminId(int adminId) {
        List<Organisation> organisations = organisationRepository.findByAdministrateurId(adminId);
        return organisations.isEmpty() ? Optional.empty() : Optional.of(organisations.get(0));
    }

    @Override
    public boolean isOrganisationAdmin(int organisationId, int userId) {
        return organisationRepository.existsByIdAndAdministrateurId(organisationId, userId);
    }
}