package com.nakamas.hatfieldbackend.models.entities;

import com.nakamas.hatfieldbackend.config.AttributeEncryptor;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_phones")
@Entity
public class UserPhone extends AbstractPersistable<Long> {
    @Convert(converter = AttributeEncryptor.class)
    private String phone;
    @Convert(converter = AttributeEncryptor.class)
    private String code = "";
    @Column(name = "user_id")
    private UUID userId;

    public String getPhoneWithCode() {
        return "%s-%s".formatted(code, phone);
    }

    public UserPhone(String phone) {
        String[] phoneSplit = phone.split("-");
        if (phoneSplit.length  == 2) {
            this.code = phoneSplit[0];
            this.phone = phoneSplit[1];
        } else if (phone.startsWith("0")) {
            this.phone = phone.substring(1);
            this.code = "+44";
        } else {
            this.phone = phone;
        }
    }

    public static String extractPhoneNumber(String phone) {
        return new UserPhone(phone).getPhone();
    }

    public String getComparableString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getCode());
        stringBuilder.append(getPhone());
        return stringBuilder.toString().replace("+", "");
    }
}
