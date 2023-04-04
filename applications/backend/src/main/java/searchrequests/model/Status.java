package searchrequests.model;

public enum Status {
    // Note: reordering these would affect values in existing rows in db
    CREATED,
    INVITED,
    DECLINED,
}
