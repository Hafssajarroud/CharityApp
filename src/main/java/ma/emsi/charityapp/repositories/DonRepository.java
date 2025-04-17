package ma.emsi.charityapp.repositories;

import ma.emsi.charityapp.entities.Don;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonRepository extends JpaRepository<Don, Integer> {
}
