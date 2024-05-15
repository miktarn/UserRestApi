package test.assignment.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import test.assignment.util.validation.annotation.Adult;

public record SaveUserDto(
        @Email(message = "Invalid email format") @NotBlank String email,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotNull @Adult LocalDate birthDate,
        String address,
        String phoneNumber
) {
}

