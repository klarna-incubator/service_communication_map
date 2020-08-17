package com.klarna.secoma;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ElementFactory;

@Tag("div")
public class WrapperComponent extends Component {

	private static final long serialVersionUID = -2856705271903198290L;

	public void add(Element child) {
		Element childWrapper = ElementFactory.createDiv();
		childWrapper.appendChild(child);
		getElement().appendChild(childWrapper);
	}

}
