package test.assignment.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import test.assignment.model.dto.request.DateRangeDto;
import test.assignment.model.dto.request.SaveUserDto;
import test.assignment.model.dto.request.UserUpdatePartialDto;
import test.assignment.model.dto.response.UserResponseDto;
import test.assignment.service.UserService;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    private static final long EXAMPLE_USER_ID = 42L;
    private static final SaveUserDto VALID_SAVE_USER_DTO = new SaveUserDto(
            "testuser@example.com", "John", "Doe",
            LocalDate.of(1990, 1, 1), "123 Main St",
            "123-456-7890");
    private static final UserResponseDto USER_RESPONSE_DTO = new UserResponseDto(
            1L, "testuser@example.com", "John", "Doe",
            LocalDate.of(1990, 1, 1), "123 Main St",
            "123-456-7890");
    private static final DateRangeDto VALID_DATE_RANGE_DTO = new DateRangeDto(
            LocalDate.of(1980, 1, 1),
            LocalDate.of(1990, 12, 31));

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createUser_withValidData_returnsCreatedUser() throws Exception {
        when(userService.createUser(VALID_SAVE_USER_DTO)).thenReturn(USER_RESPONSE_DTO);

        ResultActions response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(VALID_SAVE_USER_DTO)));

        String jsonResponse = response.andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        UserResponseDto actual = objectMapper.readValue(jsonResponse, UserResponseDto.class);
        assertEquals(USER_RESPONSE_DTO, actual);
    }

    @Test
    public void createUser_withInvalidData_returnsBadRequest() throws Exception {
        SaveUserDto saveUserDtoInvalid = new SaveUserDto("invalidEmail.com",
                "John", "Doe", LocalDate.of(1990, 1, 1),
                "123 Main St", "123-456-7890");
        when(userService.createUser(saveUserDtoInvalid)).thenReturn(USER_RESPONSE_DTO);
        String expectedErrorMessage = "email Invalid email format";

        ResultActions response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(saveUserDtoInvalid)));

        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value(expectedErrorMessage));
        verify(userService, never()).createUser(any());
    }

    @ParameterizedTest
    @MethodSource("invalidUserProvider")
    public void createUser_withInvalidData_returnsBadRequest(
            SaveUserDto saveUserDto, String expectedErrorMessage) throws Exception {
        ResultActions response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(saveUserDto)));

        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value(expectedErrorMessage));
        verify(userService, never()).createUser(any());
    }

    private static Stream<Arguments> invalidUserProvider() {
        return Stream.of(
            Arguments.of(new SaveUserDto("invalidEmail.com", "John",
                    "Doe", LocalDate.of(1990, 1, 1),
                    "123 Main St", "123-456-7890"), "email Invalid email format"),
            Arguments.of(new SaveUserDto("", "John", "Doe",
                    LocalDate.of(1990, 1, 1), "123 Main St",
                    "123-456-7890"), "email must not be blank"),
            Arguments.of(new SaveUserDto("john.doe@example.com", "", "Doe",
                    LocalDate.of(1990, 1, 1), "123 Main St",
                    "123-456-7890"), "firstName must not be blank"),
            Arguments.of(new SaveUserDto("john.doe@example.com", "John", "",
                    LocalDate.of(1990, 1, 1), "123 Main St",
                    "123-456-7890"), "lastName must not be blank"),
            Arguments.of(new SaveUserDto("john.doe@example.com", "John", "Doe",
                    null, "123 Main St", "123-456-7890"),
                    "birthDate must not be null"),
            Arguments.of(new SaveUserDto("john.doe@example.com", "John", "Doe",
                    LocalDate.of(2020, 1, 1), "123 Main St",
                    "123-456-7890"), "birthDate The user must be at least 18 years old")
        );
    }

    @Test
    public void getUsersByDate_withValidDateRange_returnsUsers() throws Exception {
        when(userService.getUsersByBirthDateRange(VALID_DATE_RANGE_DTO.from(),
                VALID_DATE_RANGE_DTO.to())).thenReturn(List.of(USER_RESPONSE_DTO));

        ResultActions response = mockMvc.perform(get("/users")
                .param("from", VALID_DATE_RANGE_DTO.from().toString())
                .param("to", VALID_DATE_RANGE_DTO.to().toString())
                .contentType(MediaType.APPLICATION_JSON));

        String jsonResponse = response.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<UserResponseDto> actual = objectMapper.readerForListOf(UserResponseDto.class)
                .readValue((jsonResponse));
        assertEquals(List.of(USER_RESPONSE_DTO), actual);
    }

    @Test
    public void getUsersByDate_withInvalidDateRange_returnsBadRequest() throws Exception {
        String expectedErrorMessage = "The 'from' date must be before the 'to' date";
        LocalDate testDate = LocalDate.of(1990, 1, 1);
        DateRangeDto dateRangeDto = new DateRangeDto(testDate, testDate);

        ResultActions response = mockMvc.perform(get("/users")
                .param("from", dateRangeDto.from().toString())
                .param("to", dateRangeDto.to().toString())
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value(expectedErrorMessage));
        verify(userService, never()).getUsersByBirthDateRange(any(), any());
    }

    @Test
    public void updateUser_withValidData_returnsUpdatedUser() throws Exception {
        when(userService.updateUser(EXAMPLE_USER_ID, VALID_SAVE_USER_DTO))
                .thenReturn(USER_RESPONSE_DTO);

        ResultActions response = mockMvc.perform(put("/users/{id}", EXAMPLE_USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(VALID_SAVE_USER_DTO)));

        String jsonResponse = response.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        UserResponseDto actual = objectMapper.readValue(jsonResponse, UserResponseDto.class);
        assertEquals(USER_RESPONSE_DTO, actual);
    }

    @ParameterizedTest
    @MethodSource("invalidUserProvider")
    public void updateUser_withInvalidData_returnsBadRequest(
            SaveUserDto saveUserDto,
            String expectedErrorMessage) throws Exception {
        ResultActions response = mockMvc.perform(put("/users/{id}", EXAMPLE_USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(saveUserDto)));

        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value(expectedErrorMessage));
        verify(userService, never()).createUser(any());
    }

    @Test
    public void updatePartialUser_withValidData_returnsUpdatedUser() throws Exception {
        UserUpdatePartialDto updatePartialEmailUpdateDto = new UserUpdatePartialDto(
                Optional.of("newemail@update.com"), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty());
        when(userService.updatePartialUser(EXAMPLE_USER_ID, updatePartialEmailUpdateDto))
                .thenReturn(USER_RESPONSE_DTO);

        ResultActions response = mockMvc.perform(patch("/users/{id}", EXAMPLE_USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatePartialEmailUpdateDto)));

        String jsonResponse = response.andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        verify(userService).updatePartialUser(EXAMPLE_USER_ID, updatePartialEmailUpdateDto);
        UserResponseDto actual = objectMapper.readValue(jsonResponse, UserResponseDto.class);
        assertEquals(USER_RESPONSE_DTO, actual);
    }

    private static Stream<Arguments> invalidPartialUpdateProvider() {
        return Stream.of(
                arguments(new UserUpdatePartialDto(Optional.of("invalidEmail.com"),
                        Optional.empty(), Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty()), "email Invalid email format"),
                arguments(new UserUpdatePartialDto(Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.of(LocalDate.now().plusDays(1)),
                        Optional.empty(), Optional.empty()),
                        "birthDate Birth date must be in the past")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidPartialUpdateProvider")
    public void updatePartialUser_withInvalidData_returnsBadRequest(
            UserUpdatePartialDto userUpdatePartialDto,
            String expectedErrorMessage) throws Exception {
        ResultActions response = mockMvc.perform(patch("/users/{id}", EXAMPLE_USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdatePartialDto)));

        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value(expectedErrorMessage));
        verify(userService, never()).updatePartialUser(anyLong(), any());
    }

    @Test
    public void deleteUser_withValidId_returnsNoContent() throws Exception {
        doNothing().when(userService).deleteUser(EXAMPLE_USER_ID);

        ResultActions response = mockMvc.perform(delete("/users/{id}", EXAMPLE_USER_ID)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNoContent());
        verify(userService).deleteUser(EXAMPLE_USER_ID);
    }
}
