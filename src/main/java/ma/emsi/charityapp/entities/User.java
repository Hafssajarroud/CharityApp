package ma.emsi.charityapp.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nom;
    private String email;
    private String motDePasse;
    private String telephone;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<Don> historiqueDons;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<Participation> participations;
}

enum Role {
    ADMIN, ORGANISATION, DONATEUR
}

