package dementiev.plugin.equipment;

/**
 * @author dmitry dementiev
 */
public class Equipment {
    private int id;
    private String serial;
    private String inv;
    private Customer customer;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getInv() {
        return inv;
    }

    public void setInv(String inv) {
        this.inv = inv;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return "Equipment{" +
                "id=" + id +
                ", serial='" + serial + '\'' +
                ", inv='" + inv + '\'' +
                ", customer=" + customer +
                '}';
    }
}
