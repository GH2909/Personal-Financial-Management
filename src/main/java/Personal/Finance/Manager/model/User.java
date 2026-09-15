package Personal.Finance.Manager.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "fullname", length = 100, nullable = false)
    private String fullname;

    @Column(name = "email", length = 225, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 225, nullable = false)
    private String password;

    public User() {
    }

    public User(String fullname, String email, String password) {
        this.fullname = fullname;
        this.email = email;
        this.password = password;
    }
}