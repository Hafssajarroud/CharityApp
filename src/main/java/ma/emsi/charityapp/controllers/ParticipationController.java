package ma.emsi.charityapp.controllers;

import ma.emsi.charityapp.entities.Participation;
import ma.emsi.charityapp.services.ParticipationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/participations")
@CrossOrigin("*")
public class ParticipationController {

    private final ParticipationService participationService;

    public ParticipationController(ParticipationService participationService) {
        this.participationService = participationService;
    }

    @GetMapping
    public List<Participation> getAllParticipations() {
        return participationService.getAllParticipations();
    }

    @PostMapping
    public Participation createParticipation(@RequestBody Participation participation) {
        return participationService.createParticipation(participation);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Participation> getParticipationById(@PathVariable int id) {
        Optional<Participation> participation = participationService.getParticipationById(id);
        return participation.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParticipation(@PathVariable int id) {
        if (participationService.getParticipationById(id).isPresent()) {
            participationService.deleteParticipation(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
