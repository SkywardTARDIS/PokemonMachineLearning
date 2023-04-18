package cisc181.labs;

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

    public void updateOutcome(int outcome){
        this.outcome = outcome % 2;
    }
}
