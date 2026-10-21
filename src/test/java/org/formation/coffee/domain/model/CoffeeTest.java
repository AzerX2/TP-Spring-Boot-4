package org.formation.coffee.domain.model;

import org.formation.coffee.domain.exception.DomainValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoffeeTest {

    @Test
    @DisplayName("Devrait créer un café valide sans identifiant")
    void shouldCreateNewCoffee() {
        Coffee coffee = Coffee.create("Espresso", new Price(25));

        assertThat(coffee.isNew()).isTrue();
        assertThat(coffee.getId()).isNull();
        assertThat(coffee.getName()).isEqualTo("Espresso");
        assertThat(coffee.getPrice().amount()).isEqualTo(25);
        assertThat(coffee.getReviews()).isEmpty();
    }

    @Test
    @DisplayName("Devrait refuser un nom vide")
    void shouldRejectBlankName() {
        assertThatThrownBy(() -> Coffee.create("   ", new Price(10)))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Coffee name must not be blank");
    }

    @Test
    @DisplayName("Devrait refuser un nom dépassant 255 caractères")
    void shouldRejectTooLongName() {
        String longName = "a".repeat(256);
        assertThatThrownBy(() -> Coffee.create(longName, new Price(10)))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Coffee name must not exceed 255 characters");
    }

    @Test
    @DisplayName("Devrait refuser un prix négatif ou supérieur à 200")
    void shouldRejectInvalidPrice() {
        assertThatThrownBy(() -> new Price(-1))
                .isInstanceOf(DomainValidationException.class);

        assertThatThrownBy(() -> new Price(201))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    @DisplayName("Devrait modifier le nom et le prix avec validation")
    void shouldRenameAndChangePrice() {
        Coffee coffee = Coffee.create("Old Name", new Price(10));
        coffee.rename("New Name");
        coffee.changePrice(new Price(50));

        assertThat(coffee.getName()).isEqualTo("New Name");
        assertThat(coffee.getPrice().amount()).isEqualTo(50);
    }
}
