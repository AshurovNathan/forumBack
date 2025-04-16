package telran.java57.forum.accounting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserRegisterDto {
    String login;
    String password;
    String firstName;
    String lastName;
}
