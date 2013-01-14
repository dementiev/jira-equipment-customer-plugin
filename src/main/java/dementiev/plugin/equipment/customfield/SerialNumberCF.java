package dementiev.plugin.equipment.customfield;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.SortableCustomField;
import com.atlassian.jira.issue.customfields.converters.StringConverterImpl;
import com.atlassian.jira.issue.customfields.impl.CalculatedCFType;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.user.User;
import com.opensymphony.util.TextUtils;
import org.apache.log4j.Logger;
import dementiev.plugin.equipment.util.PropertySetUtil;

/**
 * @author dmitry dementiev
 */

public class SerialNumberCF extends CalculatedCFType implements SortableCustomField {
    Logger log = Logger.getLogger(SerialNumberCF.class);

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
                String serial = propertySet.getString("serialNumber");
                if (TextUtils.stringSet(serial)) {
                    result = serial;
                }
        }
        return result;
    }
}