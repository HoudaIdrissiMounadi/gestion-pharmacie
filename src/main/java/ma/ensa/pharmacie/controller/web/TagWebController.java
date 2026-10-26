package ma.ensa.pharmacie.controller.web;

import ma.ensa.pharmacie.repository.TagRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TagWebController {

    private final TagRepository repository;

    public TagWebController(TagRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/tags")
    public String liste(Model model) {
        model.addAttribute("tags", repository.findAll());
        return "tags/liste";
    }
}
