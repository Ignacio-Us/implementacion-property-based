package pruebas.software.propertysbase.properties;

import java.util.Optional;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import pruebas.software.propertysbase.domain.model.User;
import pruebas.software.propertysbase.domain.service.UserService;
import pruebas.software.propertysbase.domain.service.exceptions.UserNotFoundException;
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

    @Property(tries = 1000)
    void existingUsersShouldBeUpdatedSuccessfully(
        @ForAll("validNames") String name1, @ForAll("validEmails") String email1, @ForAll("validPasswords") String password1,
        @ForAll("validNames") String name2, @ForAll("validEmails") String email2, @ForAll("validPasswords") String password2
    ) {
        // 1. Crear usuario original
        User originalUser = userService.create(User.builder()
                .name(name1)
                .email(email1)
                .password(password1)
                .build());

        // 2. Preparar datos de actualización
        User updateData = User.builder()
                .name(name2)
                .email(email2)
                .password(password2)
                .build();

        // 3. Ejecutar actualización
        User updatedUser = userService.update(originalUser.getId(), updateData);

        // 4. Assert: Invariantes de Actualización
        // El ID debe mantenerse igual, pero los datos deben ser los nuevos
        assertThat(updatedUser.getId()).isEqualTo(originalUser.getId());
        assertThat(updatedUser.getName()).isEqualTo(name2);
        assertThat(updatedUser.getEmail()).isEqualTo(email2);
        assertThat(updatedUser.getPassword()).isEqualTo(password2);

        // 5. Verificar que al recuperarlo de base de datos persistan los cambios
        Optional<User> retrievedUser = userService.findById(originalUser.getId());
        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getName()).isEqualTo(name2);
        assertThat(retrievedUser.get().getEmail()).isEqualTo(email2);
        assertThat(retrievedUser.get().getPassword()).isEqualTo(password2);
    }

    @Property(tries = 1000)
    void existingUsersShouldBeDeletedSuccessfully(
        @ForAll("validNames") String name, 
        @ForAll("validEmails") String email, 
        @ForAll("validPasswords") String password
    ) {
        User originalUser = userService.create(User.builder()
                .name(name)
                .email(email)
                .password(password)
                .build());
        
        Long userId = originalUser.getId();

        userService.delete(userId);

        Optional<User> retrievedUser = userService.findById(userId);
        
        assertThat(retrievedUser).isEmpty();
        
        //Se comprueba que la cantidad de usuarios en el sistema es 0
        assertThat(userService.count()).isEqualTo(0);
    }

    @Property(tries = 100)
    void deletingNonExistentUserShouldBeHandledGracefully(
        @ForAll("validNames") String name, 
        @ForAll("validEmails") String email, 
        @ForAll("validPasswords") String password
    ) {
        
        User user = userService.create(User.builder().name(name).email(email).password(password).build());
        // Para garantizar un ID que NO existe en el sistema actual.
        Long nonExistentId = user.getId() + 9999L;

        assertThatThrownBy(() -> userService.delete(nonExistentId))
            .isInstanceOf(UserNotFoundException.class);
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
