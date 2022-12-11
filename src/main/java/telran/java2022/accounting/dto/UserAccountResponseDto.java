package telran.java2022.accounting.dto;

import java.util.Objects;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserAccountResponseDto {
	String login;
	String firstName;
	String lastName;
	@Singular
	Set<String> roles;
	@Override
	public int hashCode() {
		return Objects.hash(firstName, lastName, login, roles);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserAccountResponseDto other = (UserAccountResponseDto) obj;
		return Objects.equals(firstName, other.firstName) && Objects.equals(lastName, other.lastName)
				&& Objects.equals(login, other.login) && Objects.equals(roles, other.roles);
	}
	
}
