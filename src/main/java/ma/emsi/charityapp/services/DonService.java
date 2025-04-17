package ma.emsi.charityapp.services;

import ma.emsi.charityapp.entities.Don;
import ma.emsi.charityapp.repositories.DonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DonService {

    private final DonRepository donRepository;

    @Autowired
    public DonService(DonRepository donRepository) {
        this.donRepository = donRepository;
    }

    public List<Don> getAllDons() {
        return donRepository.findAll();
    }

    public Don createDon(Don don) {
        return donRepository.save(don);
    }

    public Optional<Don> getDonById(int id) {
        return donRepository.findById(id);
    }

    public void deleteDon(int id) {
        donRepository.deleteById(id);
    }
}
