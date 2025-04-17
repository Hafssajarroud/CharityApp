package ma.emsi.charityapp.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActionCharite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String titre;
    private String description;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String lieu;
    private double objectifCollecte;
    private double sommeActuelle;

    @Enumerated(EnumType.STRING)
    private Categorie categorie;

    @ElementCollection
    private List<String> media = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    @OneToMany(mappedBy = "actionCharite", cascade = CascadeType.ALL)
    private List<Don> dons;

    @OneToMany(mappedBy = "actionCharite", cascade = CascadeType.ALL)
    private List<Participation> participants;
}
