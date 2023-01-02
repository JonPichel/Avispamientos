package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class User {
    @Id
    private String username;
    @Column private String password;

    @JsonIgnore
    @OneToMany(mappedBy = "creator", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Sighting> sightings;

    @JsonIgnore
    @OneToMany(mappedBy = "contributor", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Confirmation> confirmations;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.sightings = new ArrayList<>();
        this.confirmations = new ArrayList<>();
    }

    public User() {

    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<Sighting> getSightings() {
        return sightings;
    }

    public List<Confirmation> getConfirmations() {
        return confirmations;
    }

    public void addSighting(Sighting sighting) {
        sightings.add(sighting);
    }

    @JsonProperty("sightings")
    public List<String> getSightingIds() {
        return sightings.stream().map(Sighting::getId).collect(Collectors.toList());
    }

    @JsonProperty("confirmations")
    public List<String> getConfirmationIds() {
        return confirmations.stream().map(Confirmation::getId).collect(Collectors.toList());
    }
}
