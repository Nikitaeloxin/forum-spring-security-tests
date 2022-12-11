package telran.java2022.accounting.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRegisterDto {
	String login;
	String password;
	String firstName;
	String lastName;
}
