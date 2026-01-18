package com.birgundegelecek.proje.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "username")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_seq"
        )
        @SequenceGenerator(
            name = "user_seq",
            sequenceName = "user_sequence",
            allocationSize = 1
        )
 
    private Long id;

    @Column(unique = true, nullable = false) 
    private String username;

    @Column(nullable = false) 
    private String password;

    private String role; 

    @OneToMany(mappedBy = "user" , fetch = FetchType.LAZY , orphanRemoval = false)
	private Set<SorunLike> sorunLikes;

	
}
