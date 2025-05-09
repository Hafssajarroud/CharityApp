package ma.emsi.charityapp.repositories;
import ma.emsi.charityapp.entities.Organisation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganisationRepository extends JpaRepository<Organisation, Integer> {
    Page<Organisation> findByStatutValidation(boolean isValid, Pageable pageable);
    List<Organisation> findTop10ByOrderByCreatedAtDesc();
    List<Organisation> findByAdministrateurId(int adminId);
    boolean existsByIdAndAdministrateurId(int id, int adminId);

    @Modifying
    @Query("UPDATE Organisation o SET o.statutValidation = :status WHERE o.id = :id")
    void updateValidationStatus(@Param("id") Integer id, @Param("status") boolean status);
}