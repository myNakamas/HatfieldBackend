package com.nakamas.hatfieldbackend.models.entities.shop;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Getter
@Setter
@NoArgsConstructor
@Table
@Entity
public class DeviceLocation extends AbstractPersistable<Long> {
    private String location;

    public DeviceLocation (String loc){
        this.location = loc;
    }
}
