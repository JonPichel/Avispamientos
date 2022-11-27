package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    @Transient
    final Logger logger = LoggerFactory.getLogger(this.getClass());

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

    public int distanceToCoords(double latitude, double longitude) {
        double lat2 = latitude * Math.PI / 180.0;
        double lat1 = this.latitude * Math.PI / 180.0;
        double dLat = (latitude - this.latitude) * Math.PI / 180.0;
        double dLon = (longitude - this.longitude) * Math.PI / 180.0;
        double a = Math.pow(Math.sin(dLat/2), 2) +
            Math.cos(lat2) * Math.cos(lat1) * Math.pow(Math.sin(dLon/2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        logger.warn("Testing 2");
        logger.warn(Double.toString(6371_000 * c));
        return (int)Math.round(6371_000 * c);
    }
}
