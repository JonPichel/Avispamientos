package models;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Sighting {
    @Id
    @GeneratedValue
    private Long id;
    @Column private String data;
    @Column private double latitude;
    @Column private double longitude;
    @Column private long timestamp;

    @ManyToOne
    private User creator;

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

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public User getCreator() {
        return creator;
    }

    public List<Confirmation> getConfirmations() {
        return confirmations;
    }
}
