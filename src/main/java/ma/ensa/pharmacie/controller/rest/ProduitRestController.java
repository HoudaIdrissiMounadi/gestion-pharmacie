package ma.ensa.pharmacie.controller.rest;

import ma.ensa.pharmacie.entity.DetailProduit;
import ma.ensa.pharmacie.entity.Produit;
import ma.ensa.pharmacie.repository.ProduitRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/produits")
public class ProduitRestController {

    private final ProduitRepository repository;

    public ProduitRestController(ProduitRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Produit> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produit> getById(@PathVariable Long id) {
        return repository.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Produit> create(@RequestBody Produit produit) {
        produit.setId(null);
        if (produit.getDetailProduit() != null) {
            produit.getDetailProduit().setId(null);
        }
        Produit saved = repository.save(produit);
        return ResponseEntity.created(URI.create("/api/produits/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produit> update(@PathVariable Long id, @RequestBody Produit body) {
        return repository.findById(id).map(existing -> {
            existing.setNom(body.getNom());
            existing.setDescription(body.getDescription());
            existing.setPrix(body.getPrix());
            existing.setCodeBarre(body.getCodeBarre());
            existing.setDateExpiration(body.getDateExpiration());
            existing.setCategorie(body.getCategorie());
            existing.setFournisseur(body.getFournisseur());
            existing.setTags(body.getTags() != null ? body.getTags() : new ArrayList<>());

            // on met à jour le détail existant au lieu d'en créer un nouveau
            DetailProduit nouveau = body.getDetailProduit();
            if (nouveau == null) {
                existing.setDetailProduit(null);
            } else if (existing.getDetailProduit() == null) {
                nouveau.setId(null);
                existing.setDetailProduit(nouveau);
            } else {
                DetailProduit actuel = existing.getDetailProduit();
                actuel.setFabricant(nouveau.getFabricant());
                actuel.setPaysOrigine(nouveau.getPaysOrigine());
                actuel.setComposition(nouveau.getComposition());
                actuel.setDosage(nouveau.getDosage());
                actuel.setIndications(nouveau.getIndications());
            }
            return ResponseEntity.ok(repository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
