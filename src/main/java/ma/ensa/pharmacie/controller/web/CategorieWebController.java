package ma.ensa.pharmacie.controller.web;

import ma.ensa.pharmacie.repository.CategorieRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CategorieWebController {

    private final CategorieRepository repository;

    public CategorieWebController(CategorieRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/categories")
    public String liste(Model model) {
        model.addAttribute("categories", repository.findAll());
        return "categories/liste";
    }
}
