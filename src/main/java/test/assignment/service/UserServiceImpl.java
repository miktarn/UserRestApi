package test.assignment.service;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import test.assignment.exception.EntityNotFoundException;
import test.assignment.model.User;
import test.assignment.model.dto.request.SaveUserDto;
import test.assignment.model.dto.request.UserUpdatePartialDto;
import test.assignment.model.dto.response.UserResponseDto;
import test.assignment.repository.UserRepository;
import test.assignment.util.UserMapper;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private UserMapper mapper;

    @Override
    public UserResponseDto createUser(SaveUserDto newUserDto) {
        User newUser = mapper.toModel(newUserDto);
        return mapper.toDto(userRepository.save(newUser));
    }

    @Override
    public List<UserResponseDto> getUsersByBirthDateRange(LocalDate from, LocalDate to) {
        return userRepository.getUsersByBirthDateBetween(from, to).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public UserResponseDto updateUser(Long id, SaveUserDto updateUserDto) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with ID: " + id);
        }
        User updatedUser = mapper.toModel(updateUserDto);
        updatedUser.setId(id);
        return mapper.toDto(userRepository.save(updatedUser));
    }

    @Override
    public UserResponseDto updatePartialUser(Long id,
                                             UserUpdatePartialDto userUpdatePartialDto) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User not found with ID: " + id)
        );
        userUpdatePartialDto.email().ifPresent(user::setEmail);
        userUpdatePartialDto.firstName().ifPresent(user::setFirstName);
        userUpdatePartialDto.lastName().ifPresent(user::setLastName);
        userUpdatePartialDto.birthDate().ifPresent(user::setBirthDate);
        userUpdatePartialDto.address().ifPresent(user::setAddress);
        userUpdatePartialDto.phoneNumber().ifPresent(user::setPhoneNumber);

        return mapper.toDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User with id " + id + " not found.");
        }
        userRepository.deleteById(id);
    }
}
