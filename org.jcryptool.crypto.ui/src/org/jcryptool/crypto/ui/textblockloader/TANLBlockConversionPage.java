package org.jcryptool.crypto.ui.textblockloader;

import java.awt.font.NumericShaper;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Spinner;
import org.jcryptool.core.util.input.AbstractUIInput;
import org.jcryptool.core.util.input.InputVerificationResult;
import org.jcryptool.core.util.input.InputVerificationResult.MessageType;
import org.jcryptool.crypto.ui.textblockloader.NumberblocksAndTextViewer.Repr;
import org.jcryptool.crypto.ui.textblockloader.conversion.AlphabetCharsToNumbers;
import org.jcryptool.crypto.ui.textblockloader.conversion.ConversionStringToBlocks;
import org.jcryptool.crypto.ui.textblockloader.conversion.NumbersToBlocksConversion;
import org.jcryptool.crypto.ui.util.WidgetBubbleUIInputHandler;

public class TANLBlockConversionPage extends WizardPage {

	private static final String RESULT_TYPE_TOO_BIG_FOR_INT = "ResultType_TooBigForInt";
	private static final String RESULT_TYPE_BASE_TOO_SMALL = "ResultType_baseTooSmall";
	private Spinner spinner_nrBlocks;
	private Spinner spinner_base;
	private AbstractUIInput<NumbersToBlocksConversion> blockMethodInput;
	private ConversionCharsToNumbers ctnForPreview;
	private List<Integer> numbersForPreview;
	private NumberblocksAndTextViewer previewNumbers;
	private NumberblocksAndTextViewer previewBlocks;
	private int maxNumber;
	private int maxPotentialNumber;
	private List<Integer> blocksForPreview;

	/**
	 * Create the wizard.
	 * @param maxNumber 
	 */
	public TANLBlockConversionPage(int maxNumber) {
		super("wizardPage");
		this.maxNumber = maxNumber;
		setTitle("Wizard Page title");
		setDescription("Wizard Page description");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(1, false));
		
		Group grpBlockDivision = new Group(container, SWT.NONE);
		grpBlockDivision.setLayout(new GridLayout(2, false));
		grpBlockDivision.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpBlockDivision.setText("Block division");
		
		Label lblNumberOfCharacters = new Label(grpBlockDivision, SWT.NONE);
		lblNumberOfCharacters.setText("Number of characters per block: ");
		
		spinner_nrBlocks = new Spinner(grpBlockDivision, SWT.BORDER);
		spinner_nrBlocks.setMinimum(1);
		
		Label lblBaseBFor = new Label(grpBlockDivision, SWT.NONE);
		lblBaseBFor.setText("Base b for b-adic block creation: ");
		
		spinner_base = new Spinner(grpBlockDivision, SWT.BORDER);
		
		Group grpPreview = new Group(container, SWT.NONE);
		grpPreview.setLayout(new GridLayout(1, false));
		grpPreview.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpPreview.setText("Preview:");
		
		Group grpNumericalRepresentationOf = new Group(grpPreview, SWT.NONE);
		grpNumericalRepresentationOf.setLayout(new GridLayout(1, false));
		grpNumericalRepresentationOf.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpNumericalRepresentationOf.setText("Numerical representation of characters from previous page:");
		
		Repr[] viewOptions = new NumberblocksAndTextViewer.Repr[]{
				NumberblocksAndTextViewer.Repr.DECIMAL, 
				NumberblocksAndTextViewer.Repr.HEX, 
				NumberblocksAndTextViewer.Repr.BINARY, 
				NumberblocksAndTextViewer.Repr.STRING 
			};
		previewNumbers = new NumberblocksAndTextViewer(grpNumericalRepresentationOf, SWT.NONE, viewOptions);
		previewNumbers.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		Group grpBlocks = new Group(grpPreview, SWT.NONE);
		grpBlocks.setLayout(new GridLayout(1, false));
		grpBlocks.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpBlocks.setText("Blocks:");

		previewBlocks = new NumberblocksAndTextViewer(grpBlocks, SWT.NONE, viewOptions);
		previewBlocks.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		this.blockMethodInput = makeBlockMethodInput();
		SelectionAdapter spinnerListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				blockMethodInput.synchronizeWithUserSide();
			}
		};
		spinner_base.addSelectionListener(spinnerListener);
		spinner_nrBlocks.addSelectionListener(spinnerListener);
		
		getDefaultWiz().getPageTextToNumbersPage().getCharsToNumbersConversionInput().addObserver(new Observer() {
			@Override
			public void update(Observable o, Object arg) {
				refreshData();
			}
		});
		getDefaultWiz().getPageTextToNumbersPage().getTextInput().addObserver(new Observer() {
			@Override
			public void update(Observable o, Object arg) {
				refreshData();
			}
		});
		this.blockMethodInput.addObserver(new Observer() {
			@Override
			public void update(Observable o, Object arg) {
				refreshData();
			}
		});
		
		WidgetBubbleUIInputHandler errorHandler = new WidgetBubbleUIInputHandler(this.getShell());
		errorHandler.addInputWidgetMapping(getBlockMethodInput(), grpBlockDivision);
		errorHandler.addAsObserverForInput(blockMethodInput);
		
		refreshData();
		
		
	}
	
	protected void refreshMaxPotNumber() {
		Integer maxPotNumber = getDefaultWiz().getCTN().getMaxNumberValue();
		this.setMaxPotentialNumber(maxPotNumber);
	}

	protected void refreshData() {
		boolean resetB = this.ctnForPreview == null || (getDefaultWiz().getCTN().getMaxNumberValue() != this.ctnForPreview.getMaxNumberValue());
		
		this.refreshMaxPotNumber();
		
		this.ctnForPreview = getDefaultWiz().getCTN();
		String stringData = getDefaultWiz().getText().getText();
		this.numbersForPreview = this.ctnForPreview.convert(stringData);
		this.blocksForPreview = getDefaultWiz().getDataBlocks();
		
		
		if(resetB) {
			performNewAlphaConversionUpdate();
		}
		
		this.showPreview();
	}

	private void performNewAlphaConversionUpdate() {
		this.spinner_base.setMinimum(this.ctnForPreview.getMaxNumberValue()+1);
		this.spinner_base.setMaximum(Integer.MAX_VALUE);
				
		this.blockMethodInput.writeContent(
				new NumbersToBlocksConversion(
						this.blockMethodInput.getContent().getNumbersPerBlock(), 
						this.ctnForPreview.getMaxNumberValue()+1
						)
				);
		this.blockMethodInput.synchronizeWithUserSide();
	}

	public void setNumbersForPreview(List<Integer> numbers, ConversionCharsToNumbers ctn) {
		this.ctnForPreview = ctn;
		this.numbersForPreview = numbers;
		this.showPreview();
	}
	
	public void setMaxNumber(int maxNumber) {
		this.maxNumber = maxNumber;
		this.blockMethodInput.synchronizeWithUserSide();
	}
	
	public void setMaxPotentialNumber(int maxPotentialNumber) {
		this.maxPotentialNumber = maxPotentialNumber;
	}
	
	private void showPreview() {
		this.previewNumbers.setContent(numbersForPreview, new ConversionStringToBlocks(ctnForPreview, new NumbersToBlocksConversion(1, 1)));
		
		NumbersToBlocksConversion ntb = this.blockMethodInput.getContent();
		ConversionStringToBlocks conversion = new ConversionStringToBlocks(ctnForPreview, ntb);
		this.previewBlocks.setContent(blocksForPreview, conversion);
	}

	private AbstractUIInput<NumbersToBlocksConversion> makeBlockMethodInput() {
		return new AbstractUIInput<NumbersToBlocksConversion>() {

//			@Override
//			protected boolean canAutocorrect(InputVerificationResult result) {
//				if(result.getResultType().equals(RESULT_TYPE_BASE_TOO_SMALL)) {
//					return true;
//				} else if(result.getResultType().equals(RESULT_TYPE_TOO_BIG_FOR_INT)) {
//					return true;
//				}
//				
//				return false;
//			}
			
//			@Override
//			protected void autocorrect(InputVerificationResult result) {
//				if(result.getResultType().equals(RESULT_TYPE_BASE_TOO_SMALL)) {
//					spinner_base.setSelection(maxNumber+1);
//				} else if(result.getResultType().equals(RESULT_TYPE_TOO_BIG_FOR_INT)) {
//					spinner_nrBlocks.setSelection(NumbersToBlocksConversion.getMaxBlockCount(spinner_base.getSelection()));
//				}
//			}
			
			@Override
			protected InputVerificationResult verifyUserChange() {
				int base = spinner_base.getSelection();
				int nrBlocks = spinner_nrBlocks.getSelection();
				
//				ConversionStringToBlocks conversionUT = new ConversionStringToBlocks(ctnForPreview, new NumbersToBlocksConversion(nrBlocks, base));
				final int highestPotentialBlock = calcHighestPotentialBlock(base, nrBlocks, maxPotentialNumber);
				if(highestPotentialBlock > maxNumber) {
					return new InputVerificationResult() {
//						@Override
//						public Object getResultType() {
//							return RESULT_TYPE_BASE_TOO_SMALL;
//						}
						@Override
						public boolean isValid() {
							return false;
						}
						@Override
						public boolean isStandaloneMessage() {
							return true;
						}
						@Override
						public MessageType getMessageType() {
							return MessageType.ERROR;
						}
						@Override
						public String getMessage() {
							return "With this base and numbers per block settings, the RSA modulus N=" + (maxNumber+1) + " could be exceeded (biggest possible block: " + highestPotentialBlock + "). For more numbers per block, choose a higher RSA modulus.";
						}
					};
				}
				
				if(maxPotentialNumber >= base) {
					return new InputVerificationResult() {
						
						@Override
						public Object getResultType() {
							return RESULT_TYPE_BASE_TOO_SMALL;
						}
						@Override
						public boolean isValid() {
							return false;
						}
						@Override
						public boolean isStandaloneMessage() {
							return false;
						}
						@Override
						public MessageType getMessageType() {
							return MessageType.ERROR;
						}
						@Override
						public String getMessage() {
							return "The base must be bigger than or equal to the alphabet size of the character conversion (" + (maxPotentialNumber+1) + ").";
						}
					};
				}
				
				if(nrBlocks > NumbersToBlocksConversion.getMaxBlockCount(base)) {
					return new InputVerificationResult() {
						
						@Override
						public Object getResultType() {
							return RESULT_TYPE_TOO_BIG_FOR_INT;
						}
						@Override
						public boolean isValid() {
							return false;
						}
						@Override
						public boolean isStandaloneMessage() {
							return false;
						}
						@Override
						public MessageType getMessageType() {
							return MessageType.ERROR;
						}
						@Override
						public String getMessage() {
							return "with this combination of base and characters per block, the potential blocks could be too big a number.";
						}
					};
				}
				
				return InputVerificationResult.DEFAULT_RESULT_EVERYTHING_OK;
			}

			@Override
			public NumbersToBlocksConversion readContent() {
				int base = spinner_base.getSelection();
				int nrBlocks = spinner_nrBlocks.getSelection();
				
				return new NumbersToBlocksConversion(nrBlocks, base);
			}

			@Override
			public void writeContent(NumbersToBlocksConversion content) {
				spinner_base.setSelection(content.getBase());
				spinner_nrBlocks.setSelection(content.getNumbersPerBlock());
			}

			@Override
			protected NumbersToBlocksConversion getDefaultContent() {
				return new NumbersToBlocksConversion(1, 256);
			}

			@Override
			public String getName() {
				return "block conversion";
			}
			
		};
	}

	protected int calcHighestPotentialBlock(int base, int nrBlocks,
			int maxPotentialNumber2) {
		Integer acc = 0;
		for(int i=0; i<nrBlocks; i++) {
			acc += ((int) Math.round(Math.pow(base, i))) * maxPotentialNumber2;
		}
		return acc;
	}

	private TextAsNumbersLoaderWizard getDefaultWiz() {
		return (TextAsNumbersLoaderWizard) getWizard();
	}
	
	public AbstractUIInput<NumbersToBlocksConversion> getBlockMethodInput() {
		return blockMethodInput;
	}
}
