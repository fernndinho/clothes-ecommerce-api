package me.fernndinho.shop.colors;

import com.google.common.collect.Lists;
import me.fernndinho.shop.colors.models.ColorEntity;
import me.fernndinho.shop.colors.payload.ColorPayload;
import me.fernndinho.shop.colors.repo.ColorRepository;
import me.fernndinho.shop.shared.error.exceptions.ConflictException;
import me.fernndinho.shop.shared.error.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ColorServiceTest {
    private ColorRepository repo;
    private ColorService service;

    @BeforeEach
    public void setup() {
        this.repo = Mockito.mock(ColorRepository.class);
        this.service = new ColorService(repo);
    }

    @Test
    public void shouldSuccessfulOnFindAllColors() {
        List<ColorPayload> expected = Lists.newArrayList(
                new ColorPayload("Yellow", "yellow", "FFFF00"),
                new ColorPayload("Red", "red", "FF0000")
        );

        Mockito.when(repo.findAll())
                .thenReturn(Lists.newArrayList(
                new ColorEntity(null, "Yellow", "yellow", "FFFF00"),
                new ColorEntity(null, "Red", "red", "FF0000")
        ));

        List<ColorPayload> result = service.getAll();

        assertEquals(expected.size(), result.size());
        assertIterableEquals(expected, result);
    }


    @Test
    public void shouldReturnAEmptyColorList() {
        Mockito.when(repo.findAll()).thenReturn(Lists.newArrayList());

        List<ColorPayload> result = service.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldSuccessOnCreateTheColor() {
        ColorPayload expected = new ColorPayload("Yellow", "yellow", "FFFF00");

        when(repo.save(any(ColorEntity.class)))
                .thenReturn(new ColorEntity(null, "Yellow", "yellow", "FFFF00"));

        ColorPayload color = service.create(new ColorPayload("Yellow", "yellow", "FFFF00"));

        assertEquals(color, expected);
    }

    @Test
    public void shouldFailOnCreateAExistentColor() {
        Class<ConflictException> expected = ConflictException.class;

        ColorPayload existentColor = new ColorPayload("Yellow", "yellow", "FFFF00");
        when(repo.existsBySlug("yellow"))
                .thenReturn(true);


        assertThrows(expected, () -> {
            service.create(existentColor);
        });
    }


    @Test
    public void shouldFailIfColorNotExistOnDelete() {
        String color = "yellow";

        when(repo.existsBySlug("yellow")).thenReturn(false);

        assertThrows(NotFoundException.class, () -> {
            service.delete("yellow");
        });
    }
}
