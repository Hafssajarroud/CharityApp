package ma.emsi.charityapp.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ma.emsi.charityapp.entities.Role;

@Data
public class UserRegistrationDto {

    public enum TypeCompte {
        DONATEUR("Donateur"),
        ORGANISATION("Organisation");

        private final String label;

        TypeCompte(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String motDePasse;

    @NotBlank(message = "La confirmation du mot de passe est obligatoire")
    private String confirmMotDePasse;

    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    private String telephone;

    @NotNull(message = "Le type de compte est obligatoire")
    private TypeCompte typeCompte;
} 