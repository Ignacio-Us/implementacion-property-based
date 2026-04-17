package pruebas.software.propertysbase.properties;

import java.util.Optional;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import pruebas.software.propertysbase.domain.model.User;
import pruebas.software.propertysbase.domain.service.UserService;
import pruebas.software.propertysbase.infrastructure.persistence.InMemoryUserRepository;
import static org.assertj.core.api.Assertions.*;

public class UserPropertyTests {

    private final UserService userService = new UserService(new InMemoryUserRepository());
    
    @Property(tries = 1000) // Ejecuta 1000 escenarios distintos
    void allValidUsersShouldBeCreatedSuccessfully(
        @ForAll("validNames") String name,
        @ForAll("validEmails") String email,
        @ForAll("validPasswords") String password
    ) {
        User newUser = User.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();

        User createdUser = userService.create(newUser);

        // Assert: Invariante de Creación
        assertThat(createdUser.getId()).isPositive();
        assertThat(createdUser.getName()).isEqualTo(name);
        assertThat(createdUser.getEmail()).isEqualTo(email);
    }

    @Property(tries = 1000)
    void createdUsersShouldBeRetrievableById(
        @ForAll("validNames") String name,
        @ForAll("validEmails") String email,
        @ForAll("validPasswords") String password
    ) {
        User newUser = User.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();

        User createdUser = userService.create(newUser);
        
        Optional<User> retrievedUser = userService.findById(createdUser.getId());

        // Assert: Invariante de Lectura/Recuperación
        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getId()).isEqualTo(createdUser.getId());
        assertThat(retrievedUser.get().getName()).isEqualTo(createdUser.getName());
        assertThat(retrievedUser.get().getEmail()).isEqualTo(createdUser.getEmail());
        assertThat(retrievedUser.get().getPassword()).isEqualTo(createdUser.getPassword());
    }

    // GENERADORES personalizados para cada propiedad
    @Provide
    Arbitrary<String> validNames() {
        return Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(50).filter(s -> !s.trim().isEmpty());
    }

    @Provide
    Arbitrary<String> validEmails() {
        return Arbitraries.strings().alpha().numeric()
                .ofMinLength(1).ofMaxLength(20)
                .map(s -> s + "@ufro.cl");
    }

    @Provide
    Arbitrary<String> validPasswords() {
        return Arbitraries.strings().ascii().ofMinLength(8).ofMaxLength(30);
    }
}
