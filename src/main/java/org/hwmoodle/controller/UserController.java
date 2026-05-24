package org.hwmoodle.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.hwmoodle.core.dto.ErrorResponse;
import org.hwmoodle.core.dto.UserRequestDto;
import org.hwmoodle.core.dto.UserResponseDto;
import org.hwmoodle.core.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management API")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "List users", description = "Returns all users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of users")
    })
    public List<UserResponseDto> listUsers() {
        return userService.listUsers();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user", description = "Returns a user by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponseDto> getUser(
            @Parameter(description = "User id", required = true)
            @PathVariable Long id
    ) {
        return userService.getUser(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create user", description = "Creates a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Data integrity violation",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto request) {
        UserResponseDto created = userService.createUser(request);
        return ResponseEntity.created(URI.create("/api/users/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Updates an existing user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Data integrity violation",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserResponseDto> updateUser(
            @Parameter(description = "User id", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDto request
    ) {
        return userService.updateUser(id, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deletes a user by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User id", required = true)
            @PathVariable Long id
    ) {
        if (userService.getUser(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

