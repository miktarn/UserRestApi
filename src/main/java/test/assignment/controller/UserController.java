package test.assignment.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import test.assignment.model.dto.request.DateRangeDto;
import test.assignment.model.dto.request.SaveUserDto;
import test.assignment.model.dto.request.UserUpdatePartialDto;
import test.assignment.model.dto.response.UserResponseDto;
import test.assignment.service.UserService;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    @PostMapping
    public UserResponseDto createUser(@RequestBody @Valid SaveUserDto newUserDto) {
        return userService.createUser(newUserDto);
    }

    @GetMapping
    public List<UserResponseDto> getUsersByBirthDateRange(@ModelAttribute @Valid DateRangeDto
                                                                      dateRangeDto) {
        return userService.getUsersByBirthDateRange(dateRangeDto.from(), dateRangeDto.to());
    }

    @PutMapping("/{id}")
    public UserResponseDto updateUser(@PathVariable Long id,
                                      @RequestBody @Valid SaveUserDto userUpdateDto) {
        return userService.updateUser(id, userUpdateDto);
    }

    @PatchMapping("/{id}")
    public UserResponseDto updatePartialUser(@PathVariable Long id,
                                             @RequestBody @Valid UserUpdatePartialDto
                                                     userUpdatePartialDto) {
        return userService.updatePartialUser(id, userUpdatePartialDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

}
