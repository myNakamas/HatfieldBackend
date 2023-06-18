package com.nakamas.hatfieldbackend.models.entities.shop;


import com.nakamas.hatfieldbackend.models.views.incoming.ShopPageTemplatesView;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Embeddable
public class ShopPageTemplates {
    @Column(columnDefinition = "text")
    private String aboutPage;


    public ShopPageTemplates() {
        this.aboutPage = "# About us";
    }

    public ShopPageTemplates(ShopPageTemplatesView view) {
        this.aboutPage = view.getAboutPage();
    }

    public void update(ShopPageTemplatesView view) {
        if (view.getAboutPage() != null) this.aboutPage = view.getAboutPage();
    }
}
