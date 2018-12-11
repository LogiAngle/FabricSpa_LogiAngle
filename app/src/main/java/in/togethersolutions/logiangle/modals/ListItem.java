package in.togethersolutions.logiangle.modals;

public class ListItem {

    private String orderNumber;
    private String clientName;
    private String clientMobileNumber;
    private String status;
    private String crmID;
    private String taskTypeID;

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientMobileNumber() {
        return clientMobileNumber;
    }

    public String getStatus() {
        return status;
    }

    public String getCrmID() {
        return crmID;
    }

    public String getTaskTypeID() {
        return taskTypeID;
    }

    public ListItem(String orderNumber, String clientName, String clientMobileNumber, String status, String crmID, String taskTypeID) {

        this.orderNumber = orderNumber;
        this.clientName = clientName;
        this.clientMobileNumber = clientMobileNumber;
        this.status = status;
        this.crmID = crmID;
        this.taskTypeID = taskTypeID;
    }
}
