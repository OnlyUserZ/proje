package com.birgundegelecek.proje.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "kategori")
@SoftDelete(strategy = SoftDeleteType.DELETED , columnName = "deleted")
public class Kategori {
	
	@Id
	@GeneratedValue(
	        strategy = GenerationType.SEQUENCE,
	        generator = "kategori_seq"
	    )
	    @SequenceGenerator(
	        name = "kategori_seq",
	        sequenceName = "kategori_sequence",
	        allocationSize = 1
	    )

	private Long id;
	
	@Column(unique = true , nullable = false)
	private String ad;
	
	@ManyToMany(mappedBy = "kategoriler")
	private Set<Urun> urunler = new HashSet<>();
	
}
