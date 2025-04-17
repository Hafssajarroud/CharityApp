package ma.emsi.charityapp.services;

import ma.emsi.charityapp.entities.Organisation;
import ma.emsi.charityapp.repositories.OrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrganisationService {

    private final OrganisationRepository organisationRepository;

    @Autowired
    public OrganisationService(OrganisationRepository organisationRepository) {
        this.organisationRepository = organisationRepository;
    }

    public List<Organisation> getAllOrganisations() {
        return organisationRepository.findAll();
    }

    public Optional<Organisation> getOrganisationById(int id) {
        return organisationRepository.findById(id);
    }

    public Organisation saveOrganisation(Organisation organisation) {
        return organisationRepository.save(organisation);
    }

    public void deleteOrganisation(int id) {
        organisationRepository.deleteById(id);
    }
}
