package cisc181.labs;

import com.github.cliftonlabs.json_simple.JsonObject;

public class BattleList {
    String uploadtime;
    String id;
    String format;
    String p1;
    String p2;

    BattleList(JsonObject battleInfo){
        this.uploadtime = battleInfo.get(listFields.uploadtime.getKey()).toString();
        this.id = battleInfo.get(listFields.id.getKey()).toString();
        this.format = battleInfo.get(listFields.format.getKey()).toString();
        this.p1 = battleInfo.get(listFields.p1.getKey()).toString().trim();
        this.p2 = battleInfo.get(listFields.p2.getKey()).toString().trim();
    }
}
