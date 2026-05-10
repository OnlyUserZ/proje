package com.birgundegelecek.proje.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Version;
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

public class Adres {
	
	@Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "adres_seq"
    )
    @SequenceGenerator(
            name = "adres_seq",
            sequenceName = "adres_sequence",
            allocationSize = 1
    )
	private Long id;
	
	@Column(nullable = false)
	private String il;
	
	@Column(nullable = false)
	private String ilce;
	
	@Column(nullable = false)
	private String mahalle;
	
	@Column(nullable = false)
	private String caddeSokak;
	
	@Column(nullable = false)
	private int binaNo;
	
	@Version
	private Long version;
	
	@Column(nullable = false)
	private int katNo;
	
	@Column(nullable = false)
	private int daireNo;
	
	@Column(nullable = false)
	private String adresTarifi;
	
	@Column(nullable = false)
	private String adresBasligi;
	
	@OneToOne
	@JoinColumn(name = "adresSahibi")
	private User adresSahibi;

}
