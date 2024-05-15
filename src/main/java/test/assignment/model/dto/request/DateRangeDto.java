package test.assignment.model.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import test.assignment.util.validation.annotation.ValidDateRange;

@ValidDateRange
public record DateRangeDto(
        @NotNull LocalDate from,
        @NotNull LocalDate to
) {
}
