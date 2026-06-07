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
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management API")
public class UserController {
    private final UserService userService;
    private final UserModelAssembler userModelAssembler;

    public UserController(UserService userService, UserModelAssembler userModelAssembler) {
        this.userService = userService;
        this.userModelAssembler = userModelAssembler;
    }

    @GetMapping
    @Operation(summary = "List users", description = "Returns all users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of users")
    })
    public CollectionModel<EntityModel<UserResponseDto>> listUsers() {
        List<EntityModel<UserResponseDto>> users = userService.listUsers().stream()
                .map(userModelAssembler::toModel)
                .toList();
        return CollectionModel.of(
                users,
                linkTo(methodOn(UserController.class).listUsers()).withSelfRel()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user", description = "Returns a user by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<EntityModel<UserResponseDto>> getUser(
            @Parameter(description = "User id", required = true)
            @PathVariable Long id
    ) {
        return userService.getUser(id)
                .map(userModelAssembler::toModel)
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
    public ResponseEntity<EntityModel<UserResponseDto>> createUser(@Valid @RequestBody UserRequestDto request) {
        UserResponseDto created = userService.createUser(request);
        EntityModel<UserResponseDto> model = userModelAssembler.toModel(created);
        return ResponseEntity.created(URI.create("/api/users/" + created.id())).body(model);
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
    public ResponseEntity<EntityModel<UserResponseDto>> updateUser(
            @Parameter(description = "User id", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDto request
    ) {
        return userService.updateUser(id, request)
                .map(userModelAssembler::toModel)
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

