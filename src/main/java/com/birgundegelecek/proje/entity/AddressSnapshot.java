package com.birgundegelecek.proje.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressSnapshot {

    private String il;
    private String ilce;
    private String mahalle;
    private String caddeSokak;
    private int binaNo;
    private int katNo;
    private int daireNo;
    private String adresTarifi;
    private String adresBasligi;
    
}