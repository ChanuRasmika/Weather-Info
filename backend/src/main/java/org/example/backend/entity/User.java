package org.example.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "user")
@Getter
@Setter
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;
    private String role;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date created_at;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date updated_at;
    
    @Column(nullable = false)
    private boolean mfaEnabled = false;

    public User(int id, String email, String password, String role, Date created_at, Date updated_at) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }


    public User() {
    }


}
