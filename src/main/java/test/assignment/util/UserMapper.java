package test.assignment.util;

import org.mapstruct.Mapper;
import test.assignment.configuration.MapperConfig;
import test.assignment.model.User;
import test.assignment.model.dto.request.SaveUserDto;
import test.assignment.model.dto.response.UserResponseDto;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    User toModel(SaveUserDto saveUserDto);

    UserResponseDto toDto(User user);
}
