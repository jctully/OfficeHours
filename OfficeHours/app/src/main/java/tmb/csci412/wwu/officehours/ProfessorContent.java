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

    public static void addItem(ProfItem p) {
        ITEMS.add(p);
        ITEM_MAP.put(p.getName(), p);
    }

}

