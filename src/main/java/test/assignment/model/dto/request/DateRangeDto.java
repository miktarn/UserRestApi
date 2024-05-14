package test.assignment.model.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import test.assignment.util.validation.annotation.ValidDateRange;

@ValidDateRange
public record DateRangeDto(
        @NotNull @Past LocalDate from,
        @NotNull @PastOrPresent LocalDate to
) {
}
