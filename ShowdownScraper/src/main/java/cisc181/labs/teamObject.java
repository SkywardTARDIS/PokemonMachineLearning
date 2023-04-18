package cisc181.labs;

import java.util.ArrayList;

public class teamObject {
    ArrayList<PokemonInfo> team;

    teamObject(){
        this.team = new ArrayList<>();
    }

    teamObject(PokemonInfo newPokemon){
        this.team = new ArrayList<>();
        this.team.add(newPokemon);
    }

    public void addPokemon(PokemonInfo newPokemon){
        if(this.team.size() < 6){
            this.team.add(newPokemon);
        }
    }
}
