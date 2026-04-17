package pruebas.software.propertysbase.domain.service;

import java.util.Optional;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pruebas.software.propertysbase.domain.model.User;
import pruebas.software.propertysbase.domain.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User create(User user) {
        if (!user.isValid()) {
            throw new IllegalArgumentException("Datos de usuario inválidos");
        }
        return userRepository.save(user);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}
