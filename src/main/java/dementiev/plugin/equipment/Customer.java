package dementiev.plugin.equipment;

/**
 *@author dmitry dementiev
 */
public class Customer {
    private int id;
    private String jiraGroupName;
    private int level;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJiraGroupName() {
        return jiraGroupName;
    }

    public void setJiraGroupName(String jiraGroupName) {
        this.jiraGroupName = jiraGroupName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", jiraGroupName='" + jiraGroupName + '\'' +
                ", level=" + level +
                '}';
    }
}
