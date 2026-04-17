package pruebas.software.propertysbase.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder(toBuilder = true)
@ToString
public class User {
    private final Long id;
    private final String name;
    private final String email;
    private final String password;
    
    public boolean isValid() {
        return isNameValid() && isEmailValid() && isPasswordValid();
    }

    private boolean isNameValid() {
        return name != null && !name.trim().isEmpty();
    }

    private boolean isEmailValid() {
        return email != null && email.contains("@") && email.length() > 5;
    }

    private boolean isPasswordValid() {
        return password != null && password.length() >= 8;
    }
}
