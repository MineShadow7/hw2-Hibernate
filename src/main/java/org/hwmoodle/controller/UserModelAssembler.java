package org.hwmoodle.controller;

import org.hwmoodle.core.dto.UserResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<UserResponseDto, EntityModel<UserResponseDto>> {

    @Override
    public EntityModel<UserResponseDto> toModel(UserResponseDto user) {
        return EntityModel.of(
                user,
                linkTo(methodOn(UserController.class).getUser(user.id())).withSelfRel(),
                linkTo(methodOn(UserController.class).listUsers()).withRel("users")
        );
    }
}

