package org.formation.coffee.application.service;

import org.formation.coffee.application.port.in.CreateCoffeeCommand;
import org.formation.coffee.application.port.in.UpdateCoffeeCommand;
import org.formation.coffee.application.port.out.LoadCoffeePort;
import org.formation.coffee.application.port.out.SaveCoffeePort;
import org.formation.coffee.application.port.out.TransactionRunner;
import org.formation.coffee.domain.exception.CoffeeNotFoundException;
import org.formation.coffee.domain.model.Coffee;
import org.formation.coffee.domain.model.CoffeeId;
import org.formation.coffee.domain.model.Price;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoffeeApplicationServiceTest {

    private InMemoryCoffeeRepository fakeRepository;
    private CoffeeApplicationService service;

    @BeforeEach
    void setUp() {
        fakeRepository = new InMemoryCoffeeRepository();
        TransactionRunner directRunner = new TransactionRunner() {
            @Override
            public <T> T execute(Supplier<T> work) {
                return work.get();
            }

            @Override
            public <T> T executeReadOnly(Supplier<T> work) {
                return work.get();
            }
        };
        service = new CoffeeApplicationService(fakeRepository, fakeRepository, directRunner);
    }

    @Test
    @DisplayName("Devrait créer et enregistrer un café")
    void shouldCreateCoffee() {
        Coffee created = service.createCoffee(new CreateCoffeeCommand("Mocha", new Price(30)));

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Mocha");
        assertThat(created.getPrice().amount()).isEqualTo(30);
    }

    @Test
    @DisplayName("Devrait charger un café par son identifiant")
    void shouldGetCoffeeById() {
        Coffee created = service.createCoffee(new CreateCoffeeCommand("Latte", new Price(20)));

        Coffee found = service.getCoffee(created.getId());
        assertThat(found.getName()).isEqualTo("Latte");
    }

    @Test
    @DisplayName("Devrait lancer CoffeeNotFoundException si café inexistant")
    void shouldThrowWhenCoffeeNotFound() {
        assertThatThrownBy(() -> service.getCoffee(new CoffeeId(404L)))
                .isInstanceOf(CoffeeNotFoundException.class)
                .hasMessageContaining("Coffee 404 not found");
    }

    @Test
    @DisplayName("Devrait mettre à jour partiellement le nom et le prix")
    void shouldUpdateCoffeePartially() {
        Coffee created = service.createCoffee(new CreateCoffeeCommand("Cappuccino", new Price(25)));

        Coffee updated = service.updateCoffee(new UpdateCoffeeCommand(created.getId(), "Freddo Cappuccino", null));
        assertThat(updated.getName()).isEqualTo("Freddo Cappuccino");
        assertThat(updated.getPrice().amount()).isEqualTo(25);
    }

    private static class InMemoryCoffeeRepository implements LoadCoffeePort, SaveCoffeePort {
        private final Map<Long, Coffee> store = new HashMap<>();
        private final AtomicLong sequence = new AtomicLong(1);

        @Override
        public List<Coffee> findAll() {
            return new ArrayList<>(store.values());
        }

        @Override
        public Optional<Coffee> findById(CoffeeId id) {
            return Optional.ofNullable(store.get(id.value()));
        }

        @Override
        public Coffee save(Coffee coffee) {
            if (coffee.isNew()) {
                long newId = sequence.getAndIncrement();
                Coffee saved = Coffee.reconstitute(new CoffeeId(newId), coffee.getName(), coffee.getPrice(), coffee.getReviews());
                store.put(newId, saved);
                return saved;
            } else {
                store.put(coffee.getId().value(), coffee);
                return coffee;
            }
        }
    }
}
