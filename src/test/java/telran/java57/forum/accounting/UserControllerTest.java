package telran.java57.forum.accounting;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import telran.java57.forum.accounting.dao.UserAccountRepository;
import telran.java57.forum.accounting.dto.UpdateUserDto;
import telran.java57.forum.accounting.model.UserAccount;
import java.util.Base64;
import java.util.Optional;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


// To make these tests work, set the environment variable:
// MONGODB_URI={your_database_connection_string}
// It's recommended to use a separate test database, as these tests will delete all data from the specified database.
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void cleanMongoDB() {
        userAccountRepository.deleteAll();
    }

    @Test
    void testRegister() throws Exception {
        String json = """
                    {
                      "login": "user",
                      "password": "1234",
                      "firstName": "John",
                      "lastName": "Smith"
                    }
                """;

        MockHttpServletRequestBuilder requestBuilder = post("/account/register")
                .servletPath("/account/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("user"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.roles", containsInAnyOrder("USER")));
    }

    @Test
    void testRegisterExists() throws Exception {
        userAccountRepository.save(new UserAccount("user", "1234", "John", "Smith"));

        String json = """
                    {
                      "login": "user",
                      "password": "1234",
                      "firstName": "John",
                      "lastName": "Smith"
                    }
                """;

        MockHttpServletRequestBuilder requestBuilder = post("/account/register")
                .servletPath("/account/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isConflict());
    }

    @Test
    void testRegisterWrongLogin() throws Exception {
        String json = """
                    {
                      "login": "user@",
                      "password": "1234",
                      "firstName": "John",
                      "lastName": "Smith"
                    }
                """;

        MockHttpServletRequestBuilder requestBuilder = post("/account/register")
                .servletPath("/account/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder).andExpect(status().isConflict());
    }

    @Test
    void testGetUser() throws Exception {
        userAccountRepository.save(new UserAccount("user", passwordEncoder.encode("1234"), "John", "Smith"));

        MockHttpServletRequestBuilder requestBuilder = get("/account/user/user")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:1234".getBytes()));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("user"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.roles", containsInAnyOrder("USER")));
    }

    @Test
    void testGetUserNotFound() throws Exception {
        userAccountRepository.save(new UserAccount("user", passwordEncoder.encode("1234"), "John", "Smith"));

        MockHttpServletRequestBuilder requestBuilder = get("/account/user/user@")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:1234".getBytes()));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    void testRemoveUser() throws Exception {
        userAccountRepository.save(new UserAccount("user", passwordEncoder.encode("1234"), "John", "Smith"));

        MockHttpServletRequestBuilder requestBuilder = delete("/account/user/user")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:1234".getBytes()));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("user"));

    }

    @Test
    void testRemoveUserNotExists() throws Exception {
        userAccountRepository.save(new UserAccount("user", passwordEncoder.encode("1234"), "John", "Smith"));

        MockHttpServletRequestBuilder requestBuilder = delete("/account/user/user@")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:1234".getBytes()));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden());

    }

    @Test
    void testUpdateUser() throws Exception {
        userAccountRepository.save(new UserAccount("user", passwordEncoder.encode("1234"), "John", "Smith"));
        UpdateUserDto dto = new UpdateUserDto("NewFirst", "NewLast");

        MockHttpServletRequestBuilder requestBuilder = put("/account/user/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:1234".getBytes()));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("NewFirst"))
                .andExpect(jsonPath("$.lastName").value("NewLast"));
    }

    @Test
    void testUpdateUserNotFound() throws Exception {
        userAccountRepository.save(new UserAccount("user", passwordEncoder.encode("1234"), "John", "Smith"));
        UpdateUserDto dto = new UpdateUserDto("NewFirst", "NewLast");

        MockHttpServletRequestBuilder requestBuilder = put("/account/user/user@")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:1234".getBytes()));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateUserOneOfThemNull() throws Exception {
        userAccountRepository.save(new UserAccount("user", passwordEncoder.encode("1234"), "John", "Smith"));
        UpdateUserDto dto = new UpdateUserDto(null, "NewLast");

        MockHttpServletRequestBuilder requestBuilder = put("/account/user/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:1234".getBytes()));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("NewLast"));
    }

    @Test
    void testAddRole() throws Exception {
        UserAccount account = new UserAccount("user", passwordEncoder.encode("1234"), "John", "Smith");
        account.addRole("ADMINISTRATOR");
        userAccountRepository.save(account);

        MockHttpServletRequestBuilder requestBuilder = put("/account/user/user/role/ADMINISTRATOR")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:1234".getBytes()));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("user"))
                .andExpect(jsonPath("$.roles", containsInAnyOrder("USER", "ADMINISTRATOR")));

    }

    @Test
    void testDeleteRole() throws Exception {
        UserAccount account = new UserAccount("user", passwordEncoder.encode("1234"), "John", "Smith");
        account.addRole("USER");
        account.addRole("ADMINISTRATOR");
        userAccountRepository.save(account);

        MockHttpServletRequestBuilder requestBuilder = delete("/account/user/user/role/ADMINISTRATOR")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:1234".getBytes()));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("user"))
                .andExpect(jsonPath("$.roles", not(containsInAnyOrder("MODERATOR"))));
    }

    @Test
    void testChangePassword() throws Exception {
        userAccountRepository.save(new UserAccount("user", passwordEncoder.encode("1234"), "John", "Smith"));

        MockHttpServletRequestBuilder requestBuilder = put("/account/password")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:1234".getBytes()))
                .header("X-Password", "12");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent());

        Optional<UserAccount> updated = userAccountRepository.findById("user");
        assertTrue(updated.isPresent());
        assertTrue(updated.get().getPassword().startsWith("$2a$"));
    }
}