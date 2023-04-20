package cisc181.labs;

import java.util.ArrayList;

public class teamObject {
    String playerName;
    ArrayList<PokemonInfo> fullTeam;
    ArrayList<String> broughtTeam;

    teamObject(String name) {
        this.playerName = name;
        this.fullTeam = new ArrayList<>();
        this.broughtTeam = new ArrayList<>();
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
}
