package ma.emsi.charityapp.services.impl;

import ma.emsi.charityapp.entities.ActionCharite;
import ma.emsi.charityapp.entities.Categorie;
import ma.emsi.charityapp.repositories.ActionChariteRepository;
import ma.emsi.charityapp.services.ActionChariteService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActionChariteServiceImpl implements ActionChariteService {

    private final ActionChariteRepository actionChariteRepository;

    public ActionChariteServiceImpl(ActionChariteRepository actionChariteRepository) {
        this.actionChariteRepository = actionChariteRepository;
    }

    @Override
    public List<ActionCharite> getAllActions() {
        return actionChariteRepository.findAll();
    }

    @Override
    public List<ActionCharite> getActionsByCategorie(String categorie) {
        try {
            Categorie cat = Categorie.valueOf(categorie.toUpperCase());
            return actionChariteRepository.findByCategorie(cat);
        } catch (IllegalArgumentException e) {
            return actionChariteRepository.findAll(); // En cas d'erreur : retourne tout
        }
    }

    @Override
    public Optional<ActionCharite> getActionById(int id) {
        return actionChariteRepository.findById(id);
    }

    @Override
    public ActionCharite saveAction(ActionCharite action) {
        return actionChariteRepository.save(action);
    }

    @Override
    public void deleteAction(int id) {
        actionChariteRepository.deleteById(id);
    }
}
