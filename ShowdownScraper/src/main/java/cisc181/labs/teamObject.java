package cisc181.labs;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import java.util.ArrayList;
import java.util.Arrays;

public class teamObject {
    String playerName;
    ArrayList<PokemonInfo> fullTeam;
    ArrayList<String> broughtTeam;

    teamObject(String name) {
        this.playerName = name;
        this.fullTeam = new ArrayList<>();
        this.broughtTeam = new ArrayList<>();
    }

    teamObject(JsonObject teamJSON) throws JsonException {
        this.playerName = teamJSON.get(teamObjectFields.playerName.getKey()).toString();
        JsonArray team = (JsonArray) teamJSON.get(teamObjectFields.fullTeam.getKey());
        this.fullTeam = new ArrayList<>();
        for(int i=0; i<team.size(); i++){
            this.fullTeam.add(new PokemonInfo((JsonObject) team.get(i)));
        }
        this.broughtTeam = new ArrayList<>();
        ArrayList<String> broughtHolder = new ArrayList<>(Arrays.asList(teamJSON.get(teamObjectFields.broughtTeam.getKey()).toString().replace("[", "").replace("]", "").split(",")));;
        for(int i=0; i<broughtHolder.size(); i++){
            this.broughtTeam.add(broughtHolder.get(i));
        }
    }

    public void addPokemon(PokemonInfo newPokemon){
        if(this.fullTeam.size() < 6){
            this.fullTeam.add(newPokemon);
        }
    }

    public void addBrought(String broughtPokemon){
        if(!this.broughtTeam.contains(broughtPokemon) && this.broughtTeam.size() < 4){
            this.broughtTeam.add(broughtPokemon);
        }
    }

    public void printTeam(){
        System.out.println(this.playerName);
        for(int i=0; i<this.fullTeam.size(); i++){
            fullTeam.get(i).printPokemon();
            System.out.println("\n");
        }
        System.out.println(broughtTeam);
    }
    public String toJSON(){
        String teamHolder = "{\n\"playerName\": \"" + this.playerName + "\",\n\"fullTeam\": [\n";
        teamHolder = teamHolder + fullTeam.get(0).toJSON();
        for(int i=1; i< fullTeam.size(); i++){
            teamHolder = teamHolder + ",\n" + fullTeam.get(i).toJSON();
        }
        teamHolder = teamHolder + "\n],\n";
        teamHolder = teamHolder + "\"broughtTeam\": [";
        for(int i=0; i<this.broughtTeam.size(); i++){
            if(i != 0){
                teamHolder = teamHolder + ", ";
            }
            teamHolder = teamHolder + "\"" + this.broughtTeam.get(i) + "\"";
        }
        teamHolder = teamHolder + "]\n}";
        return teamHolder;
    }

}
