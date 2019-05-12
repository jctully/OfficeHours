package tmb.csci412.wwu.officehours;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by tullyj2 on 4/28/19.
 */

public class ProfessorContent {

    public static final List<ProfItem> ITEMS = new ArrayList<>();
    public static final Map<String, ProfItem> ITEM_MAP = new HashMap<>();
    public static int lastProfId = 0;

    public ProfessorContent() {
    }

    public void addItem(ProfItem p) {
        ITEMS.add(p);
        ITEM_MAP.put(p.name, p);
    }

    public ProfItem createProfItem(String name, String office, String startTime, String endTime) {
        return new ProfItem(name, office, startTime, endTime);
    }

    public class ProfItem {
        private String name;
        private String office;
        private String startTime;
        private String endTime;
        private boolean online;

        public ProfItem(String name, String office, String startTime, String endTime) {
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

    }


}

