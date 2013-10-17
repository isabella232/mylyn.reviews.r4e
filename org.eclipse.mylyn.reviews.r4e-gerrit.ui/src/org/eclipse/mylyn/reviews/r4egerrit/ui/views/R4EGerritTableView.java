/*******************************************************************************
 * Copyright (c) 2013 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Description:
 * 	This class implements the implementation of the R4E-Gerrit UI view.
 * 
 * Contributors:
 *   Jacques Bouthillier - Initial Implementation of the plug-in
 *   Francois Chouinard - Handle gerrit queries and open reviews in editor
 *   Guy Perron			- Add review counter, Add Gerrit button selection
 ******************************************************************************/

package org.eclipse.mylyn.reviews.r4egerrit.ui.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.mylyn.commons.workbench.DelayedRefreshJob;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.GerritCorePlugin;
import org.eclipse.mylyn.internal.gerrit.core.GerritQuery;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.SynchronizeEditorAction;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.reviews.r4e_gerrit.R4EGerritPlugin;
import org.eclipse.mylyn.reviews.r4e_gerrit.core.R4EGerritTask;
import org.eclipse.mylyn.reviews.r4e_gerrit.core.R4EGerritTaskDataCollector;
import org.eclipse.mylyn.reviews.r4e_gerrit.core.R4EQueryException;
import org.eclipse.mylyn.reviews.r4e_gerrit.ui.R4EGerritUi;
import org.eclipse.mylyn.reviews.r4e_gerrit.ui.internal.model.ReviewTableData;
import org.eclipse.mylyn.reviews.r4e_gerrit.ui.internal.model.UIReviewTable;
import org.eclipse.mylyn.reviews.r4e_gerrit.ui.internal.utils.R4EGERRITUIConstants;
import org.eclipse.mylyn.reviews.r4e_gerrit.ui.internal.utils.R4EGerritServerUtility;
import org.eclipse.mylyn.reviews.r4e_gerrit.ui.internal.utils.R4EUIConstants;
import org.eclipse.mylyn.reviews.r4e_gerrit.ui.internal.utils.UIUtils;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.IRepositoryModel;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.IServiceLocator;
import org.osgi.framework.Version;

/**
 * This class initiate a new workbench view. The view
 * shows data obtained from R4E-Gerrit model. The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view.
 *  
 * @author Jacques Bouthillier
 * @version $Revision: 1.0 $
 */

@SuppressWarnings("restriction")
public class R4EGerritTableView extends ViewPart implements ITaskListChangeListener {

	// ------------------------------------------------------------------------
	// Constants
	// ------------------------------------------------------------------------

    /**
	 * The ID of the view as specified by the extension.
	 */
	public static final String VIEW_ID = "org.eclipse.mylyn.reviews.r4egerrit.ui.views.R4EGerritTableView";
	
	/**
	 * Field COMMAND_MESSAGE. (value is ""Search Gerrit info ..."")
	 */
	private static final String COMMAND_MESSAGE = "Search Gerrit info ...";

	// Labels for the Search 
	private final String SEARCH_LABEL 	= "Current Query:";
	private final String SEARCH_BTN 	= "Search";
	private final String TOTAL_COUNT 	= "Total reviews:";
	private final String GERRIT_LABEL   = " - Gerrit ";

	private final int SEARCH_WIDTH = 300;
	private final int REPO_WIDTH = 200;
	private final int VERSION_WIDTH = 35;
	
	private final String SEARCH_TOOLTIP = "Ex. status:open (or is:open) \n status:merged \n "
			+ "is:draft \n status:open project:Foo \n "
			+ "See explanation by selecting in the toolbar \n "
			+ "Documentation > Searching";
	
	//Numbers of menu items in the Search pulldown menu; SEARCH_SIZE_MENU_LIST + 1 will be the max
	private final int SEARCH_SIZE_MENU_LIST = 4;

	// ------------------------------------------------------------------------
	// Member variables
	// ------------------------------------------------------------------------

	private GerritConnector fConnector = GerritCorePlugin.getDefault().getConnector();;

	private TaskRepository fTaskRepository = null;
	
	private RepositoryQuery fCurrentQuery = null;

	private static R4EGerritTableView rtv = null;
	private Label 	fSearchForLabel;
	private Label	fSearchResulLabel;

	private Label	fRepositoryVersionResulLabel;
	
    private Label 	fReviewsTotalLabel;	
    private Label 	fReviewsTotalResultLabel;	
	
	private Combo	fSearchRequestText;
	private Button	fSearchRequestBtn;
	
	private LinkedHashSet<String>   fRequestList = new LinkedHashSet<String>();
	private static TableViewer fViewer;
	
	private ReviewTableData fReviewTable = new ReviewTableData();
	private R4EGerritServerUtility fServerUtil = R4EGerritServerUtility.getInstance();

	private Map<TaskRepository, String> fMapRepoServer = null;

	private Action doubleClickAction;
	
	private final LinkedHashSet<Job> fJobs = new LinkedHashSet<Job>();
	

	// ------------------------------------------------------------------------
	// TableRefreshJob
	// ------------------------------------------------------------------------

	private TableRefreshJob fTableRefreshJob;

	// Periodical refreshing job
	private final class TableRefreshJob extends DelayedRefreshJob {

		private TableRefreshJob(TableViewer viewer, String name) {
			super(viewer, name);
		}

		@Override
		protected void doRefresh(Object[] items) {
	        Display.getDefault().syncExec(new Runnable() {
	            @Override
	            public void run() {
	            	fViewer.setInput(fReviewTable.getReviews()); 
	            	//Refresh the counter
                    setReviewsTotalResultLabel(Integer.toString(fReviewTable.getReviews().length));
	            	fViewer.refresh(false, false);
	            }
	        });
		}
	}

	// ------------------------------------------------------------------------
	// Constructor and life cycle
	// ------------------------------------------------------------------------

	/**
	 * The constructor.
	 */
	public R4EGerritTableView() {
		super();
		rtv = this;
	}

	
	public void setConnector (GerritConnector connector)
	{
			fConnector = connector;
	}


	public void setReviewTableData (ReviewTableData ReviewTable)
	{
		fReviewTable = ReviewTable;
	}

	
	public void setGerritServerUtility (R4EGerritServerUtility ServerUtil)
	{
		fServerUtil = ServerUtil;
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		TasksUiPlugin.getTaskList().removeChangeListener(this);
		fTableRefreshJob.cancel();
		cleanJobs();		
	}
	
	private void cleanJobs () {
		Iterator<Job> iter = fJobs.iterator();
		while (iter.hasNext()) {
			Job job  = iter.next();
			job.sleep();
			job.cancel();
		}
        fJobs.clear();
	}

	/**
	 * Refresh the view content 
	 */
	private void refresh() {
		fTableRefreshJob.doRefresh(null);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite aParent) {
		ScrolledComposite sc = new ScrolledComposite(aParent,  SWT.H_SCROLL| SWT.V_SCROLL | SWT.BORDER);
		sc.setExpandHorizontal(true);
		Composite c = new Composite(sc, SWT.NONE);
		sc.setContent(c);
    	sc.setExpandVertical(true);

		createSearchSection(c);
		UIReviewTable reviewTable = new UIReviewTable();
		fViewer = reviewTable.createTableViewerSection(c);
			
		// Setup the view layout
		createLayout(c);

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

		// Start the periodic refresh job
		fTableRefreshJob = new TableRefreshJob(fViewer, "Refresh table");

		// Listen on query results
		TasksUiPlugin.getTaskList().addChangeListener(this);
		
		sc.setMinSize(c.computeSize(SWT.DEFAULT, SWT.DEFAULT));		
	}

	private void createLayout(Composite aParent) {

		//Add a listener when the view is resized
		GridLayout layout = new GridLayout();
		layout.numColumns = 1 ;
		layout.makeColumnsEqualWidth = false;
		
		aParent.setLayout(layout);
		
	}

	/**
	 * Create a group to show the search command and a search text
	 * @param Composite aParent
	 */
	@SuppressWarnings("unchecked")
	private void createSearchSection(Composite aParent) {
		
		final Group formGroup =  new Group(aParent, SWT.SHADOW_ETCHED_IN | SWT.H_SCROLL);
		GridData gribDataGroup = new GridData(GridData.FILL_HORIZONTAL);
		formGroup.setLayoutData(gribDataGroup);
		
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.verticalSpacing = 1;
		layout.makeColumnsEqualWidth = false;
		
		formGroup.setLayout(layout);

		//Left side of the Group
		//Create a form to maintain the search data
		Composite leftSearchForm = UIUtils.createsGeneralComposite(formGroup, SWT.NONE);

		GridData gribDataViewer = new GridData(GridData.FILL_HORIZONTAL);
		leftSearchForm.setLayoutData(gribDataViewer);

		GridLayout leftLayoutForm = new GridLayout();
		leftLayoutForm.numColumns = 5;
		leftLayoutForm.marginHeight = 0;
		leftLayoutForm.makeColumnsEqualWidth = false;
		leftLayoutForm.horizontalSpacing = 10;
		
		leftSearchForm.setLayout(leftLayoutForm);
		
		//Label to display the repository and the version
		fRepositoryVersionResulLabel = new Label(leftSearchForm, SWT.NONE);
		fRepositoryVersionResulLabel.setLayoutData(new GridData(REPO_WIDTH, SWT.DEFAULT));	
		
		
		// Label for SEARCH for
		fSearchForLabel = new Label(leftSearchForm, SWT.NONE);
		fSearchForLabel.setText(SEARCH_LABEL);
		
		// Label for the SEARCH request
		fSearchResulLabel = new Label(leftSearchForm, SWT.NONE);
		fSearchResulLabel.setLayoutData(new GridData(REPO_WIDTH, SWT.DEFAULT));

		//Label to display Total reviews
		fReviewsTotalLabel = new Label(leftSearchForm, SWT.NONE);
		fReviewsTotalLabel.setText(TOTAL_COUNT);
		
		fReviewsTotalResultLabel = new Label(leftSearchForm, SWT.NONE);
		fReviewsTotalResultLabel.setLayoutData(new GridData(VERSION_WIDTH, SWT.DEFAULT));		

		
		//Right side of the Group
		Composite rightSsearchForm = UIUtils.createsGeneralComposite(formGroup, SWT.NONE);
		GridData gribDataViewer2 = new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_END);
		rightSsearchForm.setLayoutData(gribDataViewer2);
		GridLayout rightLayoutForm = new GridLayout();
		rightLayoutForm.numColumns = 2;
		rightLayoutForm.marginHeight = 0;
		rightLayoutForm.makeColumnsEqualWidth = false;
		
		rightSsearchForm.setLayout(rightLayoutForm);

		//Create a SEARCH text data entry
		fSearchRequestText = new Combo (rightSsearchForm, SWT.BORDER);
		fSearchRequestText.setLayoutData(new GridData(SEARCH_WIDTH, SWT.DEFAULT));
		fSearchRequestText.setToolTipText(SEARCH_TOOLTIP);
		//Get the last saved commands
		fRequestList = fServerUtil.getListLastCommands();
		setSearchText("");
	
		//Create a SEARCH button 
		fSearchRequestBtn = new Button (rightSsearchForm, SWT.NONE);
		fSearchRequestBtn.setText(SEARCH_BTN);
		fSearchRequestBtn.addListener(SWT.Selection, new Listener()  {

			@Override
			public void handleEvent(Event event) {
				processCommands(GerritQuery.CUSTOM);
			}});

	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				R4EGerritTableView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(fViewer.getControl());
		fViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, fViewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
	}

	private void fillContextMenu(IMenuManager manager) {
		CommandContributionItem[] contribItems = buildContributions();
		for (int index = 0; index < contribItems.length; index++) {
			manager.add(contribItems[index]);			
		}
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
	}

	private void makeActions() {
		doubleClickAction = new Action() {
			@Override
			public void run() {

			    // -------------------------------------------------
				// Open an editor with the detailed task information
                // -------------------------------------------------

			    // Retrieve the single table selection ("the" task)
				ISelection selection = fViewer.getSelection();
				if (!(selection instanceof IStructuredSelection)) {
					return;
				}
				IStructuredSelection structuredSelection = (IStructuredSelection) selection;
				if (structuredSelection.size() != 1) {
					return;
				}
				Object element = structuredSelection.getFirstElement();
				if (!(element instanceof AbstractTask)) {
					return;
				}
				AbstractTask task = (AbstractTask) element;

				// Open the task in the proper editor
                AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(GerritConnector.CONNECTOR_KIND);
		        IEditorInput editorInput = connectorUi.getTaskEditorInput(fTaskRepository, task);
		        if (editorInput == null) {
		            editorInput = new TaskEditorInput(fTaskRepository, task);
		        }
		        String editorId = connectorUi.getTaskEditorId(task);
				IEditorPart editorPart = TasksUiUtil.openEditor(editorInput, editorId, null);
				if (editorPart instanceof TaskEditor) {
					TaskEditor taskEditor = (TaskEditor) editorPart;
					//Allow to open a Task even if not found locally yet
					SynchronizeEditorAction synchAction = new SynchronizeEditorAction();
					synchAction.selectionChanged(new StructuredSelection(taskEditor));
					synchAction.run();
					if (task instanceof R4EGerritTask) {
						//Refresh the table column with the appropriate data, so the "CR" and "V" column gets updated
						fReviewTable.updateReviewItem((R4EGerritTask) task);
						refresh() ;
					}
					
				}
			}
		};
	}

	private void hookDoubleClickAction() {
		fViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		fViewer.getControl().setFocus();
	}
	

	/**
	 * Create a list for commands to add to the table review list menu
	 * @return CommandContributionItem[]
	 */
	private CommandContributionItem[] buildContributions() {
		IServiceLocator serviceLocator = getViewSite().getActionBars()
				.getServiceLocator();
		CommandContributionItem[] contributionItems = new CommandContributionItem[1];
		CommandContributionItemParameter contributionParameter = new CommandContributionItemParameter(
				serviceLocator, R4EGERRITUIConstants.ADJUST_MY_STARRED_NAME,
				R4EGERRITUIConstants.ADJUST_MY_STARRED_COMMAND_ID,
				CommandContributionItem.STYLE_PUSH);

		contributionParameter.label = R4EGERRITUIConstants.ADJUST_MY_STARRED_NAME;
		contributionParameter.visibleEnabled = true;
		contributionItems[0] = new CommandContributionItem(
				contributionParameter);
		

		return contributionItems;

	}

	public static TableViewer getTableViewer() {
		return fViewer;
	}
	
	public  TaskRepository getTaskRepository() {
		 return fTaskRepository;
	}
	
	public static R4EGerritTableView getActiveView() {
		IViewPart viewPart = null;
		if (rtv != null) {
			return rtv;
		} else {
			IWorkbench workbench = R4EGerritUi.getDefault().getWorkbench();
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			IWorkbenchPage  page = null;
			if (window != null ) {
				page = workbench.getActiveWorkbenchWindow()
						.getActivePage();				
			}
			
			if (page != null) {
				viewPart = page.findView(VIEW_ID);
				// The following can occurs in LINUX environment since
				// killing the window call the dispose() method

				if (viewPart == null) {
					try {
						viewPart = page.showView(VIEW_ID, null,
								org.eclipse.ui.IWorkbenchPage.VIEW_CREATE);
					} catch (PartInitException e) {
						R4EGerritUi.Ftracer.traceWarning("PartInitException:   " 
								+ e.getLocalizedMessage() ); //$NON-NLS-1$
						e.printStackTrace();
					}
					R4EGerritUi.Ftracer.traceWarning("getActiveView() SHOULD (JUST) CREATED A NEW Table:"
							+ viewPart ); //$NON-NLS-1$

				}
			} 
			
			return (R4EGerritTableView) viewPart;
		}
	}

	/**
	 * bring the R4E view visible to the current workbench
	 * 
	 */
	public void openView() {
		IWorkbench workbench = R4EGerritUi.getDefault().getWorkbench();
		IWorkbenchPage page = workbench.getActiveWorkbenchWindow()
				.getActivePage();
		IViewPart viewPart = page.findView(VIEW_ID);
		// if the review view is not showed yet,
		if (viewPart == null) {
			try {
				viewPart = page.showView(VIEW_ID);
			} catch (PartInitException e) {
				R4EGerritUi.Ftracer.traceWarning("PartInitException:   " 
						+ e.getLocalizedMessage() ); //$NON-NLS-1$
			}
		}
		// if there exists the view, but if not on the top,
		// then brings it to top when the view is already showed.
		else if (!page.isPartVisible(viewPart)) {
			page.bringToTop(viewPart);
		}
	}
	
	/**
	 * Process the commands based on the Gerrit string 
	 * @param String aQuery
	 */
	public void processCommands(String aQuery) {
		R4EGerritUi.Ftracer.traceInfo("Process command :   "  + aQuery );
		String lastSaved = fServerUtil.getLastSavedGerritServer();
		if (lastSaved != null) {
			//Already saved a Gerrit server, so use it
			fTaskRepository  = fServerUtil.getTaskRepo(lastSaved);
		}
		
		if (fTaskRepository == null) {
			//If we did not find the task Repository
			fMapRepoServer = R4EGerritServerUtility.getInstance().getGerritMapping();
			//Verify How many gerrit server are defined
			if (fMapRepoServer.size() == 1) {
				Set<TaskRepository> mapSet = fMapRepoServer.keySet();
				for (TaskRepository key: mapSet) {
				    fTaskRepository = key;
					//Save it for the next query time
					fServerUtil.saveLastGerritServer(key.getRepositoryUrl());
					break;
				}
				
			}
		}
		
		//We should have a TaskRepository here, otherwise, the user need to define one
		if (fTaskRepository == null) {
			String msg = "You need to define a Gerrit repository.";
			String reason = "No Gerrit repository has been selected yet.";
			R4EGerritUi.Ftracer.traceInfo(msg );
			UIUtils.showErrorDialog(msg, reason);
		} else {
			updateTable (fTaskRepository, aQuery);
		}

	}
	
	/**
	 * Find the last Gerrit server being used , otherwise consider the Eclipse.org gerrit server version as a default
	 * @return Version
	 */
	public Version getlastGerritServerVersion () {
		Version version = null;
		String lastSaved = fServerUtil.getLastSavedGerritServer();
		if (lastSaved != null) {
			//Already saved a Gerrit server, so use it
			fTaskRepository  = fServerUtil.getTaskRepo(lastSaved);
		}
		
		if (fTaskRepository == null) {
			//If we did not find the task Repository
			fMapRepoServer = R4EGerritServerUtility.getInstance().getGerritMapping();
			//Verify How many gerrit server are defined
			if (fMapRepoServer.size() == 1) {
				Set<TaskRepository> mapSet = fMapRepoServer.keySet();
				for (TaskRepository key: mapSet) {
				    fTaskRepository = key;
					//Save it for the next query time
					fServerUtil.saveLastGerritServer(key.getRepositoryUrl());
					break;
				}
				
			}
		}
		
		//We should have a TaskRepository here, otherwise, the user need to define one
		if (fTaskRepository != null) {
			if (fConnector != null) {
				GerritClient gerritClient = fConnector.getClient(fTaskRepository);
				try {
					version = gerritClient.getVersion(new NullProgressMonitor());
					R4EGerritUi.Ftracer.traceInfo("Selected version: " + version.toString());
				} catch (GerritException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return version;
	}
	
	/**
	 * Verify if the Gerrit version is before 2.5
	 * @return boolean
	 */
	public boolean isGerritVersionBefore_2_5 () {
		boolean ret = false;
	
        Version version = getlastGerritServerVersion ();
        if (version != null && version.getMajor() >= 2) {
        	if (version.getMinor() < 5) {
        		ret = true;
        	}
        }
		return ret;
	}
	
	/**
	 * @param aTaskRepo
	 * @param aQueryType
	 * @return
	 */
	private Object updateTable(final TaskRepository aTaskRepo, final String aQueryType)  {
		
		String cmdMessage = COMMAND_MESSAGE + " " + aTaskRepo.getUrl() + " : " + aQueryType;
		final Job job = new Job(cmdMessage) {

			public String familyName = R4EUIConstants.R4E_UI_JOB_FAMILY;

			@Override
			public boolean belongsTo(Object aFamily) {
				return familyName.equals(aFamily);
			}

			@Override
			public IStatus run(final IProgressMonitor aMonitor) {
				aMonitor.beginTask(COMMAND_MESSAGE, IProgressMonitor.UNKNOWN);
						
				R4EGerritPlugin.Ftracer.traceInfo("repository:   " + aTaskRepo.getUrl() +
						"\t query: " + aQueryType); //$NON-NLS-1$
				
				// If there is only have one Gerrit server, we can proceed as if it was already used before
				IStatus status = null;
				try {
	                fReviewTable.createReviewItem(aQueryType, aTaskRepo);
	                getReviews(aTaskRepo, aQueryType);
	                Display.getDefault().syncExec(new Runnable() {
	                    @Override
	                    public void run() {
	                        setSearchLabel(aQueryType);
	                        if (aQueryType != GerritQuery.CUSTOM ) {
		                        setSearchText(aQueryType);	                        	
	                        } else {
	                        	//Record the custom query
	                        	setSearchText (getSearchText());
	                        }
	                        GerritClient gerritClient = fConnector.getClient(aTaskRepo);
	                        try {
								R4EGerritUi.Ftracer.traceInfo("GerritClient: " + gerritClient.getVersion(new NullProgressMonitor()) );
								setRepositoryVersionLabel (aTaskRepo.getRepositoryLabel(), gerritClient.getVersion(new NullProgressMonitor()).toString() );
							} catch (GerritException e1) {
								e1.printStackTrace();
							}
	                        
	                    }
	                });
	                status = Status.OK_STATUS;
                }
                catch (R4EQueryException e) {
                    status = e.getStatus();
                    R4EGerritPlugin.Ftracer.traceWarning(status.toString());
//                    UIUtils.showErrorDialog(e.getMessage(), status.toString());
                }

				aMonitor.done();
				fJobs.remove(this);
				return status;
			}
		};
		//Clean some Jobs if still running
		cleanJobs();
		
		fJobs.add(job);
		job.setUser(true);
		job.schedule();
		
		return null;
	}

	private void setSearchLabel (String aSt) {
		if (!fSearchResulLabel.isDisposed() ) {
			fSearchResulLabel.setText(aSt);
		}
	}

	private void setSearchText (String aSt) {
		if (!fSearchRequestText.isDisposed() ) {
			if (aSt != null && aSt != "" ) {
				int index = -1;
				String [] ar = fSearchRequestText.getItems();
				for (int i =0; i< ar.length; i++ ) {
					if ( ar[i].equals(aSt.trim()) ) {
						index = i;
						break;
					}
				}
				
				if (index != -1) {
					fRequestList.remove(fRequestList.remove(ar[index]));
				} else {
					//Remove the oldest element from the list
					if (fRequestList.size() > SEARCH_SIZE_MENU_LIST) {
						Object obj= fRequestList.iterator().next(); //Should be the first item in the list
						fRequestList.remove(fRequestList.remove(obj));
					}
				}
				//Add the new text in the combo
				fRequestList.add (aSt.trim());
				//Save the list of commands in file
				fServerUtil.saveLastCommandList(fRequestList);
				
			}
			
			fSearchRequestText.setItems( reverseOrder ( fRequestList.toArray(new String[0])));
			if (aSt != null && aSt != "" ) {
				fSearchRequestText.select(0);			
			} else {
				//Leave the text empty
				fSearchRequestText.setText("");
			}
		}
	}
	
	/**
	 * Take the list of last save queries and reverse the order to have 
	 * the latest selection to be the first one on the pull-down menu
	 * @param aList String[]
	 * @return String[] reverse order
	 */
	private String[] reverseOrder (String[] aList) {
		int size = aList.length;
		int index = size - 1;
		String [] rev = new String[size];
		for (int i = 0; i < size; i++) {
			rev[i] = aList[index--];
		}
		return rev;
	}
	
	private String getSearchText () {
		if (!fSearchRequestText.isDisposed() ) {
			final String[] str  = new String[1];
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					str[0] = fSearchRequestText.getText().trim();
					R4EGerritUi.Ftracer.traceInfo( "Custom string: " + str[0]);
				}
			});
			return str[0];
		}
		return null;
	}
	
	// ------------------------------------------------------------------------
	// Query handling
	// ------------------------------------------------------------------------

    /**
     * Perform the requested query and convert the resulting tasks in R4EGerritTask:s
     * 
     * @param repository the tasks repository
     * @param queryType	the query
     * 
     * @throws R4EQueryException
     */
    private void getReviews(TaskRepository repository, String queryType) throws R4EQueryException {

    	// Format the query id
    	String queryId = rtv.getTitle() + " - " + queryType;

    	// Retrieve the query from the repository (if previously defined)
    	Set<RepositoryQuery> queries = TasksUiInternal.getTaskList().getQueries();
    	RepositoryQuery query = null;

		//Look to a regular query, otherwise, need to create the proper custom query
    	if (queryType == GerritQuery.CUSTOM ) {
    		//For Custom query, need to get the extra data
    		queryId = rtv.getTitle() + " - " + queryType + "- " + getSearchText();
    	}
    	
    	for (RepositoryQuery rquery : queries) {
    		if (rquery.getRepositoryUrl().equals(repository.getRepositoryUrl()) && rquery.getSummary().equals(queryId)) {
    			query = rquery;
    			break;
    		}
    	}

    	// If not found, create one and save it
    	if (query == null) {
        	IRepositoryModel repositoryModel = TasksUi.getRepositoryModel();
        	query = (RepositoryQuery) repositoryModel.createRepositoryQuery(repository);
        	query.setSummary(queryId);
            query.setAttribute(GerritQuery.TYPE, queryType);
    		query.setAttribute(GerritQuery.PROJECT, null);
   		 	// Flag to prevent the query to be displayed in the Task List 
    		// and pop-up list showing all the times
   		 	query.setAttribute(ITasksCoreConstants.ATTRIBUTE_HIDDEN, Boolean.toString(true));
    		if (queryType == GerritQuery.CUSTOM ) {
        		query.setAttribute(GerritQuery.QUERY_STRING, getSearchText());
    		} else {
        		query.setAttribute(GerritQuery.QUERY_STRING, null);    			
    		}
    		TasksUiPlugin.getTaskList().addQuery(query);
    	}

    	// Save query
    	fCurrentQuery = query;
    	
    	// Fetch the list of reviews and pre-populate the table
        R4EGerritTask[] reviews = getReviewList(repository, query);
        
        fReviewTable.init(reviews);
        refresh();
    	
    	// Start the long-running synchronized query; the individual review details
        // are handled by ITaskListChangeListener.containersChanged()
        GerritConnector connector = GerritCorePlugin.getDefault().getConnector();

        //Do we re-synch for all Gerrit server version or just Gerrit 2.4 ?
//        if (isGerritVersionBefore_2_5()) {
//            Job job = null;
//            try {
//                job = TasksUiInternal.synchronizeQuery(connector, query, null, true);
//    		} catch (Exception e) {
//    			if (job != null) {
//    				job.cancel();
//    			}
//    		}
//    	}
        	
        //Should sync any Gerrit server instead of waiting the timer re-synch 
        Job job = null;
        try {
            job = TasksUiInternal.synchronizeQuery(connector, query, null, true);
		} catch (Exception e) {
			if (job != null) {
				job.cancel();
			}
		}
    }
	
    private R4EGerritTask[] getReviewList(TaskRepository repository, RepositoryQuery aQuery) throws R4EQueryException {
    	
        // Execute the query
        R4EGerritTaskDataCollector resultCollector = new R4EGerritTaskDataCollector();
        IStatus status = fConnector.performQuery(repository, aQuery, resultCollector, null, new NullProgressMonitor());
        if (!status.isOK()) {
            String msg = "Unable to read the Gerrit server.";
            throw new R4EQueryException(status, msg);
        }        

        // Extract the result
        List<R4EGerritTask> reviews = new ArrayList<R4EGerritTask>();
        List<TaskData> tasksData = resultCollector.getResults();
        for (TaskData taskData : tasksData) {
            R4EGerritTask review = new R4EGerritTask(taskData);
            reviews.add(review);
        }
        return reviews.toArray(new R4EGerritTask[0]);
    }

	// ------------------------------------------------------------------------
	// ITaskListChangeListener
	// ------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.mylyn.internal.tasks.core.ITaskListChangeListener#containersChanged(java.util.Set)
	 */
	@Override
	public void containersChanged(final Set<TaskContainerDelta> deltas) {
		for (TaskContainerDelta taskContainerDelta : deltas) {
			IRepositoryElement element = taskContainerDelta.getElement();
			switch (taskContainerDelta.getKind()) {
			case ROOT:
				refresh();
				break;
			case ADDED:
			case CONTENT:
				if (element != null && element instanceof TaskTask) {
					updateReview((TaskTask) element);
				}
				refresh();
				break;
			case DELETED:
			case REMOVED:
				if (element != null && element instanceof TaskTask) {
					deleteReview((TaskTask) element);
				}
				refresh();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Delete a review 
	 */
	private synchronized void deleteReview(TaskTask task) {
		fReviewTable.deleteReviewItem(task.getTaskId());
	}

	/**
	 * Add/update a review 
	 */
	private synchronized void updateReview(TaskTask task) {
		String summary = task.getSummary();
		boolean ourQuery = task.getParentContainers().contains(fCurrentQuery);
		if (ourQuery && summary != null && !summary.equals("")) {
			try {
				TaskData taskData = fConnector.getTaskData(fTaskRepository, task.getTaskId(), new NullProgressMonitor());
				R4EGerritTask gtask = new R4EGerritTask(taskData);
				if (gtask.getAttribute(R4EGerritTask.DATE_COMPLETION) == null) {
					fReviewTable.updateReviewItem(gtask);
		        }
			} catch (CoreException e) {
			}
		}
		
	}

	private void setRepositoryVersionLabel(String aRepo, String aVersion) {
		if (!fRepositoryVersionResulLabel.isDisposed() ) {
			// e.g. "Eclipse.org Reviews - Gerrit 2.6.1" 
			String s = aRepo + GERRIT_LABEL + aVersion;
			fRepositoryVersionResulLabel.setText(s);
		}
	}

	private void setReviewsTotalResultLabel(String aSt) {
		if (!fReviewsTotalResultLabel.isDisposed() ) {
			fReviewsTotalResultLabel.setText(aSt);
		}
	}

}
