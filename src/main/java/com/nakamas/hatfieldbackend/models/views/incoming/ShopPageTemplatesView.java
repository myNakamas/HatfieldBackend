package com.nakamas.hatfieldbackend.models.views.incoming;

import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.entities.shop.ShopPageTemplates;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ShopPageTemplatesView {
    @Column(columnDefinition = "text")
    private String aboutPage;

    public ShopPageTemplatesView(ShopPageTemplates templates) {
        this(templates != null ? templates.getAboutPage() : "");
    }

    public void fillTemplates(Shop shop) {
        this.aboutPage = aboutPage.replace("${shop.name}", shop.getShopName());
        this.aboutPage = aboutPage.replace("${shop.phone}", shop.getPhone());
        this.aboutPage = aboutPage.replace("${shop.address}", shop.getAddress());
        this.aboutPage = aboutPage.replace("${shop.email}", shop.getEmail());
    }

    public String getAboutPage() {
        return aboutPage;
    }

}
