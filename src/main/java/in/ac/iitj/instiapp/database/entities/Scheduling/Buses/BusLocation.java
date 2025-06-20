package in.ac.iitj.instiapp.database.entities.Scheduling.Buses;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bus_location")
public class BusLocation {


    public BusLocation(String location) {
        this.name = location;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long Id;


    @Column(nullable = false, unique = true)
    String name;
}
