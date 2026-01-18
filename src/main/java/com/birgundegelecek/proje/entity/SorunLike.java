package com.birgundegelecek.proje.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "likes",
uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "sorun_id"})})

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class SorunLike {
	
	@Id
	@GeneratedValue(
	        strategy = GenerationType.SEQUENCE,
	        generator = "sorunlike_seq"
	    )
	    @SequenceGenerator(
	        name = "sorunlike_seq",
	        sequenceName = "sorunlike_sequence",
	        allocationSize = 1
	    )

	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sorun_id")
	private Sorun sorun;
	
	
	
	

}
