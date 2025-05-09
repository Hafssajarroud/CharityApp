package ma.emsi.charityapp.services;

import ma.emsi.charityapp.dtos.ActionChariteDto;
import ma.emsi.charityapp.entities.ActionCharite;
import ma.emsi.charityapp.entities.Organisation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ActionChariteService {
    List<ActionCharite> getAllActions();
    Page<ActionCharite> getAllActions(Pageable pageable);
    List<ActionCharite> getActionsByCategorie(String categorie);
    Page<ActionCharite> getActionsByCategorie(String categorie, Pageable pageable);
    Optional<ActionCharite> getActionById(int id);
    long countAllActions();
    List<ActionCharite> getRecentActions();
    void createAction(ActionChariteDto actionDto);
    ActionCharite updateAction(int id, ActionChariteDto actionDto);
    void deleteAction(int id);
    ActionCharite saveAction(ActionCharite action);
    void archiveAction(int id);
    String saveLogo(MultipartFile logo);
    List<ActionCharite> getActiveActions();
    boolean isActive(int id);
    void archiveAction(ActionCharite action);

    // Méthodes pour les statistiques
    long countActionsByOrganisation(int organisationId);
    long countActiveActionsByOrganisation(int organisationId);
    List<ActionCharite> getRecentActionsByOrganisation(int organisationId);
    List<ActionCharite> getActionsByOrganisation(int organisationId);
    Page<ActionCharite> getActionsByOrganisation(int organisationId, Pageable pageable);
    Optional<ActionCharite> getActionByIdAndOrganisation(int actionId, int organisationId);
    void archiveAction(int actionId, int organisationId);
    void deleteAction(int actionId, int organisationId);
}