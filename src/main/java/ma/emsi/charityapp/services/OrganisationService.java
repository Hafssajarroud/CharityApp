package ma.emsi.charityapp.services;

import ma.emsi.charityapp.entities.Organisation;
import ma.emsi.charityapp.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface OrganisationService {
    Organisation createOrganisation(Organisation organisation, User admin);
    Page<Organisation> getAllOrganisations(Pageable pageable);
    Page<Organisation> getOrganisationsByStatus(boolean isValid, Pageable pageable);
    Optional<Organisation> getOrganisationById(int id);
    Optional<Organisation> getOrganisationByAdminId(int adminId);
    Organisation updateOrganisation(int id, Organisation organisationDetails);
    void deleteOrganisation(int id);
    void validateOrganisation(int id);
    String saveLogo(MultipartFile file) throws IOException;
    List<Organisation> getAllOrganisations();
    long countAllOrganisations();
    List<Organisation> getRecentOrganisations();
    boolean isOrganisationAdmin(int organisationId, int userId);
}
