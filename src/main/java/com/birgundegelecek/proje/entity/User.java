package com.birgundegelecek.proje.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;
import org.hibernate.type.TrueFalseConverter;

import com.birgundegelecek.proje.status.UserStatus;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@SoftDelete(
        strategy = SoftDeleteType.DELETED,
        columnName = "deleted"     
)
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;
    
    @OneToMany(mappedBy = "sahip" , fetch = FetchType.LAZY , orphanRemoval = true)
    private List<Siparis> siparisler = new ArrayList<>();
    
    @OneToOne(mappedBy = "user" , fetch = FetchType.LAZY , orphanRemoval = true , cascade = CascadeType.ALL)
    private UserSepet userSepet;
}