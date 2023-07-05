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
        if (view != null && view.getAboutPage() != null && view.getAboutPage().isBlank()) {
            this.aboutPage = view.getAboutPage();
        } else this.aboutPage = "# About us";
    }

    public void update(ShopPageTemplatesView view) {
        if (view.getAboutPage() != null) this.aboutPage = view.getAboutPage();
    }
}
