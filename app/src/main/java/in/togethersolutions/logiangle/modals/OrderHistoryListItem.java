package in.togethersolutions.logiangle.modals;

public class OrderHistoryListItem {
    private String orderNumber;
    private String clientName;
    private String clientMobileNumber;
    private String clientAddress;
    private String status;
    private String dateTime;
    private String taskType;
    private String pickUpDateTime;
    private String totalAmount;
    private String crmID;
    public OrderHistoryListItem(String orderNumber, String clientName, String clientMobileNumber, String clientAddress, String status, String dateTime, String taskType, String pickUpDateTime, String totalAmount, String crmID) {
        this.orderNumber = orderNumber;
        this.clientName = clientName;
        this.clientMobileNumber = clientMobileNumber;
        this.clientAddress = clientAddress;
        this.status = status;
        this.dateTime = dateTime;
        this.taskType = taskType;
        this.pickUpDateTime = pickUpDateTime;
        this.totalAmount = totalAmount;
        this.crmID = crmID;
    }
    public String getOrderNumber() {
        return orderNumber;
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientMobileNumber() {
        return clientMobileNumber;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public String getStatus() {
        return status;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getTaskType() {
        return taskType;
    }

    public String getPickUpDateTime() {
        return pickUpDateTime;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getCrmID() {
        return crmID;
    }

}
