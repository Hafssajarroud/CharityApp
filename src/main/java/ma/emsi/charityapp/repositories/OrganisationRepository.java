package ma.emsi.charityapp.repositories;
import ma.emsi.charityapp.entities.Organisation;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganisationRepository extends JpaRepository<Organisation, Integer> {
}