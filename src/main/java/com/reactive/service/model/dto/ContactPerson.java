package com.reactive.service.model.dto;

import lombok.*;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ContactPerson {
    private String name;
    private String surname;
    private String jobPosition;
    private String email;
    private String phone;
}
