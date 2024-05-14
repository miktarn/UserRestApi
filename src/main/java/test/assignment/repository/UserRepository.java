package test.assignment.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import test.assignment.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> getUsersByBirthDateBetween(LocalDate from, LocalDate to);
}
