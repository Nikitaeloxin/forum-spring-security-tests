package telran.java2022.accounting.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import telran.java2022.accounting.dao.UserAccountRepository;
import telran.java2022.accounting.dto.RolesResponseDto;
import telran.java2022.accounting.dto.UserAccountResponseDto;
import telran.java2022.accounting.dto.UserRegisterDto;
import telran.java2022.accounting.dto.UserUpdateDto;
import telran.java2022.accounting.model.UserAccount;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceImplTest {
	
	@InjectMocks
	UserAccountServiceImpl userAccountService;
	
	@Mock
	UserAccountRepository repository;
	
	
	PasswordEncoder passwordEncoder =mock(PasswordEncoder.class);
	Optional<UserAccount> userAcc;
	UserAccountResponseDto userDto;
	
	@BeforeEach
	void init(){
		 userAcc = Optional.of(new UserAccount("JavaFan", "1234", "John", "Connor"));
		 userDto = UserAccountResponseDto.builder()
					.login(userAcc.get().getLogin())
					.firstName(userAcc.get().getFirstName())
					.lastName(userAcc.get().getLastName())
					.roles(userAcc.get().getRoles())
					.build();
		 
	}

	@Test
	void testAddUser() {
		when(repository.existsById("JavaFan")).thenReturn(false);
		when(passwordEncoder.encode(userAcc.get().getPassword())).thenReturn(userAcc.get().getPassword());
		UserRegisterDto userRegisterDto = UserRegisterDto.builder()
																.login(userAcc.get().getLogin())
																.password(userAcc.get().getPassword())
																.firstName(userAcc.get().getFirstName())
																.lastName(userAcc.get().getLastName())
																.build();
		assertEquals(userAccountService.addUser(userRegisterDto), userDto);
	}
	

	@Test
	void testGetUser() {
		when(repository.findById("JavaFan")).thenReturn(userAcc);
		assertEquals(userAccountService.getUser("JavaFan"), userDto);
	}

	@Test
	void testRemoveUser() {
		when(repository.findById("JavaFan")).thenReturn(userAcc);
		assertEquals(userAccountService.removeUser("JavaFan"), userDto);
	}

	@Test
	void testEditUser() {
		when(repository.findById("JavaFan")).thenReturn(userAcc);
		UserUpdateDto userUpdateDto = new UserUpdateDto("firstName1", "lastName1");
		UserAccountResponseDto userDto = UserAccountResponseDto.builder()
																.login(userAcc.get().getLogin())
																.firstName(userUpdateDto.getFirstName())
																.lastName(userUpdateDto.getLastName())
																.roles(userAcc.get().getRoles())
																.build();
		assertEquals(userAccountService.editUser("JavaFan",userUpdateDto), userDto);
	}

	@Test
	void testChangeRolesList() {
		when(repository.findById("JavaFan")).thenReturn(userAcc);
		Set<String> roles =  new HashSet<String>();
		roles.add("USER");
		roles.add("ADMIN");
		RolesResponseDto rolesResponseDto = new RolesResponseDto("JavaFan", roles);
		assertEquals(userAccountService.changeRolesList("JavaFan","ADMIN",true), rolesResponseDto);
	}

	@Test
	void testChangePassword() {
		when(repository.findById("JavaFan")).thenReturn(userAcc);
		String newPassword = "4321";
		when(passwordEncoder.encode(newPassword)).thenReturn(newPassword);
		userAccountService.changePassword("JavaFan", newPassword);
		assertEquals(userAcc.get().getPassword(), newPassword);
	}


}
