package com.birgundegelecek.proje.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KategoriRequest {

    @NotBlank(message = "Kategori adı boş olamaz")
    @Size(min = 2, max = 30, message = "Kategori adı 2-30 karakter arasında olmalı")
    private String ad;

}