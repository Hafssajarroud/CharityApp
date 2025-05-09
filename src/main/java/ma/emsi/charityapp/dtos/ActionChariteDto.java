package ma.emsi.charityapp.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;
import ma.emsi.charityapp.entities.ActionCharite;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class ActionChariteDto {
    private Integer id;

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDate dateFin;

    @NotBlank(message = "Le lieu est obligatoire")
    private String lieu;

    @NotNull(message = "L'objectif de collecte est obligatoire")
    @Min(value = 1, message = "L'objectif de collecte doit être supérieur à 0")
    private Double objectifCollecte;

    @Min(value = 0, message = "La somme actuelle ne peut pas être négative")
    private Double sommeActuelle = 0.0;

    @NotNull(message = "L'organisation est obligatoire")
    private Integer organisationId;

    @NotNull(message = "La catégorie est obligatoire")
    private ActionCharite.Categorie categorie;

    private String logo;
    private MultipartFile logoFile;
} 