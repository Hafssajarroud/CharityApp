package ma.emsi.charityapp.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Organisation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Le nom de l'organisation est obligatoire")
    @Size(min = 3, max = 100, message = "Le nom doit contenir entre 3 et 100 caractères")
    private String nom;

    @NotBlank(message = "L'adresse est obligatoire")
    private String adresse;

    @NotBlank(message = "Le numéro fiscal est obligatoire")
    @Size(min = 8, max = 20, message = "Le numéro fiscal doit contenir entre 8 et 20 caractères")
    private String numeroFiscal;

    @NotBlank(message = "Le contact principal est obligatoire")
    private String contactPrincipal;

    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    private String telephone;

    private String logo;

    @Column(length = 1000)
    private String description;

    private boolean statutValidation = false;

    // Utilisation de LocalDateTime et ajout d'un 'default' SQL pour la valeur actuelle
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User administrateur;

    @OneToMany(mappedBy = "organisation", cascade = CascadeType.ALL)
    private List<ActionCharite> actions = new ArrayList<>();
}
