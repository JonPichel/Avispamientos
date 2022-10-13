package models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User {
    @Id
    private String username;
    @Column private String password;

    @OneToMany(mappedBy = "creator", fetch = FetchType.EAGER)
    private List<Sighting> sightings;

    @OneToMany (mappedBy = "contributor", fetch = FetchType.EAGER)
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
}
