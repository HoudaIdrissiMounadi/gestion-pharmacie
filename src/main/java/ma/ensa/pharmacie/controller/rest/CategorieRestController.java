package ma.ensa.pharmacie.controller.rest;

import ma.ensa.pharmacie.entity.Categorie;
import ma.ensa.pharmacie.repository.CategorieRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategorieRestController {

    private final CategorieRepository repository;

    public CategorieRestController(CategorieRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Categorie> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categorie> getById(@PathVariable Long id) {
        return repository.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Categorie> create(@RequestBody Categorie categorie) {
        categorie.setId(null);
        Categorie saved = repository.save(categorie);
        return ResponseEntity.created(URI.create("/api/categories/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categorie> update(@PathVariable Long id, @RequestBody Categorie body) {
        return repository.findById(id).map(existing -> {
            existing.setNom(body.getNom());
            existing.setDescription(body.getDescription());
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
