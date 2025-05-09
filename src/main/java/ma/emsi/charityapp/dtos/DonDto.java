package ma.emsi.charityapp.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ma.emsi.charityapp.entities.MethodePaiement;

public class DonDto {

    private Integer id;

    @NotNull(message = "L'action caritative est requise")
    private Integer actionId;

    @NotNull(message = "Le montant est requis")
    @Min(value = 1, message = "Le montant doit être supérieur à 0")
    private Double montant;

    @NotNull(message = "La méthode de paiement est requise")
    private MethodePaiement methodePaiement;

    @NotNull(message = "L'ID de l'utilisateur est requis")
    private int userId;

    @Size(max = 500, message = "Le message ne doit pas dépasser 500 caractères")
    private String message;

    // Getters & setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getActionId() {
        return actionId;
    }

    public void setActionId(Integer actionId) {
        this.actionId = actionId;
    }

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }

    public MethodePaiement getMethodePaiement() {
        return methodePaiement;
    }

    public void setMethodePaiement(MethodePaiement methodePaiement) {
        this.methodePaiement = methodePaiement;
    }

    public Integer getUserId() {
        return Math.toIntExact(userId);
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
