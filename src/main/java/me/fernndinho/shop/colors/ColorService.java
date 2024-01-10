package me.fernndinho.shop.colors;

import lombok.AllArgsConstructor;
import me.fernndinho.shop.colors.models.ColorEntity;
import me.fernndinho.shop.colors.payload.ColorPayload;
import me.fernndinho.shop.colors.repo.ColorRepository;
import me.fernndinho.shop.shared.error.exceptions.ConflictException;
import me.fernndinho.shop.shared.error.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ColorService {
    private ColorRepository repo;

    public List<ColorPayload> getAll() {
        return repo.findAll()
                .stream()
                .map(ColorPayload::new)
                .collect(Collectors.toList());
    }

    public ColorPayload getColor(String slug) {
        return repo.findBySlug(slug)
                .map(ColorPayload::new)
                .orElseThrow(() -> new NotFoundException("color provided not found"));
    }

    public ColorPayload create(ColorPayload payload) {
        if(repo.existsBySlug(payload.getSlug()))
            throw new ConflictException("the color provided already exist");

        ColorEntity color = new ColorEntity(null, payload.getName(), payload.getSlug(), payload.getHex());

        ColorEntity savedColor = repo.save(color);

        return new ColorPayload(savedColor);
    }

    public void delete(String slug) {
        if(!repo.existsBySlug(slug))
            throw new NotFoundException("cannot delete a color that does not exist");
        repo.deleteBySlug(slug);
    }
}