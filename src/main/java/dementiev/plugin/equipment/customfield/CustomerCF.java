package dementiev.plugin.equipment.customfield;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.SortableCustomField;
import com.atlassian.jira.issue.customfields.converters.StringConverterImpl;
import com.atlassian.jira.issue.customfields.impl.CalculatedCFType;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.seraph.auth.DefaultAuthenticator;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.user.User;
import com.opensymphony.util.TextUtils;
import org.apache.log4j.Logger;
import dementiev.plugin.equipment.Customer;
import dementiev.plugin.equipment.Equipment;
import dementiev.plugin.equipment.util.PropertySetUtil;
import dementiev.plugin.equipment.util.SqlUtils;
import webwork.action.ActionContext;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;


public class CustomerCF extends CalculatedCFType implements SortableCustomField {
    Logger log = Logger.getLogger(CustomerCF.class);

    protected User getUser() {
        return (User) ActionContext.getSession().get(DefaultAuthenticator.LOGGED_IN_KEY);
    }

    public Collection<Customer> getAllCustomers() throws ClassNotFoundException, SQLException {
        ArrayList<Customer> customerArrayList = SqlUtils.getAllCustomers();
        return customerArrayList;
    }

    public Collection<Equipment> getEquipmentOfGroup(int groupId) throws ClassNotFoundException, SQLException {
        ArrayList<Equipment> equipmentArrayList = SqlUtils.getEquipmentOfGroup(groupId);
        return equipmentArrayList;
    }

    public Collection<Equipment> getAllEquipments() throws ClassNotFoundException, SQLException {
        ArrayList<Equipment> equipmentArrayList = SqlUtils.getAllEquipments();
        return equipmentArrayList;
    }

       public int compare(final String customFieldObjectValue1, final String customFieldObjectValue2, final FieldConfig fieldConfig) {
        return customFieldObjectValue1.compareTo(customFieldObjectValue2);
    }

    public String getStringFromSingularObject(Object singularObject) {
        if (singularObject != null) {
            return singularObject.toString();
        }
        return null;
    }

    public Object getSingularObjectFromString(String value) throws FieldValidationException {
        assertObjectImplementsType(String.class, value);
        return StringConverterImpl.convertNullToEmpty((String) value);
    }

    public Object getValueFromIssue(CustomField field, Issue issue) {
        String result = "";
            PropertySet propertySet = null;
            propertySet = PropertySetUtil.getPropertySet("Issue", issue.getId());
            if (propertySet != null) {
                String customer = propertySet.getString("customerOfEquipment");
                if (TextUtils.stringSet(customer)) {
                    result = customer;
                }
        }
        return result;
    }
}