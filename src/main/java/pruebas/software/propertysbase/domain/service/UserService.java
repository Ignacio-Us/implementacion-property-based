package pruebas.software.propertysbase.domain.service;

import java.util.Optional;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pruebas.software.propertysbase.domain.model.User;
import pruebas.software.propertysbase.domain.repository.UserRepository;
import pruebas.software.propertysbase.domain.service.exceptions.UserNotFoundException;

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

    public User update(Long id, User updatedUser) {
        if (!updatedUser.isValid()) {
            throw new IllegalArgumentException("Datos de usuario inválidos");
        }
        return userRepository.findById(id).map(existingUser -> {
            User userToSave = updatedUser.toBuilder().id(id).build();
            return userRepository.save(userToSave);
        }).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    public void delete(Long id) {
        if (!userRepository.findById(id).isPresent()) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    public long count() {
        return userRepository.count();
    }
}
