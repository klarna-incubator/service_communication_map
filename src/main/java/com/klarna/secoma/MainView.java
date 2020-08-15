package com.klarna.secoma;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.StreamResource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Route("")
@PWA(name = "Project Base for Vaadin", shortName = "Project Base")
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class MainView extends VerticalLayout {

    @Autowired
    CommunicationMapService communicationMapService;

    public MainView() {
        TextField textField = new TextField("Correlation ID");

        Image graphImage = new Image();

        Button button = new Button("Search");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickShortcut(Key.ENTER);
        button.addClickListener(event -> {
            String correlationId = textField.getValue();
            if (StringUtils.isEmpty(correlationId)) {
                Notification.show("Empty correlation ID");
                return;
            }

            StreamResource res = communicationMapService.findCommunicationMap(correlationId);
            graphImage.setSrc(res);
        });

        

        VerticalLayout workspace = new VerticalLayout();
        workspace.setAlignItems(Alignment.CENTER);
        workspace.setSizeFull();
        workspace.add(textField, button, graphImage);

        setSizeFull();
        setMargin(false);
        setSpacing(false);
        setPadding(false);
        add(createHeader(), workspace);
    }

    private Component createHeader() {
        Icon drawer = VaadinIcon.MENU.create();
        Span title = new Span("Service Communication Map Viewer");
        Icon help = VaadinIcon.QUESTION_CIRCLE.create();
        HorizontalLayout header = new HorizontalLayout(drawer, title, help);
        header.expand(title);
        header.setPadding(true);
        header.setWidth("100%");

        return header;
    }

}