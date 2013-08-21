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
 ******************************************************************************/

package org.eclipse.mylyn.reviews.r4egerrit.ui.views;

import java.util.ArrayList;
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
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.mylyn.commons.workbench.DelayedRefreshJob;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.GerritCorePlugin;
import org.eclipse.mylyn.internal.gerrit.core.GerritQuery;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
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
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
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
	private final String SEARCH_LABEL = "Current Query:";
	private final String SEARCH_BTN = "Search";
	private final String REPOSITORY = "Repository:";

	private final int SEARCH_WIDTH = 150;
	
	// ------------------------------------------------------------------------
	// Member variables
	// ------------------------------------------------------------------------

	private GerritConnector fConnector;

	private TaskRepository fTaskRepository;
	
	private RepositoryQuery fCurrentQuery = null;

	private static R4EGerritTableView rtv = null;
	private Label 	fSearchForLabel;
	private Label	fSearchResulLabel;

	private Label 	fRepositoryLabel;
	private Label	fRepositoryResulLabel;

	private Text	fSearchRequestText;
	private Button	fSearchRequestBtn;
	
	private static TableViewer fViewer;
	
	private ReviewTableData fReviewTable = new ReviewTableData();
	private R4EGerritServerUtility fServerUtil = new R4EGerritServerUtility();

	private Map<TaskRepository, String> fMapRepoServer = null;

	private Action doubleClickAction;

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
		fConnector = GerritCorePlugin.getDefault().getConnector();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		TasksUiPlugin.getTaskList().removeChangeListener(this);
		fTableRefreshJob.cancel();
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

		createSearchSection(aParent);
		UIReviewTable reviewTable = new UIReviewTable();
		fViewer = reviewTable.createTableViewerSection(aParent);
			
		// Setup the view layout
		createLayout(aParent);

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

		// Start the periodic refresh job
		fTableRefreshJob = new TableRefreshJob(fViewer, "Refresh table");

		// Listen on query results
		TasksUiPlugin.getTaskList().addChangeListener(this);
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
		leftLayoutForm.numColumns = 2;
		leftLayoutForm.marginHeight = 0;
		leftLayoutForm.makeColumnsEqualWidth = false;
		
		leftSearchForm.setLayout(leftLayoutForm);
		
		// Label for SEARCH for
		fSearchForLabel = new Label(leftSearchForm, SWT.NONE);
		fSearchForLabel.setText(SEARCH_LABEL);
		
		// Label for the SEARH request
		fSearchResulLabel = new Label(leftSearchForm, SWT.NONE);
		fSearchResulLabel.setLayoutData(new GridData(SEARCH_WIDTH, SWT.DEFAULT));

		//Label to display the repository
		fRepositoryLabel = new Label(leftSearchForm, SWT.NONE);
		fRepositoryLabel.setText(REPOSITORY);
		
		fRepositoryResulLabel = new Label(leftSearchForm, SWT.NONE);
		fRepositoryResulLabel.setLayoutData(new GridData(SEARCH_WIDTH, SWT.DEFAULT));

		
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
		fSearchRequestText = new Text (rightSsearchForm, SWT.BORDER);
		fSearchRequestText.setLayoutData(new GridData(SEARCH_WIDTH, SWT.DEFAULT));
	
		//Create a SEARCH button 
		fSearchRequestBtn = new Button (rightSsearchForm, SWT.NONE);
		fSearchRequestBtn.setText(SEARCH_BTN);
		fSearchRequestBtn.addListener(SWT.Selection, new Listener()  {

			@Override
			public void handleEvent(Event event) {
				UIUtils.notInplementedDialog("Search Button");
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
				TasksUiUtil.openEditor(editorInput, editorId, null);
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
			fMapRepoServer = R4EGerritServerUtility.getGerritMapping();
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
//			try {
			String msg = "You need to define a Gerrit repository.";
			String reason = "No Gerrit repository has been selected yet.";
				R4EGerritUi.Ftracer.traceInfo(msg );
				UIUtils.showErrorDialog(msg, reason);
//			} catch (NotDefinedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		} else {
			updateTable (fTaskRepository, aQuery);
		}

	}
	
	/**
	 * @param aTaskRepo
	 * @param aQuery
	 * @return
	 */
	private Object updateTable(final TaskRepository aTaskRepo, final String aQuery)  {
		
		final Job job = new Job(COMMAND_MESSAGE) {

			public String familyName = R4EUIConstants.R4E_UI_JOB_FAMILY;

			@Override
			public boolean belongsTo(Object aFamily) {
				return familyName.equals(aFamily);
			}

			@Override
			public IStatus run(final IProgressMonitor aMonitor) {
				aMonitor.beginTask(COMMAND_MESSAGE, IProgressMonitor.UNKNOWN);
						
				R4EGerritPlugin.Ftracer.traceInfo("repository:   " + aTaskRepo.getUrl() +
						"\t query: " + aQuery); //$NON-NLS-1$
				
				// If there is only have one Gerrit server, we can proceed as if it was already used before
				IStatus status = null;
				try {
	                fReviewTable.createReviewItem(aQuery, aTaskRepo);
	                getReviews(aTaskRepo, aQuery);
	                Display.getDefault().syncExec(new Runnable() {
	                    @Override
	                    public void run() {
	                        setSearchLabel(aQuery);
	                        setSearchText(aQuery);
	                        setRepositoryLabel(aTaskRepo.getRepositoryLabel());
	                    }
	                });
	                status = Status.OK_STATUS;
                }
                catch (R4EQueryException e) {
                    status = e.getStatus();
                    R4EGerritPlugin.Ftracer.traceWarning(status.toString());
                    UIUtils.showErrorDialog(e.getMessage(), status.toString());
                }

				aMonitor.done();
				return status;
			}
		};
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
			fSearchRequestText.setText(aSt);
		}
	}
	
	private void setRepositoryLabel(String aSt) {
		if (!fRepositoryResulLabel.isDisposed() ) {
			fRepositoryResulLabel.setText(aSt);
		}
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
    		query.setAttribute(GerritQuery.QUERY_STRING, null);
    		TasksUiPlugin.getTaskList().addQuery(query);
    	}

    	// Save query
    	fCurrentQuery = query;
    	
    	// Fetch the list of reviews and pre-populate the table
        R4EGerritTask[] reviews = getReviewList(repository, queryType);
        fReviewTable.init(reviews);
        refresh();
    	
    	// Start the long-running synchronized query; the individual review details
        // are handled by ITaskListChangeListener.containersChanged()
        GerritConnector connector = GerritCorePlugin.getDefault().getConnector();
        Job job = null;
        try {
            job = TasksUiInternal.synchronizeQuery(connector, query, null, true);
		} catch (Exception e) {
			if (job != null) {
				job.cancel();
			}
		}
    }
	
    private R4EGerritTask[] getReviewList(TaskRepository repository, String queryType) throws R4EQueryException {

        // Format the query
        IRepositoryQuery query = new RepositoryQuery(repository.getConnectorKind(), "query"); //$NON-NLS-1$
        query.setAttribute(GerritQuery.TYPE, queryType);

        // Execute the query
//        GerritConnector connector = GerritCorePlugin.getDefault().getConnector();
        R4EGerritTaskDataCollector resultCollector = new R4EGerritTaskDataCollector();
        IStatus status = fConnector.performQuery(repository, query, resultCollector, null, new NullProgressMonitor());
        if (!status.isOK()) {
            String msg = "Unable to read the Gerrit server.";
            throw new R4EQueryException(status, msg);
        }        

        // Extract the result
        List<R4EGerritTask> reviews = new ArrayList<R4EGerritTask>();
        List<TaskData> tasksData = resultCollector.getResults();
        for (TaskData taskData : tasksData) {
            R4EGerritTask review = new R4EGerritTask(taskData);
            if (review.getAttribute(R4EGerritTask.DATE_COMPLETION) == null) {
                reviews.add(review);
            }
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

}
