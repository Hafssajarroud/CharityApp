package ma.emsi.charityapp.repositories;

import ma.emsi.charityapp.entities.ActionCharite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActionChariteRepository extends JpaRepository<ActionCharite, Integer> {
    List<ActionCharite> findByCategorie(ActionCharite.Categorie categorie);
    Page<ActionCharite> findByCategorie(ActionCharite.Categorie categorie, Pageable pageable);
    Page<ActionCharite> findTop5ByOrderByDateDebutDesc(Pageable pageable);
    long countByArchivedFalse();
    long countByArchivedTrue();
    List<ActionCharite> findByArchivedFalse();
    boolean existsByIdAndArchivedFalse(int id);

    // Méthodes pour les statistiques par organisation
    long countByOrganisationId(int organisationId);
    long countByOrganisationIdAndArchivedFalse(int organisationId);
    List<ActionCharite> findTop5ByOrganisationIdOrderByDateCreationDesc(int organisationId);
    List<ActionCharite> findByOrganisationId(int organisationId);
    Page<ActionCharite> findByOrganisationId(int organisationId, Pageable pageable);
    Optional<ActionCharite> findByIdAndOrganisationId(int id, int organisationId);
}