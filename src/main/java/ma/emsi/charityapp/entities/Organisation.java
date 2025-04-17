package ma.emsi.charityapp.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Organisation {
    @Id
    private int id;
    private String nom;
    private String adresse;
    private String numeroFiscal;
    private String contactPrincipal;
    private String logo;
    private String description;
    private boolean statutValidation;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User administrateur;

    @OneToMany(mappedBy = "organisation", cascade = CascadeType.ALL)
    private List<ActionCharite> actions;
}
