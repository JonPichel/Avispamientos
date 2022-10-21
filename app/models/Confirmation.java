package models;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;

@Entity
public class Confirmation {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @JsonIgnore
    @ManyToOne
    private Sighting sighting;

    @Column private long timestamp;

    @JsonIgnore
    @ManyToOne
    private User contributor;


    public Confirmation(User contributor, Sighting sighting) {
        this.sighting = sighting;
        this.timestamp= Instant.now().toEpochMilli();
        this.contributor = contributor;
    }

    public Confirmation() {

    }

    public String getId() {
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

    @JsonProperty("sighting")
    public String getSightingId() {
        return sighting.getId();
    }

    @JsonProperty("contributor")
    public String getContributorUsername() {
        return contributor.getUsername();
    }
}
