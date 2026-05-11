package com.birgundegelecek.proje;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.ToString;

@Getter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class ApiErrorResponse {

    private final int status;
    private final String error;
    private final String message;
    private final String path;

    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();
}