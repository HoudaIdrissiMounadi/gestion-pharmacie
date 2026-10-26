package ma.ensa.pharmacie.repository;

import ma.ensa.pharmacie.entity.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategorieRepository extends JpaRepository<Categorie, Long> {
}
