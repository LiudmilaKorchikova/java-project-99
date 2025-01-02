package hexlet.code.app.controller;

import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        User admin = new User();
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("adminpassword"));
        userRepository.save(admin);
    }

    @Test
    @WithMockUser(username = "admin@example.com", password = "adminpassword")
    public void testCreateUser() throws Exception {
        String userJson = "{\"email\": \"john@google.com\", \"firstName\": \"John\", \"lastName\": \"Doe\", \"password\": \"password\"}";

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@google.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", password = "adminpassword")
    public void testGetUserById() throws Exception {
        User user = new User();
        user.setEmail("john@google.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword(passwordEncoder.encode("password"));
        user = userRepository.save(user);

        mockMvc.perform(get("/api/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@google.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", password = "adminpassword")
    public void testGetAllUsers() throws Exception {
        User user1 = new User();
        user1.setEmail("john@google.com");
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setPassword(passwordEncoder.encode("password"));
        userRepository.save(user1);

        User user2 = new User();
        user2.setEmail("jack@yahoo.com");
        user2.setFirstName("Jack");
        user2.setLastName("Jons");
        user2.setPassword(passwordEncoder.encode("password"));
        userRepository.save(user2);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.email == 'john@google.com')]").exists())
                .andExpect(jsonPath("$[?(@.email == 'jack@yahoo.com')]").exists());
    }

    @Test
    @WithMockUser(username = "admin@example.com", password = "adminpassword")
    public void testUpdateUser() throws Exception {
        User user = new User();
        user.setEmail("john@google.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword(passwordEncoder.encode("password"));
        user = userRepository.save(user);

        String updatedUserJson = "{\"email\": \"john@newmail.com\", \"password\": \"newpassword\"}";

        mockMvc.perform(put("/api/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedUserJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@newmail.com"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", password = "adminpassword")
    public void testDeleteUser() throws Exception {
        User user = new User();
        user.setEmail("john@google.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword(passwordEncoder.encode("password"));
        user = userRepository.save(user);

        mockMvc.perform(delete("/api/users/" + user.getId()))
                .andExpect(status().isNoContent());
    }
}
