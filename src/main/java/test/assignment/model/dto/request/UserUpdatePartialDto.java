package test.assignment.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import java.util.Optional;

public record UserUpdatePartialDto(
        Optional<@Email(message = "Invalid email format") String> email,
        Optional<String> firstName,
        Optional<String> lastName,
        Optional<@Past(message = "Birth date must be in the past") LocalDate> birthDate,
        Optional<String> address,
        Optional<String> phoneNumber
) {
}
