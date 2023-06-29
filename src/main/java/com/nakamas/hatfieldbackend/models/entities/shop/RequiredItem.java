package com.nakamas.hatfieldbackend.models.entities.shop;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Getter
@Setter
@Table
@Entity
@NoArgsConstructor
public class RequiredItem extends AbstractPersistable<Long> {
    private Integer requiredAmount;
    private Boolean needed = true;
    @Column(columnDefinition = "int default 0")
    private int defectiveCount = 0;

    public RequiredItem(int requiredAmount) {
        this.requiredAmount = requiredAmount;
    }

    public void setNeeded(Boolean need) {
        this.needed = need;
    }

    public void addDefectiveCount(int count) {
        this.defectiveCount += count;
    }

    public void removeDefectiveCount(int count) {
        if (this.defectiveCount - count < 0)
            throw new CustomException("There are not enough defective items to return");
        this.defectiveCount -= count;
    }
}
