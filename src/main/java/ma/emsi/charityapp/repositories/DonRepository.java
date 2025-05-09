package ma.emsi.charityapp.repositories;

import ma.emsi.charityapp.entities.Don;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonRepository extends JpaRepository<Don, Integer> {

    // Calcul du total des dons
    @Query("SELECT SUM(d.montant) FROM Don d")
    Double sumMontant();

    // Récupérer les 10 derniers dons
    List<Don> findTop10ByOrderByDateDesc();

    List<Don> findTop5ByOrderByDateDesc();
    List<Don> findByUtilisateurIdOrderByDateDesc(int userId);
    List<Don> findByUtilisateurId(int userId);

    // Méthodes pour les statistiques par organisation
    @Query("SELECT COALESCE(SUM(d.montant), 0) FROM Don d WHERE d.actionCharite.organisation.id = :organisationId")
    double sumMontantByOrganisationId(@Param("organisationId") int organisationId);

    @Query("SELECT COALESCE(AVG(d.montant), 0) FROM Don d WHERE d.actionCharite.organisation.id = :organisationId")
    double averageMontantByOrganisationId(@Param("organisationId") int organisationId);

    @Query("SELECT d FROM Don d WHERE d.actionCharite.organisation.id = :organisationId ORDER BY d.date DESC")
    List<Don> findTop5ByOrganisationIdOrderByDateDonDesc(@Param("organisationId") int organisationId);

    @Query("SELECT d FROM Don d WHERE d.actionCharite.organisation.id = :organisationId")
    List<Don> findByOrganisationId(@Param("organisationId") int organisationId);

    @Query("SELECT d FROM Don d WHERE d.actionCharite.organisation.id = :organisationId")
    Page<Don> findByOrganisationId(@Param("organisationId") int organisationId, Pageable pageable);

    @Query("SELECT d FROM Don d WHERE d.id = :id AND d.actionCharite.organisation.id = :organisationId")
    Optional<Don> findByIdAndOrganisationId(@Param("id") int id, @Param("organisationId") int organisationId);

    @Query("SELECT COUNT(DISTINCT d.utilisateur.id) FROM Don d WHERE d.actionCharite.organisation.id = :organisationId")
    long countDistinctDonateurByOrganisationId(@Param("organisationId") int organisationId);
}