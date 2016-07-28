/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.webeco;

import name.martingeisse.webeco.model.ISimulatorAction;
import name.martingeisse.webeco.model.SimulationModel;
import name.martingeisse.webeco.model.Simulator;
import name.martingeisse.webeco.wicket.CharacterDisplayPanel;
import name.martingeisse.webeco.wicket.QueueTextFeeder;

import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.time.Duration;

/**
 * The main web page for the simulator.
 */
public class SimulatorPage extends WebPage {

	/**
	 * the inputLine
	 */
	private String inputLine;

	/**
	 * Constructor.
	 */
	public SimulatorPage() {

		final GuiMessageHub guiMessageHub = Simulator.getGuiMessageHub();
		
		final CharacterDisplayPanel characterDisplayPanel = new CharacterDisplayPanel("characterDisplayPanel", guiMessageHub.getCharacterDisplayQueue());
		add(characterDisplayPanel);
		
		final QueueTextFeeder feeder = new QueueTextFeeder("text", guiMessageHub.getTerminalOutputQueue());
		add(feeder);

		add(new AbstractAjaxTimerBehavior(Duration.seconds(1)) {
			@Override
			protected void onTimer(final AjaxRequestTarget target) {
				characterDisplayPanel.feed(target);
				feeder.feed(target);
			}
		});

		final Form<Void> inputForm = new Form<Void>("inputForm");
		add(inputForm);

		final TextField<String> inputLineTextField = new TextField<String>("text", new PropertyModel<String>(this, "inputLine"));
		inputLineTextField.setOutputMarkupId(true);
		inputForm.add(inputLineTextField);

		inputForm.add(new AjaxButton("submit", inputForm) {
			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
				inputLine = (inputLine == null) ? "" : inputLine;
				guiMessageHub.getTerminalInputQueue().add(inputLine + "\r\n");
				notifyTerminalInputAvailable();
				inputLine = null;
				target.add(inputLineTextField);
			}
			@Override
			protected void onError(AjaxRequestTarget arg0, Form<?> arg1) {
			}
		});

		final WebMarkupContainer backspaceButton = new WebMarkupContainer("backspaceButton");
		add(backspaceButton);
		backspaceButton.add(new AjaxEventBehavior("onclick") {
			@Override
			protected void onEvent(final AjaxRequestTarget target) {
				guiMessageHub.getTerminalInputQueue().add("\b");
				notifyTerminalInputAvailable();
			}
		});
		
		add(new AjaxLink<Void>("resetLink") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				guiMessageHub.getActions().add(new ISimulatorAction() {
					@Override
					public void execute(SimulationModel model) {
						model.getCpu().reset();
					}
				});
			}
		});

	}

	/**
	 * 
	 */
	private void notifyTerminalInputAvailable() {
		Simulator.getGuiMessageHub().getActions().add(new ISimulatorAction() {
			@Override
			public void execute(SimulationModel model) {
				model.notifyTerminalInputAvailable();
			}
		});
	}
	
	/**
	 * Getter method for the inputLine.
	 * @return the inputLine
	 */
	public String getInputLine() {
		return inputLine;
	}

	/**
	 * Setter method for the inputLine.
	 * @param inputLine the inputLine to set
	 */
	public void setInputLine(final String inputLine) {
		this.inputLine = inputLine;
	}
	
}
