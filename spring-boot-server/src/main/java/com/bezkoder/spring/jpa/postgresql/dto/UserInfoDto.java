package com.bezkoder.spring.jpa.postgresql.dto;

public record UserInfoDto(
        Long id,
        String email,
        String uname,
        String firstname,
        String lastname
) {}
