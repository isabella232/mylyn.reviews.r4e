// $codepro.audit.disable com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.alwaysOverridetoString.alwaysOverrideToString, com.instantiations.assist.eclipse.analysis.deserializeabilitySecurity, com.instantiations.assist.eclipse.analysis.disallowReturnMutable, com.instantiations.assist.eclipse.analysis.enforceCloneableUsageSecurity, explicitThisUsage
/*******************************************************************************
 * Copyright (c) 2010 Ericsson Research Canada
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Description:
 * 
 * This class is used as the input class that feeds the eclipse compare
 * editor
 * 
 * Contributors:
 *   Sebastien Dubois - Created for Mylyn Review R4E project
 *   
 ******************************************************************************/

package org.eclipse.mylyn.reviews.r4e.ui.internal.editors;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.compare.structuremergeviewer.ICompareInput;
import org.eclipse.compare.structuremergeviewer.IDiffElement;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.team.ui.synchronize.SaveableCompareEditorInput;

/**
 * @author lmcdubo
 * @version $Revision: 1.0 $
 */
public class R4ECompareEditorInput extends SaveableCompareEditorInput {

	// ------------------------------------------------------------------------
	// Member variables
	// ------------------------------------------------------------------------

	/**
	 * Field fConfig - the compare configuration
	 */
	private final CompareConfiguration fConfig;

	/**
	 * Field fAncestor - the optional element that will appear on the top of the compare editor
	 */
	private final ITypedElement fAncestor;

	/**
	 * Field fLeft - the element that will appear on the left side of the compare editor
	 */
	private final ITypedElement fLeft;

	/**
	 * Field fRight - the element that will appear on the right side of the compare editor
	 */
	private final ITypedElement fRight;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	/**
	 * Constructor for R4ECompareEditorInput.
	 * 
	 * @param aConfig
	 *            CompareConfiguration
	 * @param aAncestor
	 *            ITypedElement
	 * @param aLeft
	 *            ITypedElement
	 * @param aRight
	 *            ITypedElement
	 */
	public R4ECompareEditorInput(CompareConfiguration aConfig, ITypedElement aAncestor, ITypedElement aLeft,
			ITypedElement aRight) {
		super(aConfig, null);
		fConfig = aConfig;
		fAncestor = aAncestor;
		fLeft = aLeft;
		fRight = aRight;
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * Method getLeftElement.
	 * 
	 * @return FileRevisionTypedElement
	 */
	public ITypedElement getLeftElement() {
		return fLeft;
	}

	/**
	 * Method getRightElement.
	 * 
	 * @return FileRevisionTypedElement
	 */
	public ITypedElement getRightElement() {
		return fRight;
	}

	private ICompareInput createCompareInput() {
		return compare(fLeft, fRight);
	}

	private DiffNode compare(ITypedElement actLeft, ITypedElement actRight) {
		return new DiffNode(actLeft, actRight);
	}

	/**
	 * Method getToolTipText.
	 * 
	 * @return String
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	@Override
	public String getToolTipText() {
		if (null != fLeft && null != fRight) {
			String format = null;

			// Set the label values for the compare editor
			StringBuilder leftLabel = null;
			if (null != fLeft) {
				leftLabel = new StringBuilder("Target: " + fLeft.getName());
				if (fLeft instanceof R4EFileRevisionTypedElement) {
					leftLabel.append("_" + ((R4EFileRevisionTypedElement) fLeft).getFileVersion().getVersionID());
				}
				fConfig.setLeftLabel(leftLabel.toString());
			}
			StringBuilder rightLabel = null;
			if (null != fRight) {
				rightLabel = new StringBuilder("Base: " + fRight.getName());
				if (fRight instanceof R4EFileRevisionTypedElement) {
					rightLabel.append("_" + ((R4EFileRevisionTypedElement) fRight).getFileVersion().getVersionID());
				}
				fConfig.setRightLabel(rightLabel.toString());
			}

			if (null != fAncestor) {
				format = CompareUI.getResourceBundle().getString("ResourceCompare.threeWay.tooltip"); //$NON-NLS-1$
				final String ancestorLabel = "";
				return MessageFormat.format(format, new Object[] { ancestorLabel, leftLabel, rightLabel });
			}
			format = CompareUI.getResourceBundle().getString("ResourceCompare.twoWay.tooltip"); //$NON-NLS-1$
			return MessageFormat.format(format, new Object[] { leftLabel, rightLabel });
		}
		// fall back
		return super.getToolTipText();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.compare.CompareEditorInput#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes")
	Class adapter) {
		if (adapter == IFile.class) {
			if (getWorkspaceElement() != null) {
				return getWorkspaceElement().getResource();
			}
			return null;
		}
		return super.getAdapter(adapter);
	}

	/**
	 * Method fireInputChange.
	 */
	@Override
	protected void fireInputChange() { // $codepro.audit.disable emptyMethod
		// Not implemented for now
	}

	private R4EFileTypedElement getWorkspaceElement() {
		if (fLeft instanceof R4EFileTypedElement) {
			return (R4EFileTypedElement) fLeft;
		}
		return null;
	}

	@Override
	protected ICompareInput prepareCompareInput(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		ICompareInput input = createCompareInput();
		initLabels();

		// The compare editor (Structure Compare) will show the diff filenames
		// with their project relative path. So, no need to also show directory entries.
		DiffNode flatDiffNode = new DiffNode(null, Differencer.CHANGE, null, fLeft, fRight);
		flatDiffView(flatDiffNode, (DiffNode) input);
		return flatDiffNode;
	}

	private void flatDiffView(DiffNode rootNode, DiffNode currentNode) {
		if (currentNode != null) {
			IDiffElement[] dElems = currentNode.getChildren();
			if (dElems != null) {
				for (IDiffElement dElem : dElems) {
					DiffNode dNode = (DiffNode) dElem;
					if (dNode.getChildren() != null && dNode.getChildren().length > 0) {
						flatDiffView(rootNode, dNode);
					} else {
						rootNode.add(dNode);
					}
				}
			}
		}
	}

	private void initLabels() {
		// Set the label values for the compare editor
		if (null != fLeft) {
			final StringBuilder leftLabel = new StringBuilder("Target: " + fLeft.getName());
			if (fLeft instanceof R4EFileRevisionTypedElement) {
				leftLabel.append(" "
						+ ((R4EFileRevisionTypedElement) fLeft).getFileVersion().getVersionID().substring(0, 7));
			}
			fConfig.setLeftLabel(leftLabel.toString());
		}
		if (null != fRight) {
			final StringBuilder rightLabel = new StringBuilder("Base: " + fRight.getName());
			if (fRight instanceof R4EFileRevisionTypedElement) {
				rightLabel.append("_" + ((R4EFileRevisionTypedElement) fRight).getFileVersion().getVersionID());
			}
			fConfig.setRightLabel(rightLabel.toString());
		}

		// If the ancestor is not null, just put the file name as the workspace label
		if (null != fAncestor) {
			fConfig.setAncestorLabel(fAncestor.getName());
		}
	}
}
