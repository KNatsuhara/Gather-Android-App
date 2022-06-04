package com.example.Gather;

public class GroupModel {
    String groupName;


    public GroupModel(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void print() {
        System.out.println(groupName);
    }
}
