package ma.emsi.charityapp.entities;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;


import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Don {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    @Min(1)
    private double montant;
    @NotNull
    private LocalDateTime date;

    @NotNull

    @Enumerated(EnumType.STRING)
    private MethodePaiement methodePaiement;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private User utilisateur;

    @ManyToOne
    @JoinColumn(name = "action_id")
    private ActionCharite actionCharite;
}

