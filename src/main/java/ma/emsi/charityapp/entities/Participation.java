package ma.emsi.charityapp.entities;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDateTime dateParticipation;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private User utilisateur;

    @ManyToOne
    @JoinColumn(name = "action_id")
    private ActionCharite actionCharite;
}
