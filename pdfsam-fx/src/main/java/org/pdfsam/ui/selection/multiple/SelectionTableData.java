package org.pdfsam.ui.selection.multiple;

import static org.pdfsam.ui.commons.SetDestinationRequest.requestDestination;
import static org.sejda.eventstudio.StaticStudio.eventStudio;

import java.util.function.Consumer;

import org.pdfsam.i18n.DefaultI18nContext;
import org.pdfsam.ui.commons.OpenFileRequest;
import org.pdfsam.ui.commons.RemoveSelectedEvent;
import org.pdfsam.ui.commons.SetPageRangesRequest;
import org.pdfsam.ui.commons.ShowPdfDescriptorRequest;
import org.pdfsam.ui.selection.multiple.move.MoveSelectedEvent;
import org.pdfsam.ui.selection.multiple.move.MoveType;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.application.Platform;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class SelectionTableData {
	public Consumer<SelectionChangedEvent> selectionChangedConsumer;

	public SelectionTableData() {
	}

	void initTopSectionContextMenu(SelectionTable selectionTable, ContextMenu contextMenu, boolean hasRanges) {
	    MenuItem setDestinationItem = selectionTable.createMenuItem(DefaultI18nContext.getInstance().i18n("Set destination"),
	            MaterialDesignIcon.AIRPLANE_LANDING);
	    setDestinationItem.setOnAction(e -> eventStudio().broadcast(
	            requestDestination(selectionTable.getSelectionModel().getSelectedItem().descriptor().getFile(), selectionTable.getOwnerModule()),
	            selectionTable.getOwnerModule()));
	    setDestinationItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.ALT_DOWN));
	
	    selectionChangedConsumer = e -> setDestinationItem.setDisable(!e.isSingleSelection());
	    contextMenu.getItems().add(setDestinationItem);
	
	    if (hasRanges) {
	        MenuItem setPageRangesItem = selectionTable.createMenuItem(DefaultI18nContext.getInstance().i18n("Set as range for all"),
	                MaterialDesignIcon.FORMAT_INDENT_INCREASE);
	        setPageRangesItem.setOnAction(e -> eventStudio().broadcast(
	                new SetPageRangesRequest(selectionTable.getSelectionModel().getSelectedItem().pageSelection.get()),
	                selectionTable.getOwnerModule()));
	        setPageRangesItem.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
	        selectionChangedConsumer = selectionChangedConsumer
	                .andThen(e -> setPageRangesItem.setDisable(!e.isSingleSelection()));
	        contextMenu.getItems().add(setPageRangesItem);
	    }
	    contextMenu.getItems().add(new SeparatorMenuItem());
	}

	void initItemsSectionContextMenu(SelectionTable selectionTable, ContextMenu contextMenu, boolean canDuplicate, boolean canMove) {
	
	    MenuItem removeSelected = selectionTable.createMenuItem(DefaultI18nContext.getInstance().i18n("Remove"),
	            MaterialDesignIcon.MINUS);
	    removeSelected.setOnAction(e -> eventStudio().broadcast(new RemoveSelectedEvent(), selectionTable.getOwnerModule()));
	    removeSelected.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));
	    contextMenu.getItems().add(removeSelected);
	    selectionChangedConsumer = selectionChangedConsumer
	            .andThen(e -> removeSelected.setDisable(e.isClearSelection()));
	    if (canMove) {
	        MenuItem moveTopSelected = selectionTable.createMenuItem(DefaultI18nContext.getInstance().i18n("Move to Top"),
	                MaterialDesignIcon.CHEVRON_DOUBLE_UP);
	        moveTopSelected
	                .setOnAction(e -> eventStudio().broadcast(new MoveSelectedEvent(MoveType.TOP), selectionTable.getOwnerModule()));
	
	        MenuItem moveUpSelected = selectionTable.createMenuItem(DefaultI18nContext.getInstance().i18n("Move Up"),
	                MaterialDesignIcon.CHEVRON_UP);
	        moveUpSelected
	                .setOnAction(e -> eventStudio().broadcast(new MoveSelectedEvent(MoveType.UP), selectionTable.getOwnerModule()));
	
	        MenuItem moveDownSelected = selectionTable.createMenuItem(DefaultI18nContext.getInstance().i18n("Move Down"),
	                MaterialDesignIcon.CHEVRON_DOWN);
	        moveDownSelected
	                .setOnAction(e -> eventStudio().broadcast(new MoveSelectedEvent(MoveType.DOWN), selectionTable.getOwnerModule()));
	
	        MenuItem moveBottomSelected = selectionTable.createMenuItem(DefaultI18nContext.getInstance().i18n("Move to Bottom"),
	                MaterialDesignIcon.CHEVRON_DOUBLE_DOWN);
	        moveBottomSelected.setOnAction(
	                e -> eventStudio().broadcast(new MoveSelectedEvent(MoveType.BOTTOM), selectionTable.getOwnerModule()));
	
	        contextMenu.getItems().addAll(moveTopSelected, moveUpSelected, moveDownSelected, moveBottomSelected);
	
	        moveBottomSelected.setAccelerator(new KeyCodeCombination(KeyCode.END, KeyCombination.ALT_DOWN));
	        moveDownSelected.setAccelerator(new KeyCodeCombination(KeyCode.DOWN, KeyCombination.ALT_DOWN));
	        moveUpSelected.setAccelerator(new KeyCodeCombination(KeyCode.UP, KeyCombination.ALT_DOWN));
	        moveTopSelected.setAccelerator(new KeyCodeCombination(KeyCode.HOME, KeyCombination.ALT_DOWN));
	
	        selectionChangedConsumer = selectionChangedConsumer.andThen(e -> {
	            moveTopSelected.setDisable(!e.canMove(MoveType.TOP));
	            moveUpSelected.setDisable(!e.canMove(MoveType.UP));
	            moveDownSelected.setDisable(!e.canMove(MoveType.DOWN));
	            moveBottomSelected.setDisable(!e.canMove(MoveType.BOTTOM));
	        });
	    }
	    if (canDuplicate) {
	        MenuItem duplicateItem = selectionTable.createMenuItem(DefaultI18nContext.getInstance().i18n("Duplicate"),
	                MaterialDesignIcon.CONTENT_DUPLICATE);
	        duplicateItem.setOnAction(e -> eventStudio().broadcast(new DuplicateSelectedEvent(), selectionTable.getOwnerModule()));
	        duplicateItem.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT2, KeyCombination.ALT_DOWN));
	
	        contextMenu.getItems().add(duplicateItem);
	
	        selectionChangedConsumer = selectionChangedConsumer
	                .andThen(e -> duplicateItem.setDisable(e.isClearSelection()));
	    }
	}

	void initBottomSectionContextMenu(SelectionTable selectionTable, ContextMenu contextMenu) {
	
	    MenuItem copyItem = selectionTable.createMenuItem(DefaultI18nContext.getInstance().i18n("Copy to clipboard"),
	            MaterialDesignIcon.CONTENT_COPY);
	    copyItem.setOnAction(e -> selectionTable.copySelectedToClipboard());
	
	    MenuItem infoItem = selectionTable.createMenuItem(DefaultI18nContext.getInstance().i18n("Document properties"),
	            MaterialDesignIcon.INFORMATION_OUTLINE);
	    infoItem.setOnAction(e -> Platform.runLater(() -> eventStudio()
	            .broadcast(new ShowPdfDescriptorRequest(selectionTable.getSelectionModel().getSelectedItem().descriptor()))));
	
	    MenuItem openFileItem = selectionTable.createMenuItem(DefaultI18nContext.getInstance().i18n("Open"),
	            MaterialDesignIcon.FILE_PDF_BOX);
	    openFileItem.setOnAction(e -> eventStudio()
	            .broadcast(new OpenFileRequest(selectionTable.getSelectionModel().getSelectedItem().descriptor().getFile())));
	
	    MenuItem openFolderItem = selectionTable.createMenuItem(DefaultI18nContext.getInstance().i18n("Open Folder"),
	            MaterialDesignIcon.FOLDER_OUTLINE);
	    openFolderItem.setOnAction(e -> eventStudio().broadcast(
	            new OpenFileRequest(selectionTable.getSelectionModel().getSelectedItem().descriptor().getFile().getParentFile())));
	
	    copyItem.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN));
	    infoItem.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.ALT_DOWN));
	    openFileItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));
	    openFolderItem.setAccelerator(
	            new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN, KeyCombination.ALT_DOWN));
	
	    contextMenu.getItems().addAll(new SeparatorMenuItem(), copyItem, infoItem, openFileItem, openFolderItem);
	
	    selectionChangedConsumer = selectionChangedConsumer.andThen(e -> {
	        copyItem.setDisable(e.isClearSelection());
	        infoItem.setDisable(!e.isSingleSelection());
	        openFileItem.setDisable(!e.isSingleSelection());
	        openFolderItem.setDisable(!e.isSingleSelection());
	    });
	}
}