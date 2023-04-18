package cisc181.labs;

import com.github.cliftonlabs.json_simple.JsonObject;

public class battleData {
    String id;
    String p1;
    String p2;
    String log;

    battleData(JsonObject battleLog){
        this.id = battleLog.get(logFields.id.getKey()).toString();
        this.p1 = battleLog.get(logFields.p1.getKey()).toString();
        this.p2 = battleLog.get(logFields.p2.getKey()).toString();
        this.log = battleLog.get(logFields.log.getKey()).toString();
    }
}
