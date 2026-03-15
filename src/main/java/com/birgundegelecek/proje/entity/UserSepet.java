package com.birgundegelecek.proje.entity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SoftDelete(strategy = SoftDeleteType.DELETED , columnName = "deleted")
@DynamicUpdate
public class UserSepet {
	
	@Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "usersepet_seq"
    )
    @SequenceGenerator(
            name = "usersepet_seq",
            sequenceName = "usersepet_sequence",
            allocationSize = 1
    )
	private long id;
	
	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@Column(nullable = false)
	private BigDecimal toplam_fiyat;
	
	@OneToMany
	private Set<SepetUrun> sepetUruns = new HashSet<>();
	
	

}
