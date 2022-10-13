package models;
import javax.persistence.*;
import java.time.Instant;

@Entity
public class Confirmation {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Sighting sighting;

    @Column private long timestamp;

    @ManyToOne
    private User contributor;


    public Confirmation(User contributor, Sighting sighting) {
        this.sighting = sighting;
        this.timestamp= Instant.now().toEpochMilli();
        this.contributor = contributor;
    }

    public Confirmation() {

    }

    public Long getId() {
        return id;
    }

    public Sighting getSighting() {
        return sighting;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public User getContributor() {
        return contributor;
    }
}
