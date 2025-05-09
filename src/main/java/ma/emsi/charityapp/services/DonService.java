package ma.emsi.charityapp.services;

import ma.emsi.charityapp.dtos.DonDto;
import ma.emsi.charityapp.entities.Don;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface DonService {
    List<Don> getAllDons();
    Don createDon(DonDto donDto);
    Optional<Don> getDonById(int id);
    void deleteDon(int id);
    double getTotalDonations();
    List<Don> getRecentDons();
    double getAverageDonation();
    List<Don> getDonsByUser(int userId);
    double getTotalDonationsByUser(int userId);
    double getTotalDonationsByOrganisation(int organisationId);
    double getAverageDonationByOrganisation(int organisationId);
    List<Don> getDonsByOrganisation(int organisationId);
    Page<Don> getDonsByOrganisation(int organisationId, Pageable pageable);
    List<Don> getRecentDonsByOrganisation(int organisationId);
    long countDonateursByOrganisation(int organisationId);
    Optional<Don> getDonByIdAndOrganisation(int donId, int organisationId);
}
