package dementiev.plugin.equipment.action;

import com.atlassian.jira.ManagerFactory;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.issue.IssueFactory;
import com.atlassian.jira.issue.IssueInputParametersImpl;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.security.xsrf.RequiresXsrfCheck;
import com.atlassian.jira.util.JiraUrlCodec;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.atlassian.jira.web.action.issue.CreateIssue;
import com.atlassian.jira.web.action.issue.IssueCreationHelperBean;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.WorkflowException;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.util.TextUtils;
import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.ofbiz.core.entity.GenericValue;
import dementiev.plugin.equipment.Customer;
import dementiev.plugin.equipment.util.PropertySetUtil;
import dementiev.plugin.equipment.util.SqlUtils;
import webwork.action.ActionContext;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author dmitry dementiev
 */


public class MyCreateIssueDetails extends CreateIssue {
    private final IssueService issueService;
    protected IssueService.CreateValidationResult validationResult;

    //new fields
    private String customerOfEquipment;
    private String serialNumber;
    private String invNumber;

    public MyCreateIssueDetails(final IssueFactory issueFactory, final IssueCreationHelperBean issueCreationHelperBean,
                                final IssueService issueService) {
        super(issueFactory, issueCreationHelperBean);
        this.issueService = issueService;
    }

    /**
     * JRA-4791 - To allow direct links to populate the create issue details page without submiting the form
     */
    public String doInit() {
        if (isAbleToCreateIssueInSelectedProject()) {
            doValidation();
            if (invalidInput())
                return ERROR;
        }
        return INPUT;
    }

    protected void doValidation() {
        validationResult = issueService.validateCreate(getRemoteUser(), new IssueInputParametersImpl(ActionContext.getParameters()));
        this.issueObject = validationResult.getIssue();
        // We want to be able to repopulate the fields with their input values
        this.fieldValuesHolder = validationResult.getFieldValuesHolder();
        if (!validationResult.isValid()) {
            addErrorCollection(validationResult.getErrorCollection());
        }
    }

    @RequiresXsrfCheck
    protected String doExecute() throws Exception {
        try {
            createIssue();
            MutableIssue newIssue = getIssueObject();
            String customerOfEquipment = "";
            if (TextUtils.stringSet(getCustomerOfEquipment())) {
                int custId = Integer.parseInt(getCustomerOfEquipment());
                Customer customer = SqlUtils.getCustomerById(custId);
//                customerOfEquipment = URLDecoder.decode(getCustomerOfEquipment(), "UTF-8");
                customerOfEquipment = customer.getJiraGroupName();
            }
            String serialNumber = "";
            if (TextUtils.stringSet(getSerialNumber())) {
                serialNumber = URLDecoder.decode(getSerialNumber(), "UTF-8");
            }

            String invNumber = "";
            if (TextUtils.stringSet(getInvNumber())) {
                invNumber = URLDecoder.decode(getInvNumber(), "UTF-8");
            }

            PropertySet propertySet = PropertySetUtil.getPropertySet("Issue", newIssue.getId());
            if (TextUtils.stringSet(customerOfEquipment)) {//add propertyset customer to issue
                propertySet.setString("customerOfEquipment", customerOfEquipment);
            }
            if (TextUtils.stringSet(serialNumber)) {//add propertyset serialNumber to issue
                propertySet.setString("serialNumber", serialNumber);
            }
            if (TextUtils.stringSet(invNumber)) {//add propertyset invNumber to issue
                propertySet.setString("invNumber", invNumber);
            }
            if (hasAnyErrors()) {
                return ERROR;
            }
            return doPostCreationTasks();
        }
        catch (Exception e) {
            log.error(e, e);
            addErrorMessage((e.getMessage() != null ? e.getMessage() : ExceptionUtils.getFullStackTrace(e)));
            return ERROR;
        }
    }

    protected void createIssue() throws Exception {
        if (validationResult == null) {
            // There are alot of actions that use this as a base action and not all of them have been converted to
            // use the issue service, we need this for those that setup the issue themselves.
            validationResult = new IssueService.CreateValidationResult(getIssueObject(), new SimpleErrorCollection(), getFieldValuesHolder());
        }

        IssueService.IssueResult issueResult = issueService.create(getRemoteUser(), validationResult, getAuxiliarySubmitButtonValue());
        if (issueResult.isValid()) {
            setIssue(issueResult.getIssue().getGenericValue());
        } else {
            addErrorCollection(issueResult.getErrorCollection());
        }
    }

    protected String doPostCreationTasks() throws Exception {
        if (ManagerFactory.getPermissionManager().hasPermission(Permissions.BROWSE, getIssue(), getRemoteUser()))
            return getRedirect("/browse/" + JiraUrlCodec.encode(getIssue().getString("key")));
        else
            return getRedirect("CantBrowseCreatedIssue.jspa?issueKey=" + JiraUrlCodec.encode(getIssue().getString("key")));
    }

    public GenericValue getIssue() {
        GenericValue issue = super.getIssue();
        if (issue == null)
            throw new IllegalStateException("No issue has been created yet so it can not be retrieved");
        else
            return issue;
    }

    /**
     * The jira-workflow.xml Create action (id 1) can contain meta attributes indicating that auxiliary (alternative) 'Create'
     * buttons must be shown, to support alternative ways of submitting an issue (eg. shortcutting a step). For example:
     * <p/>
     * <meta name="jira.button.submit">createissue.shortcut.submit</meta>
     * <p/>
     * This method checks whether a Request param matching any meta name was found
     * (eg. 'foo.jsp?jira.button.submit=jira.button.submit', and if so, returns the name ('jira.button.submit').  This can
     * then be passed back to the workflow.
     * Returns 'null' if the regular submit button was pressed
     */
    protected String getAuxiliarySubmitButtonValue() throws WorkflowException {
        JiraWorkflow workflow = ManagerFactory.getWorkflowManager().getWorkflow(getPid(), getIssuetype());
        if (workflow == null)
            throw new WorkflowException("No workflow for pid " + getPid() + ", issuetype " + getIssuetype());
        final WorkflowDescriptor descriptor = workflow.getDescriptor();
        final List initialActions = descriptor.getInitialActions();
        if (initialActions.size() == 0) {
            throw new WorkflowException("There are no initial actions for workflow " + workflow + ", descriptor " + descriptor + " associated with pid " + getPid() + " and issue type " + getIssuetype());
        }
        Map buttons = ((ActionDescriptor) initialActions.get(0)).getMetaAttributes();
        final Iterator it = buttons.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            if (request.getParameter(key) != null) {
                return key;
            }
        }
        return null;
    }

    public Map<String, Object> getDisplayParams() {
        final Map<String, Object> displayParams = new HashMap<String, Object>();
        displayParams.put("theme", "aui");
        return displayParams;
    }

    public String getCustomerOfEquipment() {
        return customerOfEquipment;
    }

    public void setCustomerOfEquipment(String customerOfEquipment) {
        this.customerOfEquipment = customerOfEquipment;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getInvNumber() {
        return invNumber;
    }

    public void setInvNumber(String invNumber) {
        this.invNumber = invNumber;
    }
}
