package ma.emsi.charityapp.services;

import ma.emsi.charityapp.entities.Participation;
import ma.emsi.charityapp.repositories.ParticipationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParticipationService {

    private final ParticipationRepository participationRepository;

    @Autowired
    public ParticipationService(ParticipationRepository participationRepository) {
        this.participationRepository = participationRepository;
    }

    public List<Participation> getAllParticipations() {
        return participationRepository.findAll();
    }

    public Participation createParticipation(Participation participation) {
        return participationRepository.save(participation);
    }

    public Optional<Participation> getParticipationById(int id) {
        return participationRepository.findById(id);
    }

    public void deleteParticipation(int id) {
        participationRepository.deleteById(id);
    }

    public List<Participation> getParticipationsByUser(int userId) {
        return participationRepository.findByUtilisateurId(userId);
    }

    public boolean hasUserParticipated(int userId, int actionId) {
        return participationRepository.existsByUtilisateurIdAndActionChariteId(userId, actionId);
    }
}
