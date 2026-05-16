package com.birgundegelecek.proje.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;

import com.birgundegelecek.proje.status.SiparisStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.PackagePrivate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@SoftDelete(strategy = SoftDeleteType.DELETED , columnName = "deleted")
public class Siparis {
	
	@Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "siparis_seq"
    )
    @SequenceGenerator(
            name = "siparis_seq",
            sequenceName = "siparis_sequence",
            allocationSize = 1
    )
	private long id;
	
	@Column(nullable = false)
	private LocalDateTime created_at;
	
	@Column(nullable = false)
	private BigDecimal toplamFiyat;
	
	@Column(unique = true )
	private String siparisKodu;
	
	@Embedded
	private AddressSnapshot adres;
	
	@ManyToOne
	@JoinColumn(name = "sahip")
	private User sahip;
	
	@OneToMany(mappedBy = "siparis" , cascade = CascadeType.ALL , orphanRemoval = true)
	private List<SiparisUrun> siparisurun;
	
	@Enumerated(EnumType.STRING)
	private SiparisStatus status;

}
