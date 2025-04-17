package ma.emsi.charityapp.services;

import ma.emsi.charityapp.entities.ActionCharite;

import java.util.List;
import java.util.Optional;

public interface ActionChariteService {
    List<ActionCharite> getAllActions();
    List<ActionCharite> getActionsByCategorie(String categorie);
    Optional<ActionCharite> getActionById(int id);
    ActionCharite saveAction(ActionCharite action);
    void deleteAction(int id);
}
