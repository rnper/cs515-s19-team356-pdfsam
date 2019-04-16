package org.pdfsam.ui;

import org.pdfsam.news.HideNewsPanelRequest;
import org.pdfsam.news.ShowNewsPanelRequest;
import org.sejda.eventstudio.annotation.EventListener;

import javafx.animation.FadeTransition;
import javafx.scene.layout.VBox;

public class ContentPaneData {
	public VBox newsContainer;
	public FadeTransition fadeIn;
	public FadeTransition fadeOut;

	public ContentPaneData() {
	}

	@EventListener(priority = Integer.MIN_VALUE)
	@SuppressWarnings("unused")
	public void onShowNewsPanel(ContentPane contentPane, ShowNewsPanelRequest request) {
	    if (!newsContainer.isVisible()) {
	        newsContainer.setVisible(true);
	        fadeIn.play();
	    }
	}

	@EventListener(priority = Integer.MIN_VALUE)
	@SuppressWarnings("unused")
	public void onHideNewsPanel(ContentPane contentPane, HideNewsPanelRequest request) {
	    if (newsContainer.isVisible()) {
	        fadeOut.play();
	    }
	}
}