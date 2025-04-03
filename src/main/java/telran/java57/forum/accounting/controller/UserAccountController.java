package telran.java57.forum.accounting.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import telran.java57.forum.accounting.dto.RolesDto;
import telran.java57.forum.accounting.dto.UpdateUserDto;
import telran.java57.forum.accounting.dto.UserDto;
import telran.java57.forum.accounting.dto.UserRegisterDto;
import telran.java57.forum.accounting.service.UserAccountService;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class UserAccountController {
    final UserAccountService userAccountService;


    @PostMapping("/register")
    public UserDto register(@RequestBody UserRegisterDto userRegisterDto){
        return userAccountService.register(userRegisterDto);
    }


    @DeleteMapping("/user/{login}")
    public UserDto removeUser(@PathVariable String login){
        return userAccountService.removeUser(login);
    }


    @GetMapping("/user/{login}")
    public UserDto getUser(@PathVariable String login){
        return userAccountService.getUser(login);
    }


    @PutMapping("/user/{login}")
    public UserDto updateUser(@PathVariable String login, @RequestBody UpdateUserDto updateUserDto){
        return userAccountService.updateUser(login,updateUserDto);
    }


    @PutMapping("/user/{login}/role/{role}")
    public RolesDto addRole(@PathVariable String login, @PathVariable String role){
        return userAccountService.changeRolesList(login,role,true);
    }


    @DeleteMapping("/user/{login}/role/{role}")
    public RolesDto removeRole(@PathVariable String login, @PathVariable String role){
        return userAccountService.changeRolesList(login,role,false);
    }


    @PostMapping("/login")
    public UserDto login(Principal principal){
        return userAccountService.getUser(principal.getName());
    }


    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(Principal principal, @RequestHeader("X-Password") String newPassword){
        userAccountService.changePassword(principal.getName(), newPassword);
    }

}
