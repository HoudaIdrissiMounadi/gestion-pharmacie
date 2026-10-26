package ma.ensa.pharmacie.controller.rest;

import ma.ensa.pharmacie.entity.Tag;
import ma.ensa.pharmacie.repository.TagRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagRestController {

    private final TagRepository repository;

    public TagRestController(TagRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Tag> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tag> getById(@PathVariable Long id) {
        return repository.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Tag> create(@RequestBody Tag tag) {
        tag.setId(null);
        Tag saved = repository.save(tag);
        return ResponseEntity.created(URI.create("/api/tags/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tag> update(@PathVariable Long id, @RequestBody Tag body) {
        return repository.findById(id).map(existing -> {
            existing.setLibelle(body.getLibelle());
            return ResponseEntity.ok(repository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return repository.findById(id).map(tag -> {
            // retirer le tag des produits avant suppression (table produit_tag)
            tag.getProduits().forEach(p -> p.getTags().remove(tag));
            repository.delete(tag);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
