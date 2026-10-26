package ma.ensa.pharmacie.controller.web;

import ma.ensa.pharmacie.repository.FournisseurRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FournisseurWebController {

    private final FournisseurRepository repository;

    public FournisseurWebController(FournisseurRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/fournisseurs")
    public String liste(Model model) {
        model.addAttribute("fournisseurs", repository.findAll());
        return "fournisseurs/liste";
    }
}
