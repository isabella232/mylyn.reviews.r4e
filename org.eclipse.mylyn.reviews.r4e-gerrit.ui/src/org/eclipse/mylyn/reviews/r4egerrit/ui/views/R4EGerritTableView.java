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
 ******************************************************************************/

package org.eclipse.mylyn.reviews.r4egerrit.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.GerritCorePlugin;
import org.eclipse.mylyn.internal.gerrit.core.GerritQuery;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.reviews.r4e_gerrit.R4EGerritPlugin;
import org.eclipse.mylyn.reviews.r4e_gerrit.core.R4EGerritQueryUtils;
import org.eclipse.mylyn.reviews.r4e_gerrit.core.R4EGerritTask;
import org.eclipse.mylyn.reviews.r4e_gerrit.core.R4EQueryException;
import org.eclipse.mylyn.reviews.r4e_gerrit.ui.R4EGerritUi;
import org.eclipse.mylyn.reviews.r4e_gerrit.ui.internal.model.ReviewTableData;
import org.eclipse.mylyn.reviews.r4e_gerrit.ui.internal.model.UIReviewTable;
import org.eclipse.mylyn.reviews.r4e_gerrit.ui.internal.utils.R4EGERRITUIConstants;
import org.eclipse.mylyn.reviews.r4e_gerrit.ui.internal.utils.R4EGerritServerUtility;
import org.eclipse.mylyn.reviews.r4e_gerrit.ui.internal.utils.R4EUIConstants;
import org.eclipse.mylyn.reviews.r4e_gerrit.ui.internal.utils.UIUtils;
import org.eclipse.mylyn.tasks.core.IRepositoryModel;
import org.eclipse.mylyn.tasks.core.ITask;
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
import org.eclipse.swt.widgets.Table;
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
 * @author Jacques Bouthillier
 * @version $Revision: 1.0 $
 *
 */

/**
 * This class initiate a new workbench view. The view
 * shows data obtained from R4E-Gerrit model. The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. 
 */

@SuppressWarnings("restriction")
public class R4EGerritTableView extends ViewPart {

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
	private final String SEARCH_LABEL = "Search for:";
	private final String SEARCH_BTN = "Search";
	private final String REPOSITORY = "Repository:";

	private final int SEARCH_WIDTH = 150;
	
	// ------------------------------------------------------------------------
	// Member variables
	// ------------------------------------------------------------------------

	private static R4EGerritTableView rtv = null;

	private Label 	fSearchForLabel;
	private Label	fSearchResulLabel;

    private TaskRepository fTaskRepository;

    private Label 	fRepositoryLabel;
	private Label	fRepositoryResulLabel;

	private Text	fSearchRequestText;
	private Button	fSearchRequestBtn;
	private static TableViewer fViewer;
	private ReviewTableData fReviewItem = new ReviewTableData();
	private R4EGerritServerUtility fServerUtil = new R4EGerritServerUtility();
	private Map<TaskRepository, String> fMapRepoServer = null;

	private Action doubleClickAction;

	/**
	 * The constructor.
	 */
	public R4EGerritTableView() {
		super();
		rtv = this;
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * Method createPartControl. This is a callback that will allow us to create
	 * the viewer and initialize it.
	 * 
	 * @param parent
	 *            Composite
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(Composite)
	 */
	public void createPartControl(Composite aParent) {

		createSearchSection(aParent);
		UIReviewTable reviewTable = new UIReviewTable();
		fViewer = reviewTable.createTableViewerSection(aParent);
			
		// Setup the view layout
		createLayout(aParent);

		fViewer.setInput(fReviewItem.getReviews());
		Table table = fViewer.getTable();
		addListeners(table);
		
//		fViewer.setInput(getViewSite());
//
//		// Create the help context id for the viewer's control
//		PlatformUI
//				.getWorkbench()
//				.getHelpSystem()
//				.setHelp(viewer.getControl(),
//						"org.eclipse.mylyn.reviews.r4e-gerrit.ui.viewer");
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
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
//		gribDataGroup.minimumWidth = 260;
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


	/**********************************************************/
	/*                                                        */
	/* DEFAULT METHODS, EITHER MOVE OR DELETED EVENTUALLY     */
	/*                                                        */
	/**********************************************************/
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

//	private void showMessage(String message) {
//		MessageDialog.openInformation(fViewer.getControl().getShell(),
//				"R4E-Gerrit table", message);
//	}

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
	                R4EGerritTask[] reviewList = getReviewTasks(aTaskRepo, aQuery);
//	                R4EGerritTask[] reviewList = R4EGerritQueryUtils.getReviewList(aTaskRepo, aQuery);
	                final int numItems = reviewList.length;
	                R4EGerritPlugin.Ftracer.traceInfo("Number of review items: " + numItems);
	                fReviewItem.createReviewItem(reviewList, aQuery, aTaskRepo );
	                Display.getDefault().syncExec(new Runnable() {
	                    @Override
	                    public void run() {
	                        setSearchLabel (aQuery);
	                        setSearchText (aQuery);
	                        setRepositoryLabel (aTaskRepo.getRepositoryLabel());
	                        fViewer.setInput(fReviewItem.getReviews()); 
	                        fViewer.refresh();
//	                        if (numItems < 1) {
//	                            //Display a popup, we did not find any items to display
//	                            String msg = "Query ( " + aQuery + ") on " + aTaskRepo.getUrl();
//	                            String reason = "Return " + numItems  + " items.";
//	                            UIUtils.showErrorDialog(msg, reason);
//	                        }
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
//				fetchMissingReviewInfo();
				return status;
			}
		};
		job.setUser(true);
		job.schedule();
		
		return null;
	}

    /**
     * Perform the requested query and convert the resulting tasks in R4EGerritTask:s
     * 
     * @param repository the tasks repository
     * @param queryType	the query
     * 
     * @return a list of R4EGerritTask:s
     * @throws R4EQueryException
     */
    private R4EGerritTask[] getReviewTasks(TaskRepository repository, String queryType) throws R4EQueryException {

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
            TasksUiInternal.getTaskList().addQuery(query);
    	}
    	
        // Execute the query and wait for the job completion
        GerritConnector connector = GerritCorePlugin.getDefault().getConnector();
        try {
            Job job = TasksUiInternal.synchronizeQuery(connector, query, null, true);
			job.join();
		} catch (Exception e) {
		}

        // Convert the resulting tasks in R4EGerritTask:s
        Collection<ITask> tasks = query.getChildren();
        List<R4EGerritTask> reviews = new ArrayList<R4EGerritTask>();
        for (ITask task : tasks) {
        	try {
				TaskData taskData = connector.getTaskData(repository, task.getTaskId(), new NullProgressMonitor());
	            R4EGerritTask review = new R4EGerritTask(taskData);
	            if (review.getAttribute(R4EGerritTask.DATE_COMPLETION) == null) {
	                reviews.add(review);
	            }
			} catch (CoreException e) {
			}
        }
        return reviews.toArray(new R4EGerritTask[0]);
    }
	
//	/**
//	 * Reset the data in the table.
//	 * 
//	 */
//	private void resetData() {
//		//Reset the Search data
//		Display.getDefault().syncExec(new Runnable() {
//			
//			@Override
//			public void run() {
//				setSearchLabel("");
//				setSearchText ("");
//				setRepositoryLabel ("");
//				// Reset the review table
//				fReviewItem.createReviewItem(null, null, null);
//				fViewer.setInput(fReviewItem.getReviews());	
//				fViewer.refresh();
//				
//			}
//		});
//
//	}

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
	// Review data updates
    // ------------------------------------------------------------------------
    // Background:
	//
	// The general query returns the list of reviews with some but not all the
	// data required to fill a table entry - the remaining data is retrieved
	// from the server using detailed review queries, one per review.
	//
	// Since there can be a large number of reviews to fetch, it can be very
	// time consuming to retrieve ALL the detailed reviews. This is mitigated
	// by retrieving the details of the visible reviews only.
	//
	// However, this requires a little bit of plumbing, namely:
	//   - Identify the reviews to fetch (the visible ones)
	//   - Format and send the corresponding detailed review requests
	//   - Update the table as each request reply comes in
    // ------------------------------------------------------------------------
    
    /**
     * Add the listeners that trigger the review summary updates
     * 
     * @param table the table widget
     */
    private void addListeners(final Table table) {

//        // Key scrolling
//        table.addKeyListener(new KeyListener() {
//            @Override
//            public void keyReleased(KeyEvent e) {
//                trace("keyReleased");
////                fetchMissingReviewInfo();
//            }
//            @Override
//            public void keyPressed(KeyEvent e) {
//            }
//        });

//        // Mouse scrolling
//        table.addMouseWheelListener(new MouseWheelListener() {
//            @Override
//            public void mouseScrolled(MouseEvent e) {
//                trace("mouseScrolled");
////                fetchMissingReviewInfo();
//            }
//        });

//        // Scrollbar handling
//        ScrollBar scrollbar = table.getVerticalBar();
//        scrollbar.addSelectionListener(new SelectionListener() {
//            @Override
//            public void widgetSelected(SelectionEvent e) {
//                trace("ScrollBar event");
//                int top = table.getTopIndex();
//                System.out.println("top=" + top + ", detail=" + e.detail);
////                fetchMissingReviewInfo();
////                table.setTopIndex(top);
////                table.showItem(table.getItem(top + e.detail));
//            }
//            @Override
//            public void widgetDefaultSelected(SelectionEvent e) {
//            }
//        });

//        // Table resizing
//        table.addControlListener(new ControlListener() {
//            @Override
//            public void controlResized(ControlEvent e) {
//                trace("tableResized");
////                fetchMissingReviewInfo();
//            }
//            @Override
//            public void controlMoved(ControlEvent e) {
//            }
//        });

    }

//    /**
//     * Fetch and display the detailed data of visible reviews
//     */
//    private void updateVisibleReviews() {
//        List<String> reviewIds = getVisibleReviewIds();
//        for (String id : reviewIds) {
//            R4EGerritReviewData review = fReviewItem.getReview(id);
//            if (review != null && !review.hasDetails()) {
//                R4EGerritQueryUtils.getReviewDetails(fTaskRepository, review);
//                refresh();  // Refresh every time? Arguable.
//            }
//        }
//    }
    
//    /**
//     * Fetch and display the detailed data of the missing (visible) reviews
//     */
//    private Job job = null;
//    private void fetchMissingReviewInfo() {
//
//        trace("updateVisibleReviews");
//
//        if (job != null && job.getState() != Job.NONE && job.getThread() != null) {
//            trace("Killing job #" + job.getThread().getId());
//            job.cancel();
//        }
//
//        final List<String> reviewIds = new ArrayList<String>();
//        getVisibleReviewIds(reviewIds);
//
//        job = new Job("Fetch review data...") {
//            @Override
//            protected IStatus run(IProgressMonitor monitor) {
//                trace("Starting job");
//                for (String id : reviewIds) {
//                    if (monitor.isCanceled()) {
//                        trace("Canceling job");
//                        break;
//                    }
//                    R4EGerritReviewData review = fReviewItem.getReview(id);
//                    if (review != null && !review.hasDetails()) {
//                        trace("Get review details for: " + id);
//                        R4EGerritQueryUtils.getReviewDetails(fTaskRepository, review);
//                        review.hasDetails(true);
//                        refresh();  // More selective refresh?
//                    }
//                }
//                trace("Completing job");
//                return Status.OK_STATUS;
//            }
//        };
//        
//        trace("Scheduling job");
//        job.schedule();
//    }
//
//    
//    /**
//     * @return the list of review IDs that are currently visible
//     */
//    volatile Boolean semaphore = false;
//    private void getVisibleReviewIds(final List<String> reviewIds) {
//        trace("getVisibleReviewIds: enter");
//        boolean mySemaphore = false;
//        synchronized(semaphore) {
//            if (!semaphore) {
//                mySemaphore = semaphore = true;
//            }
//        }
//        if (mySemaphore) {
//            Display.getDefault().syncExec(new Runnable() {
//                @Override
//                public void run() {
//                    Table table = fViewer.getTable();
//                    int tableHeight = table.getClientArea().height;
//                    int headerHeight = table.getHeaderHeight();
//                    int itemHeight = table.getItemHeight();
//                    int nbReviewsVisible = (tableHeight - headerHeight) / itemHeight + 1;
//
//                    int topIndex = table.getTopIndex();
//                    int nbReviewsTotal = table.getItemCount();
//                    for (int i = 0; i < nbReviewsVisible; i++) {
//                        int index = topIndex + i;
//                        if (index < nbReviewsTotal) {
//                            TableItem item = table.getItem(index);
//                            if (item != null) {
//                                reviewIds.add(item.getText(1));
//                            }
//                        }
//                    }
//                }
//            });
//            semaphore = false;
//        }
//        trace("getVisibleReviewIds: exit");
//    }
//
//    /**
//     * Refresh the table content with the new data
//     */
//    private void refresh() {
//        Display.getDefault().syncExec(new Runnable() {
//            @Override
//            public void run() {
//                fViewer.refresh();
//            }
//        });
//    }
    
//    private void trace(String msg) {
//        System.out.println("[" + System.currentTimeMillis() + "] " + "T=" + Thread.currentThread().getId() + " " + msg);
//    }

}
