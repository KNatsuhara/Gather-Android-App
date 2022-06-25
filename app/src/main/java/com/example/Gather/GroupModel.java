package com.example.Gather;

public class GroupModel {
    String groupName;
    String viewStatus;

    public GroupModel(String groupName, String viewStatus) {
        this.groupName = groupName;
        this.viewStatus = viewStatus;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getViewStatus() { return viewStatus; }

    public void print() {
        System.out.println(groupName);
    }
}
