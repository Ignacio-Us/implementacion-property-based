package pruebas.software.propertysbase.domain.repository;

import pruebas.software.propertysbase.domain.model.User;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    void deleteById(Long id);
    long count(); // Para facilitar pruebas de conteo
}
