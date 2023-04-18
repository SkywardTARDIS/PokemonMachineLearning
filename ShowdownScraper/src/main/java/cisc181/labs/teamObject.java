package cisc181.labs;

import java.util.ArrayList;

public class teamObject {
    String playerName;
    ArrayList<PokemonInfo> fullTeam;
    ArrayList<String> broughtTeam;

    teamObject(String name){
        this.playerName = name;
        this.fullTeam = new ArrayList<>();
    }

    teamObject(String name, PokemonInfo newPokemon){
        this.playerName = name;
        this.fullTeam = new ArrayList<>();
        this.fullTeam.add(newPokemon);
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
}
