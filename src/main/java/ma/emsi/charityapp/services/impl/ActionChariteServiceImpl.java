package ma.emsi.charityapp.services.impl;

import ma.emsi.charityapp.dtos.ActionChariteDto;
import ma.emsi.charityapp.entities.ActionCharite;
import ma.emsi.charityapp.entities.Organisation;
import ma.emsi.charityapp.repositories.ActionChariteRepository;
import ma.emsi.charityapp.repositories.OrganisationRepository;
import ma.emsi.charityapp.services.ActionChariteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ActionChariteServiceImpl implements ActionChariteService {
    private final ActionChariteRepository actionChariteRepository;
    private final OrganisationRepository organisationRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public ActionChariteServiceImpl(ActionChariteRepository actionChariteRepository,
                                    OrganisationRepository organisationRepository) {
        this.actionChariteRepository = actionChariteRepository;
        this.organisationRepository = organisationRepository;
    }

    @Override
    public List<ActionCharite> getAllActions() {
        return actionChariteRepository.findAll();
    }

    @Override
    public Page<ActionCharite> getAllActions(Pageable pageable) {
        return actionChariteRepository.findAll(pageable);
    }

    @Override
    public List<ActionCharite> getActionsByCategorie(String categorie) {
        try {
            ActionCharite.Categorie cat = ActionCharite.Categorie.valueOf(categorie.toUpperCase());
            return actionChariteRepository.findByCategorie(cat);
        } catch (IllegalArgumentException e) {
            return actionChariteRepository.findAll();
        }
    }

    @Override
    public Page<ActionCharite> getActionsByCategorie(String categorie, Pageable pageable) {
        try {
            ActionCharite.Categorie cat = ActionCharite.Categorie.valueOf(categorie.toUpperCase());
            return actionChariteRepository.findByCategorie(cat, pageable);
        } catch (IllegalArgumentException e) {
            return actionChariteRepository.findAll(pageable);
        }
    }

    @Override
    public Optional<ActionCharite> getActionById(int id) {
        return actionChariteRepository.findById(id);
    }

    @Override
    public long countAllActions() {
        return actionChariteRepository.count();
    }

    @Override
    public List<ActionCharite> getRecentActions() {
        return actionChariteRepository.findTop5ByOrderByDateDebutDesc(PageRequest.of(0, 5)).getContent();
    }

    @Override
    public void createAction(ActionChariteDto actionDto) {
        System.out.println("Service: Début de la création d'action");
        System.out.println("Service: Action DTO reçu: " + actionDto);

        if (actionDto == null) {
            throw new IllegalArgumentException("L'action ne peut pas être null");
        }

        ActionCharite action = new ActionCharite();
        System.out.println("Service: Nouvelle action créée");

        // Vérifier et définir les champs obligatoires
        if (actionDto.getTitre() == null || actionDto.getTitre().trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre est obligatoire");
        }
        action.setTitre(actionDto.getTitre());

        if (actionDto.getDescription() == null || actionDto.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("La description est obligatoire");
        }
        action.setDescription(actionDto.getDescription());

        if (actionDto.getDateDebut() == null) {
            throw new IllegalArgumentException("La date de début est obligatoire");
        }
        action.setDateDebut(actionDto.getDateDebut());

        if (actionDto.getDateFin() == null) {
            throw new IllegalArgumentException("La date de fin est obligatoire");
        }
        action.setDateFin(actionDto.getDateFin());

        if (actionDto.getLieu() == null || actionDto.getLieu().trim().isEmpty()) {
            throw new IllegalArgumentException("Le lieu est obligatoire");
        }
        action.setLieu(actionDto.getLieu());

        if (actionDto.getObjectifCollecte() == null || actionDto.getObjectifCollecte() <= 0) {
            throw new IllegalArgumentException("L'objectif de collecte doit être supérieur à 0");
        }
        action.setObjectifCollecte(actionDto.getObjectifCollecte());

        if (actionDto.getSommeActuelle() == null || actionDto.getSommeActuelle() < 0) {
            throw new IllegalArgumentException("La somme actuelle ne peut pas être négative");
        }
        action.setSommeActuelle(actionDto.getSommeActuelle());

        if (actionDto.getCategorie() == null) {
            throw new IllegalArgumentException("La catégorie est obligatoire");
        }
        action.setCategorie(actionDto.getCategorie());

        // Gérer l'organisation
        if (actionDto.getOrganisationId() == null) {
            throw new IllegalArgumentException("L'organisation est obligatoire");
        }
        Organisation organisation = organisationRepository.findById(actionDto.getOrganisationId())
                .orElseThrow(() -> new IllegalArgumentException("L'organisation spécifiée n'existe pas"));
        action.setOrganisation(organisation);

        // Gérer le logo
        if (actionDto.getLogo() != null && !actionDto.getLogo().trim().isEmpty()) {
            action.setLogo(actionDto.getLogo());
            System.out.println("Service: Logo défini: " + actionDto.getLogo());
        }

        System.out.println("Service: Tentative de sauvegarde de l'action");
        actionChariteRepository.save(action);
        System.out.println("Service: Action sauvegardée avec succès");
    }

    public ActionCharite createActionAndReturn(ActionChariteDto actionDto) {
        ActionCharite action = new ActionCharite();
        updateActionFromDto(action, actionDto);

        // Vérifier si le logo est fourni
        if (actionDto.getLogo() != null && !actionDto.getLogo().trim().isEmpty()) {
            action.setLogo(actionDto.getLogo());
        }

        return actionChariteRepository.save(action);
    }

    @Override
    public ActionCharite updateAction(int id, ActionChariteDto actionDto) {
        ActionCharite action = actionChariteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Action not found"));
        updateActionFromDto(action, actionDto);
        return actionChariteRepository.save(action);
    }

    @Override
    public void deleteAction(int id) {
        actionChariteRepository.deleteById(id);
    }

    @Override
    public ActionCharite saveAction(ActionCharite action) {
        return actionChariteRepository.save(action);
    }

    @Override
    public void archiveAction(int id) {
        ActionCharite action = actionChariteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Action non trouvée"));
        action.setArchived(!action.isArchived());
        actionChariteRepository.save(action);
    }

    public String saveLogo(MultipartFile logo) {
        try {
            // Nettoyer le nom du fichier et le garder tel quel
            String filename = StringUtils.cleanPath(logo.getOriginalFilename());

            // Créer le répertoire uploads/organisations s'il n'existe pas
            Path uploadPath = Paths.get("uploads/organisations").toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(filename);
            Files.copy(logo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store logo", e);
        }
    }

    private void updateActionFromDto(ActionCharite action, ActionChariteDto dto) {
        if (dto.getTitre() == null || dto.getTitre().trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre est obligatoire");
        }
        action.setTitre(dto.getTitre());

        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("La description est obligatoire");
        }
        action.setDescription(dto.getDescription());

        if (dto.getDateDebut() == null) {
            throw new IllegalArgumentException("La date de début est obligatoire");
        }
        action.setDateDebut(dto.getDateDebut());

        if (dto.getDateFin() == null) {
            throw new IllegalArgumentException("La date de fin est obligatoire");
        }
        action.setDateFin(dto.getDateFin());

        if (dto.getLieu() == null || dto.getLieu().trim().isEmpty()) {
            throw new IllegalArgumentException("Le lieu est obligatoire");
        }
        action.setLieu(dto.getLieu());

        if (dto.getObjectifCollecte() <= 0) {
            throw new IllegalArgumentException("L'objectif de collecte doit être supérieur à 0");
        }
        action.setObjectifCollecte(dto.getObjectifCollecte());

        if (dto.getSommeActuelle() < 0) {
            throw new IllegalArgumentException("La somme actuelle ne peut pas être négative");
        }
        action.setSommeActuelle(dto.getSommeActuelle());

        if (dto.getCategorie() == null) {
            throw new IllegalArgumentException("La catégorie est obligatoire");
        }
        action.setCategorie(dto.getCategorie());

        // Gérer l'organisation
        if (dto.getOrganisationId() == null) {
            throw new IllegalArgumentException("L'organisation est obligatoire");
        }
        Organisation organisation = organisationRepository.findById(dto.getOrganisationId())
                .orElseThrow(() -> new IllegalArgumentException("L'organisation spécifiée n'existe pas"));
        action.setOrganisation(organisation);
    }

    @Override
    public List<ActionCharite> getActiveActions() {
        return actionChariteRepository.findByArchivedFalse();
    }

    @Override
    public boolean isActive(int id) {
        return actionChariteRepository.existsByIdAndArchivedFalse(id);
    }

    @Override
    public void archiveAction(ActionCharite action) {
        action.setArchived(true);
        actionChariteRepository.save(action);
    }

    @Override
    public long countActionsByOrganisation(int organisationId) {
        return actionChariteRepository.countByOrganisationId(organisationId);
    }

    @Override
    public long countActiveActionsByOrganisation(int organisationId) {
        return actionChariteRepository.countByOrganisationIdAndArchivedFalse(organisationId);
    }

    @Override
    public List<ActionCharite> getRecentActionsByOrganisation(int organisationId) {
        return actionChariteRepository.findTop5ByOrganisationIdOrderByDateCreationDesc(organisationId);
    }

    @Override
    public List<ActionCharite> getActionsByOrganisation(int organisationId) {
        return actionChariteRepository.findByOrganisationId(organisationId);
    }

    @Override
    public Page<ActionCharite> getActionsByOrganisation(int organisationId, Pageable pageable) {
        return actionChariteRepository.findByOrganisationId(organisationId, pageable);
    }

    @Override
    public Optional<ActionCharite> getActionByIdAndOrganisation(int actionId, int organisationId) {
        return actionChariteRepository.findByIdAndOrganisationId(actionId, organisationId);
    }

    @Override
    public void archiveAction(int actionId, int organisationId) {
        ActionCharite action = getActionByIdAndOrganisation(actionId, organisationId)
                .orElseThrow(() -> new RuntimeException("Action non trouvée"));
        action.setArchived(!action.isArchived());
        actionChariteRepository.save(action);
    }

    @Override
    public void deleteAction(int actionId, int organisationId) {
        ActionCharite action = getActionByIdAndOrganisation(actionId, organisationId)
                .orElseThrow(() -> new RuntimeException("Action non trouvée"));
        actionChariteRepository.delete(action);
    }
}
