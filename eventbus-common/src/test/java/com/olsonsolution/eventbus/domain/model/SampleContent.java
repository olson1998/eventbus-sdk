package com.olsonsolution.eventbus.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SampleContent {

    private String name;

    private String surname;

    private LocalDate birthday;

    private int age;

    private ZonedDateTime lastLoginTimestamp;

    private List<String> nicknames;

}
