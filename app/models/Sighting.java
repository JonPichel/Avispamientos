package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Sighting {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    @Column private String data;
    @Column private double latitude;
    @Column private double longitude;
    @Column private long timestamp;

    @JsonIgnore
    @ManyToOne
    private User creator;

    @JsonIgnore
    @OneToMany(mappedBy = "sighting", fetch = FetchType.EAGER)
    private List<Confirmation> confirmations;

    public Sighting(String data, double latitude, double longitude, User creator) {
        this.data = data;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = Instant.now().toEpochMilli();
        this.creator = creator;
        this.confirmations = new ArrayList<>();
    }

    public Sighting() {

    }

    public String getId() { return id; }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @JsonIgnore
    public User getCreator() {
        return creator;
    }

    public List<Confirmation> getConfirmations() {
        return confirmations;
    }

    @JsonProperty("creator")
    public String getCreatorUsername() {
        return creator.getUsername();
    }

    @JsonProperty("confirmations")
    public List<String> getConfirmationIds() {
        return confirmations.stream().map(Confirmation::getId).collect(Collectors.toList());
    }
}
