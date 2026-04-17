package pruebas.software.propertysbase.domain.service.exceptions;

public class UserNotFoundException extends RuntimeException {
    
    public UserNotFoundException(Long id) {
        super("No se encontró ningún usuario asociado al ID: " + id);
    }
}
