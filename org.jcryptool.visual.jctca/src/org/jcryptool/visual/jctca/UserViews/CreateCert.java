package org.jcryptool.visual.jctca.UserViews;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jcryptool.crypto.keystore.backend.KeyStoreAlias;
import org.jcryptool.crypto.keystore.backend.KeyStoreManager;
import org.jcryptool.visual.jctca.Util;
import org.jcryptool.visual.jctca.listeners.CreateCertListener;

public class CreateCert implements Views {

	Composite composite;

	Label lbl_firstname;
	Text txt_firstname;

	Label lbl_lastname;
	Text txt_lastname;

	Label lbl_street;
	Text txt_street;

	Label lbl_ZIP;
	Text txt_ZIP;

	Label lbl_city;
	Text txt_city;

	Label lbl_country;
	Text txt_country;

	Label lbl_mail;
	Text txt_mail;

	Label lbl_proof;
	Button btn_proof;

	Label lbl_key;
	Button btn_genKey;

	Label lbl_plain;
	Button btn_send;

	Label lbl_plain1;
	Label lbl_plain2;
	Label lbl_plain3;
	Combo cmb_genKey;

	public CreateCert(Composite content, Group exp) {
		composite = new Composite(content, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, true));

		Group createCertGroup = new Group(composite, SWT.NONE);
		createCertGroup.setLayout(new GridLayout(2, false));
		GridData gd_grp = new GridData(SWT.FILL, SWT.NONE, true, true);
		createCertGroup.setLayoutData(gd_grp);
		createCertGroup.setText(Messages.CreateCert_headline);

		lbl_firstname = new Label(createCertGroup, SWT.NONE);
		lbl_firstname.setText(Messages.CreateCert_lbl_first_name);
		txt_firstname = new Text(createCertGroup, SWT.SINGLE | SWT.BORDER);
		txt_firstname.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txt_firstname.setText(Messages.CreateCert_sample_first_name);

		lbl_lastname = new Label(createCertGroup, SWT.None);
		lbl_lastname.setText(Messages.CreateCert_lbl_last_name);
		txt_lastname = new Text(createCertGroup, SWT.SINGLE | SWT.BORDER);
		txt_lastname.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txt_lastname.setText(Messages.CreateCert_sample_last_name);

		lbl_street = new Label(createCertGroup, SWT.None);
		lbl_street.setText(Messages.CreateCert_lbl_street);
		txt_street = new Text(createCertGroup, SWT.SINGLE | SWT.BORDER);
		txt_street.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txt_street.setText(Messages.CreateCert_sample_street);

		lbl_ZIP = new Label(createCertGroup, SWT.None);
		lbl_ZIP.setText(Messages.CreateCert_lbl_zip);
		txt_ZIP = new Text(createCertGroup, SWT.SINGLE | SWT.BORDER);
		txt_ZIP.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txt_ZIP.setText(Messages.CreateCert_sample_zip);

		lbl_city = new Label(createCertGroup, SWT.None);
		lbl_city.setText(Messages.CreateCert_lbl_city);
		txt_city = new Text(createCertGroup, SWT.SINGLE | SWT.BORDER);
		txt_city.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txt_city.setText(Messages.CreateCert_sample_city);

		lbl_country = new Label(createCertGroup, SWT.None);
		lbl_country.setText(Messages.CreateCert_lbl_country);
		txt_country = new Text(createCertGroup, SWT.SINGLE | SWT.BORDER);
		txt_country.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txt_country.setText(Messages.CreateCert_sample_country);

		lbl_mail = new Label(createCertGroup, SWT.None);
		lbl_mail.setText(Messages.CreateCert_lbl_mail);
		txt_mail = new Text(createCertGroup, SWT.BORDER | SWT.SINGLE);
		txt_mail.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txt_mail.setText(Messages.CreateCert_sample_mail);
		lbl_proof = new Label(createCertGroup, SWT.None);
		lbl_proof.setText(Messages.CreateCert_lbl_idproof);
		btn_proof = new Button(createCertGroup, SWT.None);
		btn_proof.setText(Messages.CreateCert_btn_select_file);
		btn_proof.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btn_proof.setData(new Integer(0));
		
		lbl_key = new Label(createCertGroup, SWT.None);
		lbl_key.setText(Messages.CreateCert_lbl_pubkey);
		btn_genKey = new Button(createCertGroup, SWT.NONE);
		btn_genKey.setText(Messages.CreateCert_btn_create_keypair);
		btn_genKey.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btn_genKey.setData(new Integer(1));

		lbl_plain = new Label(createCertGroup, SWT.NONE);
		lbl_plain2 = new Label(createCertGroup, SWT.FILL | SWT.CENTER);
		lbl_plain2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		lbl_plain2.setText(Messages.CreateCert_btn_select_pubkey);
		lbl_plain3 = new Label(createCertGroup, SWT.NONE);
		cmb_genKey = new Combo(createCertGroup, SWT.NONE);
		cmb_genKey.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		addRSAAndDSAKeysToDropdown();

		CreateCertListener lst = new CreateCertListener(txt_firstname,
				txt_lastname, txt_street, txt_ZIP, txt_city, txt_country,
				txt_mail, cmb_genKey);
		btn_genKey.addSelectionListener(lst);
		btn_proof.addSelectionListener(lst);
		btn_send = new Button(composite, SWT.NONE);
		btn_send.setText(Messages.CreateCert_btn_send_csr_to_ra);
		btn_send.addSelectionListener(lst);
		btn_send.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		btn_send.setData(2);

		Label lbl_exp = (Label) exp.getChildren()[0];
		lbl_exp.setText(Messages.CreateCert_explain_text);
		composite.setVisible(false);
	}

	@Override
	public void dispose() {
		this.composite.dispose();
	}

	@Override
	public void setVisible(boolean visible) {
		this.composite.setVisible(visible);
	}

	private void addRSAAndDSAKeysToDropdown() {
		KeyStoreManager ksm = KeyStoreManager.getInstance();
		for (KeyStoreAlias s : Util.getAllRSAAndDSAPublicKeys(ksm)) {
			String entry = s.getContactName() + " (" + s.getKeyLength() + "bit RSA, Hash: " + s.getHashValue() + ") ";//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			cmb_genKey.add(entry);
			cmb_genKey.setData(entry,s);
		}
		// chose a default value so that we don't get a array out of bound
		// exception if user doesn't chose an entry
		cmb_genKey.select(0);
	}
}
