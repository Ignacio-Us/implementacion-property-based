package pruebas.software.propertysbase.infrastructure.persistence;

import pruebas.software.propertysbase.domain.model.User;
import pruebas.software.propertysbase.domain.repository.UserRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> store = new ConcurrentHashMap<>();

    // simulamos el autoincremento de una DB real
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public User save(User user) {
        if (user.getId() == null) { // Si existe un ID, asumimos que es una actualización, si no, es una creación
            User newUser = user.toBuilder()
                    .id(idGenerator.getAndIncrement())
                    .build();
            store.put(newUser.getId(), newUser);
            return newUser;
        } else {
            store.put(user.getId(), user);
            return user;
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }

    @Override
    public long count() {
        return store.size();
    }
}
