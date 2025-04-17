package telran.java57.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import telran.java57.forum.accounting.dao.UserAccountRepository;
import telran.java57.forum.accounting.dto.RolesDto;
import telran.java57.forum.accounting.dto.UpdateUserDto;
import telran.java57.forum.accounting.dto.UserDto;
import telran.java57.forum.accounting.dto.UserRegisterDto;
import telran.java57.forum.accounting.dto.exception.UserExistsException;
import telran.java57.forum.accounting.dto.exception.UserNotFoundException;
import telran.java57.forum.accounting.model.Role;
import telran.java57.forum.accounting.model.UserAccount;
import telran.java57.forum.accounting.service.UserAccountServiceImpl;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestUserAccountService {
    @Mock
    ModelMapper modelMapper;

    @Mock
    UserAccountRepository userAccountRepository;

    @InjectMocks
    UserAccountServiceImpl userAccountService;

    @Test
    void testUserRegister(){
        UserRegisterDto userRegisterDto = new UserRegisterDto("asd","123","John","Doe");
        UserDto userDto = new UserDto("asd","John","Doe", Set.of("USER"));
        UserAccount userAccount = new UserAccount("asd","123","John","Doe");

        when(userAccountRepository.existsById("asd")).thenReturn(false);
        when(modelMapper.map(userRegisterDto, UserAccount.class)).thenReturn(userAccount);
        when(userAccountRepository.save(userAccount)).thenReturn(userAccount);
        when(modelMapper.map(userAccount, UserDto.class)).thenReturn(userDto);

        UserDto res = userAccountService.register(userRegisterDto);

        assertNotNull(res);
        assertEquals(userDto.getFirstName(), res.getFirstName());
        assertTrue(userAccount.getPassword().startsWith("$2a$"));

        verify(userAccountRepository).existsById("asd");
        verify(modelMapper).map(userRegisterDto,UserAccount.class);
        verify(userAccountRepository).save(userAccount);
        verify(modelMapper).map(userAccount,UserDto.class);
    }

    @Test
    void testUserRegisterExists(){
        UserRegisterDto userRegisterDto = new UserRegisterDto("asd","123","John","Doe");

        when(userAccountRepository.existsById("asd")).thenReturn(true);

        assertThrows(UserExistsException.class,() -> userAccountService.register(userRegisterDto));

        verify(userAccountRepository).existsById("asd");
        verifyNoMoreInteractions(userAccountRepository);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testUserRegisterWrongLogin(){
        UserRegisterDto userRegisterDto = new UserRegisterDto("@a!?","123","John","Doe");

        assertThrows(UserExistsException.class,() -> userAccountService.register(userRegisterDto));

        verifyNoInteractions(userAccountRepository);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testGetUser(){
        String login = "asd";
        UserAccount userAccount = new UserAccount("asd","123","John","Doe");
        UserDto userDto = new UserDto("asd","John","Doe",Set.of("USER"));

        when(userAccountRepository.findById(login)).thenReturn(Optional.of(userAccount));
        when(modelMapper.map(userAccount, UserDto.class)).thenReturn(userDto);

        UserDto res = userAccountService.getUser(login);

        assertNotNull(res);
        assertEquals("John",res.getFirstName());

        verify(userAccountRepository).findById(login);
        verify(modelMapper).map(userAccount,UserDto.class);
    }

    @Test
    void testGetUserNotFound(){
        String login = "asd";

        when(userAccountRepository.findById(login)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,() -> userAccountService.getUser(login));

        verify(userAccountRepository).findById(login);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testRemoveUser(){
        String login = "asd";
        UserAccount userAccount = new UserAccount("asd","123","John","Doe");
        UserDto userDto = new UserDto("asd","John","Doe",Set.of("USER"));

        when(userAccountRepository.findById(login)).thenReturn(Optional.of(userAccount));
        when(modelMapper.map(userAccount,UserDto.class)).thenReturn(userDto);

        UserDto res = userAccountService.removeUser(login);

        assertNotNull(res);
        assertEquals(userDto.getFirstName(),res.getFirstName());

        verify(userAccountRepository).findById(login);
        verify(modelMapper).map(userAccount,UserDto.class);
    }

    @Test
    void testRemoveUserNotFound(){
        String login = "asd";

        when(userAccountRepository.findById(login)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,() -> userAccountService.removeUser(login));

        verify(userAccountRepository).findById(login);
        verifyNoMoreInteractions(userAccountRepository);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testUpdateUser(){
        String login = "asd";
        UpdateUserDto updateUserDto = new UpdateUserDto("Oscar","Piastri");
        UserAccount userAccount = new UserAccount("asd","123","John","Doe");
        UserDto userDto = new UserDto("asd", "Oscar","Piastri",Set.of("USER"));

        when(userAccountRepository.findById(login)).thenReturn(Optional.of(userAccount));
        when(userAccountRepository.save(userAccount)).thenReturn(userAccount);
        when(modelMapper.map(userAccount,UserDto.class)).thenReturn(userDto);

        UserDto res = userAccountService.updateUser(login,updateUserDto);

        assertNotNull(res);
        assertEquals("Oscar",res.getFirstName());
        assertEquals("Piastri",res.getLastName());

        assertEquals("Oscar", userAccount.getFirstName());
        assertEquals("Piastri", userAccount.getLastName());

        verify(userAccountRepository).findById(login);
        verify(userAccountRepository).save(userAccount);
        verify(modelMapper).map(userAccount,UserDto.class);
    }

    @Test
    void testUpdateUserOneOfNamesNull(){
        String login = "asd";
        UpdateUserDto updateUserDto = new UpdateUserDto("Oscar",null);
        UserAccount userAccount = new UserAccount("asd","123","John","Doe");
        UserDto userDto = new UserDto("asd", "Oscar","Doe",Set.of("USER"));

        when(userAccountRepository.findById(login)).thenReturn(Optional.of(userAccount));
        when(userAccountRepository.save(userAccount)).thenReturn(userAccount);
        when(modelMapper.map(userAccount,UserDto.class)).thenReturn(userDto);

        UserDto res = userAccountService.updateUser(login,updateUserDto);

        assertNotNull(res);
        assertEquals("Oscar",res.getFirstName());
        assertEquals("Doe",res.getLastName());

        assertEquals("Oscar", userAccount.getFirstName());
        assertEquals("Doe", userAccount.getLastName());

        verify(userAccountRepository).findById(login);
        verify(userAccountRepository).save(userAccount);
        verify(modelMapper).map(userAccount,UserDto.class);
    }

    @Test
    void updateUserNotFound(){
        String login = "asd";
        UpdateUserDto updateUserDto = new UpdateUserDto();
        UserAccount userAccount = new UserAccount();

        when(userAccountRepository.findById(login)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userAccountService.updateUser(login,updateUserDto));

        verify(userAccountRepository).findById(login);
        verifyNoMoreInteractions(userAccountRepository);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testAddRole(){
        String login = "asd";
        String role = "ADMINISTRATOR";
        UserAccount userAccount = new UserAccount("asd", "123", "John", "Doe");
        RolesDto rolesDto = new RolesDto("asd",Set.of("USER", "ADMINISTRATOR"));

        when(userAccountRepository.findById(login)).thenReturn(Optional.of(userAccount));
        when(userAccountRepository.save(userAccount)).thenReturn(userAccount);
        when(modelMapper.map(userAccount, RolesDto.class)).thenReturn(rolesDto);

        RolesDto res = userAccountService.changeRolesList(login,role,true);

        assertNotNull(res);
        assertTrue(res.getRoles().contains(role));
        assertTrue(userAccount.getRoles().contains(Role.ADMINISTRATOR));

        verify(userAccountRepository).findById(login);
        verify(userAccountRepository).save(userAccount);
        verify(modelMapper).map(userAccount,RolesDto.class);
    }

    @Test
    void testRemoveRole(){
        String login = "asd";
        String role = "ADMINISTRATOR";
        UserAccount userAccount = new UserAccount("asd", "123", "John", "Doe");
        userAccount.addRole(role);
        RolesDto rolesDto = new RolesDto("asd",Set.of("USER"));

        when(userAccountRepository.findById(login)).thenReturn(Optional.of(userAccount));
        when(userAccountRepository.save(userAccount)).thenReturn(userAccount);
        when(modelMapper.map(userAccount, RolesDto.class)).thenReturn(rolesDto);

        RolesDto res = userAccountService.changeRolesList(login,role,false);

        assertNotNull(res);
        assertFalse(res.getRoles().contains(role));

        verify(userAccountRepository).findById(login);
        verify(userAccountRepository).save(userAccount);
        verify(modelMapper).map(userAccount,RolesDto.class);
    }

    @Test
    void testChangeRolesNotFound(){
        String login = "asd";
        String role = "ADMINISTRATOR";

        when(userAccountRepository.findById(login)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userAccountService.changeRolesList(login,role,true));
        assertThrows(UserNotFoundException.class, () -> userAccountService.changeRolesList(login,role,false));

        verify(userAccountRepository,times(2)).findById(login);
        verifyNoMoreInteractions(userAccountRepository);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testChangePassword(){
        String login = "asd";
        String newPassword = "1234";
        UserAccount userAccount = new UserAccount("asd","123","John","Doe");

        when(userAccountRepository.findById(login)).thenReturn(Optional.of(userAccount));
        when(userAccountRepository.save(userAccount)).thenReturn(userAccount);

        userAccountService.changePassword(login,newPassword);

        assertTrue(userAccount.getPassword().startsWith("$2a$"));
        assertTrue(BCrypt.checkpw(newPassword,userAccount.getPassword()));

        verify(userAccountRepository).findById(login);
        verify(userAccountRepository).save(userAccount);
    }

    @Test
    void testChangePasswordNull(){
        String login = "asd";
        UserAccount userAccount = new UserAccount("asd","123","John","Doe");

        when(userAccountRepository.findById(login)).thenReturn(Optional.of(userAccount));
        when(userAccountRepository.save(userAccount)).thenReturn(userAccount);

        userAccountService.changePassword(login, null);

        assertFalse(userAccount.getPassword().startsWith("$2a$"));
        assertEquals("123", userAccount.getPassword());

        verify(userAccountRepository).findById(login);
        verify(userAccountRepository).save(userAccount);
    }

    @Test
    void testChangePasswordNotFound(){
        String login = "asd";
        String newPassword =  "1234";

        when(userAccountRepository.findById(login)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,() -> userAccountService.changePassword(login,newPassword));

        verify(userAccountRepository).findById(login);
        verifyNoMoreInteractions(userAccountRepository);
    }

}
