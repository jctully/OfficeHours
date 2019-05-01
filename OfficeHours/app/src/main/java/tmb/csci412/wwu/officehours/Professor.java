package tmb.csci412.wwu.officehours;

import java.util.ArrayList;


/**
 * Created by tullyj2 on 4/28/19.
 */

public class Professor {

    private String name;
    private String office;
    private String startTime;
    private String endTime;
    private boolean online;

    public Professor(String name, String office, String startTime, String endTime) {
        this.name = name;
        this.office = office;
        this.startTime = startTime;
        this.endTime = endTime;
        this.online = false;
    }

    //getters
    public String getName() {
        return name;
    }

    public boolean isOnline() {
        return online;
    }

    public String getOffice() {
        return office;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    //setters
    public void setOnline() {
        this.online=true;
    }

    public void setOffline() {
        this.online=false;
    }

    private static int lastProfId = 0;

    public static ArrayList<Professor> createProfList(int numProfs) {
        ArrayList<Professor> profs = new ArrayList<Professor>();

        for (int i = 1; i <= numProfs; i++) {
            profs.add(new Professor("Professor " + ++lastProfId, "Office",
                    "startTime", "endTime"));
        }

        return profs;
    }


}

