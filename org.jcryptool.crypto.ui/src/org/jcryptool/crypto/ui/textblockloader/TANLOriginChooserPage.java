package org.jcryptool.crypto.ui.textblockloader;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.wb.swt.SWTResourceManager;

public class TANLOriginChooserPage extends WizardPage {

	public static final String METHOD_TEXT_BASED = "METHOD_TEXT_BASED";
	public static final String METHOD_NUMERIC = "METHOD_NUMERIC";
	protected String method;

	/**
	 * Create the wizard.
	 */
	public TANLOriginChooserPage() {
		super("wizardPage");
		this.method = METHOD_TEXT_BASED;
		setTitle("Load data blocks");
		setDescription("Choose an input method for your data");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(1, false));
		
		Button btnTextbasedInput = new Button(container, SWT.RADIO);
		btnTextbasedInput.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TANLOriginChooserPage.this.method = METHOD_TEXT_BASED;
			}
		});
		btnTextbasedInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnTextbasedInput.setText("Text-based input");
		
		GridData lblDescriptionLData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		lblDescriptionLData.horizontalIndent = 25;
		lblDescriptionLData.widthHint = 300;
		GridData lblDescriptionLData2 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		lblDescriptionLData2.horizontalIndent = 25;
		lblDescriptionLData2.widthHint = 300;
		
		Label lblTextFromAn = new Label(container, SWT.WRAP);
		lblTextFromAn.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.ITALIC));
		lblTextFromAn.setText("Text from an editor, a file or a manually typed text will be converted into data blocks. You can choose the text and the conversion preferences in the next pages.");
		lblTextFromAn.setLayoutData(lblDescriptionLData);
		
		
		Button btnRadioButton = new Button(container, SWT.RADIO);
		GridData gd_btnRadioButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnRadioButton.verticalIndent = 10;
		btnRadioButton.setLayoutData(gd_btnRadioButton);
		btnRadioButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TANLOriginChooserPage.this.method = METHOD_NUMERIC;
			}
		});
		btnRadioButton.setText("Numeric input");

		Label lblSetTheData = new Label(container, SWT.WRAP);
		lblSetTheData.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.ITALIC));
		lblSetTheData.setText("Set the data blocks manually by entering the decimal or hex representation.");
		lblSetTheData.setLayoutData(lblDescriptionLData2);
		
		btnRadioButton.setSelection(this.method.equals(METHOD_NUMERIC));
		btnRadioButton.setSelection(this.method.equals(METHOD_TEXT_BASED));
			
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
	
}
