package ma.ensa.pharmacie.repository;

import ma.ensa.pharmacie.entity.DetailProduit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetailProduitRepository extends JpaRepository<DetailProduit, Long> {
}
