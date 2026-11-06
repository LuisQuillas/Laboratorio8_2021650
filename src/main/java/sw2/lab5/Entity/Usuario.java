package sw2.lab5.Entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user")
public class Usuario implements Serializable {
    @Id
    private int id_user;

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Rol getId_role() {
        return id_role;
    }

    public void setId_role(Rol id_role) {
        this.id_role = id_role;
    }

    @Column(nullable = false)
    private String firstname;
    @Column(nullable = false)
    private String lastname;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;

    private String intro;
    private String profile;
    @Column(nullable = false)
    private boolean active;
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_role", nullable = false)
    private Rol id_role;
}
