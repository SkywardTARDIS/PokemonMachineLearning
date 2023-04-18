package cisc181.labs;

import java.util.ArrayList;

public class PokemonInfo {
    String species;
    String item;
    ArrayList<String> moves;

    PokemonInfo(String species){
        this.species = species;
        this.item = "";
        this.moves = new ArrayList<>();
    }

    public void addItem(String item){
        this.item = item;
    }

    public void addMove(String move){
        if(moves.size() < 4 && !moves.contains(move)){
            this.moves.add(move);
        }
    }

}
