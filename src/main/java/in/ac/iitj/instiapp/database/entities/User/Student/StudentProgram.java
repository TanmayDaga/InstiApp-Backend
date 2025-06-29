package in.ac.iitj.instiapp.database.entities.User.Student;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "student_program")
public class StudentProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long Id;

    @Column( nullable = false)
    String name;

    @Column(nullable = false)
    boolean isActive;

    public StudentProgram(String name, boolean isActive) {
        this.name = name;
        this.isActive = isActive;
    }

    public StudentProgram(Long id) {
        this.Id = id;
    }
}
