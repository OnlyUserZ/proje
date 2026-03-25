package com.birgundegelecek.proje.entity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SoftDelete(strategy = SoftDeleteType.DELETED , columnName = "deleted")
@DynamicUpdate

public class Urun {
	
	@Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "urun_seq"
    )
    @SequenceGenerator(
            name = "urun_seq",
            sequenceName = "urun_sequence",
            allocationSize = 1
    )
	private long id;
	
	@Column(unique = true , nullable = false)
	private String ad; 
	
	private String aciklama;
	
	@Column(nullable = false)
	private BigDecimal fiyat;
	
	@Column(nullable = false)
	private int stok = 0;
	
	@Column(name = "rezerve_stok",nullable = false)
	private int rezerveStok = 0;
	
	@Column(nullable = false)
	private int aktifStok;
	
	@Column(name = "deleted", nullable = false)
	private boolean deleted = false;
	
	@OneToMany(mappedBy = "urun")
	private Set<SiparisUrun> siparisUrun = new HashSet<>();
	
	@ManyToMany
	@JoinTable(name = "urun_kategori",
	           joinColumns = @JoinColumn(name = "urun_id"),
	           inverseJoinColumns = @JoinColumn(name = "kategori_id")
	)
	private Set<Kategori> kategoriler = new HashSet<>();
	
	@OneToMany(mappedBy = "urun")
	private Set<SepetUrun> sepetUruns = new HashSet<>();
	
	public int getAktifStok() {
		return stok - rezerveStok;
	}
	
}
