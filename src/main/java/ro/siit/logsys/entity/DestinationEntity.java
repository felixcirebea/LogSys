package ro.siit.logsys.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity(name = "destinations")
@Data
public class DestinationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private String name;
    private Integer distance;
    @OneToMany(mappedBy = "destination", cascade = CascadeType.ALL)
    private List<OrderEntity> orders;

}
