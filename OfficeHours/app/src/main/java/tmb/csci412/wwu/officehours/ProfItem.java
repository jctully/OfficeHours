package tmb.csci412.wwu.officehours;

import java.util.ArrayList;

/**
 * Created by tullyj2 on 5/16/19.
 */

public class ProfItem {
    private String name;
    private String building;
    private String dept;
    private String room;
    private String email;
    private String hours;
    private String picURL;
    private ArrayList<String> student_list;

    private boolean online;

    public ProfItem(String name, String building, String dept, String room, String email, String hours, String picURL, Boolean online,
                    ArrayList<String> student_list) {
        this.name = name;
        this.building = building;
        this.dept = dept;
        this.room = room;
        this.email = email;
        this.hours = hours;
        this.picURL = picURL;
        this.online = online;
        this.student_list = student_list;
    }

    public ProfItem() {

    }

    //getters
    public String getName() {
        return name;
    }

    public String getHours() {
        return hours;
    }

    public String getRoom() {
        return room;
    }

    public String getBuilding() {
        return building;
    }

    public String getPicURL() {
        return picURL;
    }

    public String getDept() {
        return dept;
    }

    public String getEmail() {
        return email;
    }

    public ArrayList<String> getStudent_list() {
        return student_list;
    }


    public boolean isOnline() {
        return online;
    }

    //setters
    public void setOnline() {
        this.online=true;
    }

    public void setOffline() {
        this.online=false;
    }

}
