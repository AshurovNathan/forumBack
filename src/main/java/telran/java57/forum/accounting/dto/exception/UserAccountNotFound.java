package telran.java57.forum.accounting.dto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class UserAccountNotFound extends RuntimeException {
    public UserAccountNotFound(String login) {
        super("Account with login " + login  + " not found");
    }
}
