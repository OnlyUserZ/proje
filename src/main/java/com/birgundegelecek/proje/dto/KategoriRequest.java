package com.birgundegelecek.proje.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KategoriRequest {

    @NotBlank(message = "Kategori adı boş olamaz")
    @Size(min = 2, max = 30, message = "Kategori adı 2-30 karakter arasında olmalı")
    private String ad;

}