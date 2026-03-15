package com.birgundegelecek.proje.entity;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;

import jakarta.persistence.Entity;
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

@Entity
@Table(
    name = "sepet_urun",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"usersepet_id", "urun_id"})
    }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SoftDelete(strategy = SoftDeleteType.DELETED, columnName = "deleted")
@DynamicUpdate
@Where(clause = "deleted = false") 
public class SepetUrun {
	
	@Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "sepeturun_seq"
    )
    @SequenceGenerator(
            name = "sepeturun_seq",
            sequenceName = "sepeturun_sequence",
            allocationSize = 1
    )
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "usersepet_id")
	private UserSepet userSepet;
	
	@ManyToOne
	@JoinColumn(name = "urun_id")
	private Urun urun;

}