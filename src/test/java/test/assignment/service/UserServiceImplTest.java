package test.assignment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import test.assignment.exception.EntityNotFoundException;
import test.assignment.model.User;
import test.assignment.model.dto.request.SaveUserDto;
import test.assignment.model.dto.request.UserUpdatePartialDto;
import test.assignment.model.dto.response.UserResponseDto;
import test.assignment.repository.UserRepository;
import test.assignment.util.UserMapper;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private static final long EXAMPLE_USER_ID = 42L;
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserServiceImpl userService;

    private SaveUserDto validSaveUserDto;
    private User exampleUserWithoutId;
    private User exampleUserWithId;
    private UserResponseDto exampleUserResponseDto;
    private UserUpdatePartialDto updatePartialEmailUpdateDto;

    @BeforeEach
    void setUp() {
        validSaveUserDto = new SaveUserDto("testuser@example.com",
                "John", "Doe", LocalDate.of(1990, 1, 1),
                "123 Main St", "123-456-7890");
        exampleUserWithoutId = new User(null, "testuser@example.com",
                "John", "Doe", LocalDate.of(1990, 1, 1),
                "123 Main St", "123-456-7890");
        exampleUserWithId = new User(EXAMPLE_USER_ID, "testuser@example.com",
                "John", "Doe", LocalDate.of(1990, 1, 1),
                "123 Main St", "123-456-7890");
        exampleUserResponseDto = new UserResponseDto(EXAMPLE_USER_ID, "testuser@example.com",
                "John", "Doe", LocalDate.of(1990, 1, 1),
                "123 Main St", "123-456-7890");
        updatePartialEmailUpdateDto = new UserUpdatePartialDto(
                Optional.of("newemail@update.com"), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty());
    }

    @Test
    public void createUser_withValidUserData_returnsCreatedUser() {
        when(mapper.toModel(validSaveUserDto)).thenReturn(exampleUserWithoutId);
        when(userRepository.save(exampleUserWithoutId)).thenReturn(exampleUserWithId);
        when(mapper.toDto(exampleUserWithId)).thenReturn(exampleUserResponseDto);

        UserResponseDto actual = userService.createUser(validSaveUserDto);

        assertEquals(exampleUserResponseDto, actual);
        verify(mapper).toModel(validSaveUserDto);
        verify(userRepository).save(exampleUserWithoutId);
        verify(mapper).toDto(exampleUserWithId);
    }

    @Test
    public void getUsersByDate_withValidDates_returnsUsers() {
        LocalDate dateFrom = LocalDate.of(1980, 1, 1);
        LocalDate dateTo = LocalDate.of(1990, 1, 1);
        when(userRepository.getUsersByBirthDateBetween(dateFrom, dateTo))
                .thenReturn(List.of(exampleUserWithId));
        when(mapper.toDto(exampleUserWithId)).thenReturn(exampleUserResponseDto);

        List<UserResponseDto> actual = userService.getUsersByBirthDateRange(dateFrom, dateTo);

        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals(exampleUserResponseDto, actual.get(0));
    }

    @Test
    public void updateUser_withValidData_returnsUpdatedUser() {
        when(userRepository.existsById(EXAMPLE_USER_ID)).thenReturn(Boolean.TRUE);
        when(mapper.toModel(validSaveUserDto)).thenReturn(exampleUserWithoutId);
        when(userRepository.save(exampleUserWithId)).thenReturn(exampleUserWithId);
        when(mapper.toDto(exampleUserWithId)).thenReturn(exampleUserResponseDto);

        UserResponseDto actual = userService.updateUser(EXAMPLE_USER_ID, validSaveUserDto);

        assertEquals(exampleUserResponseDto, actual);
        verify(userRepository).existsById(EXAMPLE_USER_ID);
        verify(mapper).toModel(validSaveUserDto);
        verify(userRepository).save(exampleUserWithoutId);
        verify(mapper).toDto(exampleUserWithId);
    }

    @Test
    public void updatePartialUser_withValidData_returnsUpdatedUser() {

        when(userRepository.findById(EXAMPLE_USER_ID))
                .thenReturn(Optional.of(exampleUserWithId));
        User userWithUpdatedEmail = new User(EXAMPLE_USER_ID,
                "newemail@update.com", "John", "Doe",
                LocalDate.of(1990, 1, 1), "123 Main St",
                "123-456-7890");
        when(userRepository.save(userWithUpdatedEmail)).thenReturn(userWithUpdatedEmail);
        when(mapper.toDto(userWithUpdatedEmail)).thenReturn(exampleUserResponseDto);

        UserResponseDto actual = userService.updatePartialUser(EXAMPLE_USER_ID,
                updatePartialEmailUpdateDto);

        assertEquals(exampleUserResponseDto, actual);
        verify(userRepository).findById(EXAMPLE_USER_ID);
        verify(userRepository).save(userWithUpdatedEmail);
        verify(mapper).toDto(userWithUpdatedEmail);
    }

    @Test
    public void deleteUser_withExistingUser_deletesUser() {
        when(userRepository.existsById(EXAMPLE_USER_ID)).thenReturn(true);

        userService.deleteUser(EXAMPLE_USER_ID);

        verify(userRepository).existsById(EXAMPLE_USER_ID);
        verify(userRepository).deleteById(EXAMPLE_USER_ID);
    }

    @Test
    public void deleteUser_withNonExistingUser_throwsEntityNotFoundException() {
        when(userRepository.existsById(EXAMPLE_USER_ID)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(EXAMPLE_USER_ID));

        verify(userRepository).existsById(EXAMPLE_USER_ID);
        verify(userRepository, never()).deleteById(EXAMPLE_USER_ID);
    }

    @Test
    public void updateUser_withNonExistingUser_throwsEntityNotFoundException() {
        when(userRepository.existsById(EXAMPLE_USER_ID)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> userService.updateUser(EXAMPLE_USER_ID, validSaveUserDto));

        verify(userRepository).existsById(EXAMPLE_USER_ID);
        verify(mapper, never()).toModel(any(SaveUserDto.class));
        verify(userRepository, never()).save(any(User.class));
        verify(mapper, never()).toDto(any(User.class));
    }

    @Test
    public void updatePartialUser_withNonExistingUser_throwsEntityNotFoundException() {
        when(userRepository.findById(EXAMPLE_USER_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.updatePartialUser(EXAMPLE_USER_ID, updatePartialEmailUpdateDto));

        verify(userRepository).findById(EXAMPLE_USER_ID);
        verify(userRepository, never()).save(any(User.class));
        verify(mapper, never()).toDto(any(User.class));
    }

}
