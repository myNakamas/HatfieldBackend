package com.nakamas.hatfieldbackend.models.entities.ticket;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
@Table
public class Model {
//    todo: Make into separate tables
    private String model;
}
