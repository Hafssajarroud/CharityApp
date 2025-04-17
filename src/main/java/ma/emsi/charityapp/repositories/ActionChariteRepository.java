package ma.emsi.charityapp.repositories;

import ma.emsi.charityapp.entities.ActionCharite;
import ma.emsi.charityapp.entities.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActionChariteRepository extends JpaRepository<ActionCharite, Integer> {
    List<ActionCharite> findByCategorie(Categorie categorie);
}
