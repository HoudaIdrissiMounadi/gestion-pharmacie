package ma.ensa.pharmacie.repository;

import ma.ensa.pharmacie.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProduitRepository extends JpaRepository<Produit, Long> {
}
