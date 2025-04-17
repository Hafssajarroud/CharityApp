package ma.emsi.charityapp.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String message;
    private LocalDateTime dateEnvoi;
    private boolean etat; // true = lu, false = non lu

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private User utilisateur;
}
