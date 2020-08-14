package com.klarna.secoma;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;

@Route("")
@PWA(name = "Project Base for Vaadin", shortName = "Project Base")
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class MainView extends VerticalLayout {

    @Autowired
    CommunicationMapService communicationMapService;

    public MainView() {
        TextField textField = new TextField("Correlation ID");

        Button button = new Button("Search");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickShortcut(Key.ENTER);
        button.addClickListener(event -> {

            String correlationId = textField.getValue();
            if (StringUtils.isEmpty(correlationId)) {
                System.out.println("Empty correlation ID");
                return;
            }

            UI ui = UI.getCurrent();
            CompletableFuture.supplyAsync(() -> communicationMapService.findCommunicationMap(correlationId))
                    .thenAccept(streamResource -> {
                        ui.access(() -> {

                        });
                    });
        });
        Image graphImage = new Image();

        add(textField, button, graphImage);
    }

}