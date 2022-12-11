package telran.java2022.accounting.service;

import java.time.LocalDate;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import telran.java2022.accounting.dao.UserAccountRepository;
import telran.java2022.accounting.dto.RolesResponseDto;
import telran.java2022.accounting.dto.UserAccountResponseDto;
import telran.java2022.accounting.dto.UserRegisterDto;
import telran.java2022.accounting.dto.UserUpdateDto;
import telran.java2022.accounting.dto.exceptions.UserExistsException;
import telran.java2022.accounting.dto.exceptions.UserNotFoundException;
import telran.java2022.accounting.model.UserAccount;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService, CommandLineRunner {
	final UserAccountRepository repository;
	final ModelMapper modelMapper;
	final PasswordEncoder passwordEncoder;
	@Value("${password.period:30}")
	long passwordPeriod;

	@Override
	public UserAccountResponseDto addUser(UserRegisterDto userRegisterDto) {
		if (repository.existsById(userRegisterDto.getLogin())) {
			throw new UserExistsException(userRegisterDto.getLogin());
		}
		UserAccount userAccount = UserAccount.builder()
											.login(userRegisterDto.getLogin())
											.password("")
											.firstName(userRegisterDto.getFirstName())
											.lastName(userRegisterDto.getLastName())
											.passwordExpDate(LocalDate.now())
											.role("USER")
											.build();
		String password = passwordEncoder.encode(userRegisterDto.getPassword());
		userAccount.setPassword(password);
		userAccount.setPasswordExpDate(LocalDate.now().plusDays(passwordPeriod));
		repository.save(userAccount);
		return UserAccountResponseDto.builder()
				.login(userAccount.getLogin())
				.firstName(userAccount.getFirstName())
				.lastName(userAccount.getLastName())
				.roles(userAccount.getRoles())
				.build();
	}

	@Override
	public UserAccountResponseDto getUser(String login) {
		UserAccount userAccount = repository.findById(login).orElseThrow(() -> new UserNotFoundException());
		return UserAccountResponseDto.builder()
				.login(userAccount.getLogin())
				.firstName(userAccount.getFirstName())
				.lastName(userAccount.getLastName())
				.roles(userAccount.getRoles())
				.build();
	}

	@Override
	public UserAccountResponseDto removeUser(String login) {
		UserAccount userAccount = repository.findById(login).orElseThrow(() -> new UserNotFoundException());
		repository.deleteById(login);
		return UserAccountResponseDto.builder()
				.login(userAccount.getLogin())
				.firstName(userAccount.getFirstName())
				.lastName(userAccount.getLastName())
				.roles(userAccount.getRoles())
				.build();
	}

	@Override
	public UserAccountResponseDto editUser(String login, UserUpdateDto updateDto) {
		UserAccount userAccount = repository.findById(login).orElseThrow(() -> new UserNotFoundException());
		if (updateDto.getFirstName() != null) {
			userAccount.setFirstName(updateDto.getFirstName());
		}
		if (updateDto.getLastName() != null) {
			userAccount.setLastName(updateDto.getLastName());
		}
		repository.save(userAccount);
		return UserAccountResponseDto.builder()
				.login(userAccount.getLogin())
				.firstName(userAccount.getFirstName())
				.lastName(userAccount.getLastName())
				.roles(userAccount.getRoles())
				.build();
	}

	@Override
	public RolesResponseDto changeRolesList(String login, String role, boolean isAddRole) {
		UserAccount userAccount = repository.findById(login).orElseThrow(() -> new UserNotFoundException());
		boolean res;
		if (isAddRole) {
			res = userAccount.addRole(role.toUpperCase());
		} else {
			res = userAccount.removeRole(role.toUpperCase());
		}
		if (res) {
			repository.save(userAccount);
		}	
		return RolesResponseDto.builder()
								.login(userAccount.getLogin())
								.roles(userAccount.getRoles())
								.build();
	}

	@Override
	public void changePassword(String login, String newPassword) {
		UserAccount userAccount = repository.findById(login).orElseThrow(() -> new UserNotFoundException());
		String password = passwordEncoder.encode(newPassword);
		userAccount.setPassword(password);
		userAccount.setPasswordExpDate(LocalDate.now().plusDays(passwordPeriod));
		repository.save(userAccount);
	}
	
	@Override
	public void run(String... args) throws Exception {
		if(!repository.existsById("admin")) {
			String password = passwordEncoder.encode("admin");
			UserAccount userAccount = new UserAccount("admin", password , "", "");
			userAccount.addRole("MODERATOR");
			userAccount.addRole("ADMINISTRATOR");
			userAccount.setPasswordExpDate(LocalDate.now().plusDays(passwordPeriod));
			repository.save(userAccount);
		}
		
	}

}
