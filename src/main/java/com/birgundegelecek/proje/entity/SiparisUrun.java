package com.birgundegelecek.proje.entity;

import java.math.BigDecimal;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@DynamicUpdate
@SoftDelete(strategy = SoftDeleteType.DELETED , columnName = "deleted")
public class SiparisUrun {
	
	@Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "siparisurun_seq"
    )
    @SequenceGenerator(
            name = "siparisurun_seq",
            sequenceName = "siparisurun_sequence",
            allocationSize = 1
    )
	private long id;
	
	@ManyToOne
	@Column(nullable = false)
	@JoinColumn(name = "siparis_id")
	private Siparis siparis;
	
	@ManyToOne
	@Column(nullable = false)
	@JoinColumn(name = "urun_id")
	private Urun urun;
	
	@Column(nullable = false)
	private int adet;
	
	@Column(nullable = false)
	private BigDecimal toplam_fiyat;
	

}
