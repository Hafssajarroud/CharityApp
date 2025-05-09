package ma.emsi.charityapp.entities;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 3, max = 100, message = "Le titre doit contenir entre 3 et 100 caractères")
    private String titre;

    @NotBlank(message = "La description est obligatoire")
    @Size(min = 10, max = 1000, message = "La description doit contenir entre 10 et 1000 caractères")
    private String description;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDate dateFin;

    @NotBlank(message = "Le lieu est obligatoire")
    private String lieu;

    @NotNull(message = "L'objectif de collecte est obligatoire")
    @Min(value = 0, message = "L'objectif de collecte doit être positif")
    private double objectifCollecte;

    @NotNull(message = "La somme actuelle est obligatoire")
    @Min(value = 0, message = "La somme actuelle doit être positive")
    private double sommeActuelle = 0.0;

    private boolean archived = false;

    @NotNull(message = "La catégorie est obligatoire")
    @Enumerated(EnumType.STRING)
    private Categorie categorie;

    private String logo;

    @NotNull(message = "L'organisation est obligatoire")
    @ManyToOne
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    @OneToMany(mappedBy = "actionCharite", cascade = CascadeType.ALL)
    private List<Don> dons = new ArrayList<>();

    @OneToMany(mappedBy = "actionCharite", cascade = CascadeType.ALL)
    private List<Participation> participants = new ArrayList<>();

    @Column(name = "date_creation", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
    }

    public enum Categorie {
        EDUCATION,
        SANTE,
        ENVIRONNEMENT,
        AUTRE
    }

    public boolean isActive() {
        return !archived;
    }

    public void setActive(boolean active) {
        this.archived = !active;
    }
}