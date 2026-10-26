package ma.ensa.pharmacie.controller.rest;

import ma.ensa.pharmacie.entity.Fournisseur;
import ma.ensa.pharmacie.repository.FournisseurRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/fournisseurs")
public class FournisseurRestController {

    private final FournisseurRepository repository;

    public FournisseurRestController(FournisseurRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Fournisseur> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fournisseur> getById(@PathVariable Long id) {
        return repository.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Fournisseur> create(@RequestBody Fournisseur fournisseur) {
        fournisseur.setId(null);
        Fournisseur saved = repository.save(fournisseur);
        return ResponseEntity.created(URI.create("/api/fournisseurs/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Fournisseur> update(@PathVariable Long id, @RequestBody Fournisseur body) {
        return repository.findById(id).map(existing -> {
            existing.setNom(body.getNom());
            existing.setContact(body.getContact());
            existing.setAdresse(body.getAdresse());
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
