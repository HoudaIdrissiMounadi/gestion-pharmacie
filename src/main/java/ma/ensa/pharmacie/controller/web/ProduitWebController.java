package ma.ensa.pharmacie.controller.web;

import ma.ensa.pharmacie.entity.Produit;
import ma.ensa.pharmacie.repository.ProduitRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Controller
public class ProduitWebController {

    private final ProduitRepository repository;

    public ProduitWebController(ProduitRepository repository) {
        this.repository = repository;
    }

    @GetMapping({"/", "/produits"})
    public String liste(Model model) {
        model.addAttribute("produits", repository.findAll());
        return "produits/liste";
    }

    @GetMapping("/produits/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Produit produit = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produit introuvable"));
        model.addAttribute("produit", produit);
        return "produits/detail";
    }
}
