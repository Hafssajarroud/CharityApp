package ma.emsi.charityapp.repositories;

import ma.emsi.charityapp.entities.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Integer> {
    List<Participation> findByUtilisateurId(int userId);
    boolean existsByUtilisateurIdAndActionChariteId(int userId, int actionId);
}
