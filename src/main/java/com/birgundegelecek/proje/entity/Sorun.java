package com.birgundegelecek.proje.entity;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Sorun {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String baslik;
	
	private String sorun;
	
	private String cozum;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kategori_id")
	private Kategori kategori;
	
	@OneToMany(mappedBy = "Sorun" , cascade = CascadeType.ALL)
	private Set<SorunLike> sorunLike;


}
