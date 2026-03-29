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
    public static final String WELWYNHATFIELD = "WELWYNHATFIELD";
    @Column()
    private String name;

    @Column(columnDefinition = "text")
    private String aboutPage;


    public ShopPageTemplates() {
        this.name = WELWYNHATFIELD;
        this.aboutPage = "# About us";
    }

    public ShopPageTemplates(ShopPageTemplatesView view) {
        this();
        if (view != null && view.getAboutPage() != null && view.getAboutPage().isBlank()) {
            this.name = view.getTemplateName();
            this.aboutPage = view.getAboutPage();
        }
    }

    public void update(ShopPageTemplatesView view) {
        if (view.getTemplateName()!=null) this.name = view.getTemplateName();
        if (view.getAboutPage() != null) this.aboutPage = view.getAboutPage();
    }
}
