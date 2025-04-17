package ma.emsi.charityapp.entities;
import jakarta.persistence.*;
import lombok.*;

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

    private double montant;
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    private MethodePaiement methodePaiement;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private User utilisateur;

    @ManyToOne
    @JoinColumn(name = "action_id")
    private ActionCharite actionCharite;
}

enum MethodePaiement {
    PAYPAL, STRIPE
}

