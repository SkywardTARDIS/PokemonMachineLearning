package cisc181.labs;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;

import java.math.BigDecimal;

public class teamsData {
    String id;
    teamObject p1;
    teamObject p2;
    int outcome; // 1 is p1 victory, 0 is p2 victory

    teamsData(String id, String p1, String p2){
        this.id = id;
        this.p1 = new teamObject(p1);
        this.p2 = new teamObject(p2);
        this.outcome = -1;
    }

    teamsData(JsonObject battleJSON) throws JsonException {
        this.id = battleJSON.get(teamsDataFields.id.getKey()).toString();
        teamObject holder = new teamObject((JsonObject) battleJSON.get(teamsDataFields.p1.getKey()));
        this.p1 = holder;
        holder = new teamObject((JsonObject) battleJSON.get(teamsDataFields.p2.getKey()));
        this.p2 = holder;
        this.outcome = Integer.valueOf(String.valueOf(battleJSON.get(teamsDataFields.outcome.getKey())));
    }

    public void updateOutcome(String winner){
        if(winner.equals(this.p1.playerName)){
            this.outcome = 1;
        }else if(winner.equals(this.p2.playerName)){
            this.outcome = 0;
        }
    }

    public void printData(){
        System.out.println(this.id);
        this.p1.printTeam();
        System.out.println("\n");
        this.p2.printTeam();
        System.out.println("\n");
        System.out.println(this.outcome);
    }

    public String toJSON(){
        String holder = "{\n\"id\": \"" + this.id + "\",\n\"p1\": ";
        holder = holder + this.p1.toJSON() + ",\n\"p2\": ";
        holder = holder + this.p2.toJSON() + ",\n\"outcome\": " + this.outcome + "\n}";
        return holder;
    }
}
