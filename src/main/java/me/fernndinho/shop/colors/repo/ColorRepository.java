package me.fernndinho.shop.colors.repo;

import me.fernndinho.shop.colors.models.ColorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ColorRepository extends JpaRepository<ColorEntity, Long> {
    Optional<ColorEntity> findBySlug(String slug);

    List<ColorEntity> findBySlugIn(Collection<String> slug);

    boolean existsBySlug(String slug);

    void deleteBySlug(String slug);
}
