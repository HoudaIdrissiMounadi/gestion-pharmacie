package ma.ensa.pharmacie.repository;

import ma.ensa.pharmacie.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
