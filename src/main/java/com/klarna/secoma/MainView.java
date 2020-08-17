package com.klarna.secoma;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
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

@Route("")
@PWA(name = "Project Base for Vaadin", shortName = "Project Base")
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class MainView extends VerticalLayout {

	private static final long serialVersionUID = 4415593990886566385L;

	@Autowired
	CommunicationMapService communicationMapService;

	public MainView() {
		VerticalLayout workspace = new VerticalLayout();
		TextField textField = new TextField("Correlation ID");
		WrapperComponent wrapper = new WrapperComponent();

		Button button = new Button("Search");
		button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		button.addClickShortcut(Key.ENTER);
		button.addClickListener(event -> {
			String correlationId = StringUtils.stripToNull(textField.getValue());
			Notification.show(correlationId == null ? "Empty correlation ID, rendering all logs"
					: "Rendering for " + correlationId);
			Element image = new Element("object");
			image.setAttribute("type", "image/svg+xml");
			image	.getStyle()
					.set("display", "block");
			image.setAttribute("data", communicationMapService.findCommunicationMap(correlationId));
			wrapper.add(image);
		});

		workspace.setAlignItems(Alignment.CENTER);
		workspace.setSizeFull();
		workspace.add(textField, button, wrapper);

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