package com.birgundegelecek.proje.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.type.TrueFalseConverter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
	    name = "kategori",
	    uniqueConstraints = {
	        @UniqueConstraint(columnNames = "ad")
	    }
	)

public class Kategori {
	
	@Id
	@GeneratedValue(
	        strategy = GenerationType.SEQUENCE,
	        generator = "kategori_seq"
	    )
	    @SequenceGenerator(
	        name = "kategori_seq",
	        sequenceName = "kategori_sequence",
	        allocationSize = 100
	    )

	private Long id;
	
	@Column(unique = true , nullable = false)
	private String ad;
	
	@OneToMany(mappedBy = "kategori" , cascade = CascadeType.ALL , fetch = FetchType.LAZY)
	private List<Sorun> sorunlar = new ArrayList<>();

}
