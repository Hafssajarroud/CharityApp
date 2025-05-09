package ma.emsi.charityapp.services.impl;

import ma.emsi.charityapp.dtos.DonDto;
import ma.emsi.charityapp.entities.ActionCharite;
import ma.emsi.charityapp.entities.Don;
import ma.emsi.charityapp.entities.User;
import ma.emsi.charityapp.repositories.ActionChariteRepository;
import ma.emsi.charityapp.repositories.DonRepository;
import ma.emsi.charityapp.repositories.UserRepository;
import ma.emsi.charityapp.services.DonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DonServiceImpl implements DonService {

    private final DonRepository donRepository;
    private final UserRepository userRepository;
    private final ActionChariteRepository actionChariteRepository;

    @Autowired
    public DonServiceImpl(DonRepository donRepository, UserRepository userRepository, ActionChariteRepository actionChariteRepository) {
        this.donRepository = donRepository;
        this.userRepository = userRepository;
        this.actionChariteRepository = actionChariteRepository;
    }

    @Override
    public List<Don> getAllDons() {
        return donRepository.findAll();
    }

    @Override
    @Transactional
    public Don createDon(DonDto donDto) {
        User utilisateur = userRepository.findById(donDto.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        ActionCharite action = actionChariteRepository.findById(donDto.getActionId())
                .orElseThrow(() -> new RuntimeException("Action caritative non trouvée"));

        // Mettre à jour la somme actuelle de l'action
        action.setSommeActuelle(action.getSommeActuelle() + donDto.getMontant());
        actionChariteRepository.save(action);

        Don don = new Don();
        don.setMontant(donDto.getMontant());
        don.setDate(LocalDateTime.now());
        don.setMethodePaiement(donDto.getMethodePaiement());
        don.setUtilisateur(utilisateur);
        don.setActionCharite(action);

        return donRepository.save(don);
    }

    @Override
    public Optional<Don> getDonById(int id) {
        return donRepository.findById(id);
    }

    @Override
    @Transactional
    public void deleteDon(int id) {
        donRepository.deleteById(id);
    }

    @Override
    public double getTotalDonations() {
        return donRepository.sumMontant();
    }

    @Override
    public List<Don> getRecentDons() {
        return donRepository.findTop10ByOrderByDateDesc();
    }

    @Override
    public double getAverageDonation() {
        List<Don> dons = donRepository.findAll();
        if (dons.isEmpty()) {
            return 0.0;
        }
        double total = dons.stream()
                .mapToDouble(Don::getMontant)
                .sum();
        return total / dons.size();
    }

    @Override
    public List<Don> getDonsByUser(int userId) {
        return donRepository.findByUtilisateurIdOrderByDateDesc(userId);
    }

    @Override
    public double getTotalDonationsByUser(int userId) {
        List<Don> userDons = donRepository.findByUtilisateurId(userId);
        return userDons.stream()
                .mapToDouble(Don::getMontant)
                .sum();
    }

    @Override
    public double getTotalDonationsByOrganisation(int organisationId) {
        return donRepository.sumMontantByOrganisationId(organisationId);
    }

    @Override
    public double getAverageDonationByOrganisation(int organisationId) {
        return donRepository.averageMontantByOrganisationId(organisationId);
    }

    @Override
    public List<Don> getDonsByOrganisation(int organisationId) {
        return donRepository.findByOrganisationId(organisationId);
    }

    @Override
    public Page<Don> getDonsByOrganisation(int organisationId, Pageable pageable) {
        return donRepository.findByOrganisationId(organisationId, pageable);
    }

    @Override
    public List<Don> getRecentDonsByOrganisation(int organisationId) {
        return donRepository.findTop5ByOrganisationIdOrderByDateDonDesc(organisationId);
    }

    @Override
    public long countDonateursByOrganisation(int organisationId) {
        return donRepository.countDistinctDonateurByOrganisationId(organisationId);
    }

    @Override
    public Optional<Don> getDonByIdAndOrganisation(int donId, int organisationId) {
        return donRepository.findByIdAndOrganisationId(donId, organisationId);
    }
}