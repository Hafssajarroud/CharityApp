package ma.emsi.charityapp.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class OrganisationDto {
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

    private MultipartFile logo;

    @Size(max = 1000, message = "La description ne doit pas dépasser 1000 caractères")
    private String description;
} 