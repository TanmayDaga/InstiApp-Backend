package in.ac.iitj.instiapp.database.entities.User;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_type")
public class Usertype {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long Id;

    @Column( nullable = false, unique = true )
    String name;


    public Usertype(Long id) {
        this.Id = id;
    }
}
