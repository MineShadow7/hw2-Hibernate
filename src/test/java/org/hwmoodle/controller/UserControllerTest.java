package org.hwmoodle.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hwmoodle.dto.UserRequestDto;
import org.hwmoodle.dto.UserResponseDto;
import org.hwmoodle.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().findAndRegisterModules();
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        UserController controller = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new RestExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .setValidator(validator)
                .build();
    }

    @Test
    void listUsersReturnsData() throws Exception {
        List<UserResponseDto> users = List.of(
                new UserResponseDto(1L, "Alice", "alice@example.com", 25, LocalDateTime.now())
        );
        when(userService.listUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].email").value("alice@example.com"));
    }

    @Test
    void getUserReturnsNotFoundWhenMissing() throws Exception {
        when(userService.getUser(10L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/10"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserReturnsUserWhenFound() throws Exception {
        UserResponseDto response = new UserResponseDto(2L, "Bob", "bob@example.com", 30, LocalDateTime.now());
        when(userService.getUser(2L)).thenReturn(Optional.of(response));

        mockMvc.perform(get("/api/users/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("Bob"));
    }

    @Test
    void createUserReturnsCreated() throws Exception {
        UserRequestDto request = new UserRequestDto("Carol", "carol@example.com", 28);
        UserResponseDto response = new UserResponseDto(5L, "Carol", "carol@example.com", 28, LocalDateTime.now());
        when(userService.createUser(any(UserRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/users/5"))
                .andExpect(jsonPath("$.id").value(5L));
    }

    @Test
    void createUserReturnsValidationError() throws Exception {
        UserRequestDto request = new UserRequestDto("", "bad", -1);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.age").exists());
    }

    @Test
    void updateUserReturnsNotFound() throws Exception {
        UserRequestDto request = new UserRequestDto("Dan", "dan@example.com", 21);
        when(userService.updateUser(7L, request)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/users/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUserReturnsUser() throws Exception {
        UserRequestDto request = new UserRequestDto("Eve", "eve@example.com", 22);
        UserResponseDto response = new UserResponseDto(3L, "Eve", "eve@example.com", 22, LocalDateTime.now());
        when(userService.updateUser(3L, request)).thenReturn(Optional.of(response));

        mockMvc.perform(put("/api/users/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L));
    }

    @Test
    void deleteUserReturnsNotFound() throws Exception {
        when(userService.getUser(11L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/users/11"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUserReturnsNoContent() throws Exception {
        when(userService.getUser(12L)).thenReturn(Optional.of(
                new UserResponseDto(12L, "Frank", "frank@example.com", 33, LocalDateTime.now())
        ));

        mockMvc.perform(delete("/api/users/12"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(12L);
    }
}
