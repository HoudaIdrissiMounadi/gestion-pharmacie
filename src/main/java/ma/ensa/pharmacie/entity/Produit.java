package ma.ensa.pharmacie.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "produit")
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    private String description;

    private BigDecimal prix;

    @Column(name = "code_barre")
    private String codeBarre;

    @Column(name = "date_expiration")
    private LocalDate dateExpiration;

    // * produits -> 1 catégorie
    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;

    // * produits -> 1 fournisseur
    @ManyToOne
    @JoinColumn(name = "fournisseur_id")
    private Fournisseur fournisseur;

    // 1 produit <-> 1 détail
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "detail_id")
    private DetailProduit detailProduit;

    // * produits <-> * tags (table de jointure produit_tag)
    @ManyToMany
    @JoinTable(
            name = "produit_tag",
            joinColumns = @JoinColumn(name = "produit_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();

    public Produit() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrix() { return prix; }
    public void setPrix(BigDecimal prix) { this.prix = prix; }

    public String getCodeBarre() { return codeBarre; }
    public void setCodeBarre(String codeBarre) { this.codeBarre = codeBarre; }

    public LocalDate getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(LocalDate dateExpiration) { this.dateExpiration = dateExpiration; }

    public Categorie getCategorie() { return categorie; }
    public void setCategorie(Categorie categorie) { this.categorie = categorie; }

    public Fournisseur getFournisseur() { return fournisseur; }
    public void setFournisseur(Fournisseur fournisseur) { this.fournisseur = fournisseur; }

    public DetailProduit getDetailProduit() { return detailProduit; }
    public void setDetailProduit(DetailProduit detailProduit) { this.detailProduit = detailProduit; }

    public List<Tag> getTags() { return tags; }
    public void setTags(List<Tag> tags) { this.tags = tags; }
}
