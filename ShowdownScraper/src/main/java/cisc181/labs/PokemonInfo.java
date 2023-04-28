package cisc181.labs;

import com.github.cliftonlabs.json_simple.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;

public class PokemonInfo {
    String species;
    String nickname;
    String item;
    String ability;
    String teraType;
    ArrayList<String> moves;

    PokemonInfo(String species){
        this.species = species;
        this.nickname = "";
        this.item = "";
        this.ability = "";
        this.teraType = "";
        this.moves = new ArrayList<>();
    }

    PokemonInfo(JsonObject pokemonJSON){
        this.species = pokemonJSON.get(PokemonInfoFields.species.getKey()).toString();
        this.nickname = pokemonJSON.get(PokemonInfoFields.nickname.getKey()).toString();
        this.item = pokemonJSON.get(PokemonInfoFields.item.getKey()).toString();
        this.ability = pokemonJSON.get(PokemonInfoFields.ability.getKey()).toString();
        this.teraType = pokemonJSON.get(PokemonInfoFields.teraType.getKey()).toString();
        this.moves = new ArrayList<>();
        ArrayList<String> moveHolder = new ArrayList<String>(Arrays.asList(pokemonJSON.get(PokemonInfoFields.moves.getKey()).toString().replace("[", "").replace("]", "").split(",")));
        for(int i=0; i<moveHolder.size(); i++){
            this.moves.add(moveHolder.get(i));
        }
    }

    public void setNickname(String nickname){
        if(!nickname.equals(this.species) ){
            this.nickname = nickname;
        }else{
            this.nickname = this.species;
        }
    }

    public void addItem(String item){
        this.item = item;
    }

    public void addAbility(String ability){
        this.ability = ability;
    }

    public void addTera(String teraType){
        this.teraType = teraType;
    }

    public void addMove(String move){
        if(moves.size() < 4 && !moves.contains(move)){
            this.moves.add(move);
        }
    }

    public void printPokemon(){
        System.out.println("Species: " + this.species);
        System.out.println("Nickname: " + this.nickname);
        System.out.println("Item: " + this.item);
        System.out.println("Ability: " + this.ability);
        System.out.println("Tera Type: " + this.teraType);
        System.out.print("Moves: ");
        System.out.println(this.moves);
    }

    public String toJSON(){
        String holder = "{\n\"species\": \"" + this.species + "\",\n" + "\"nickname\": \"" + this.nickname.replace("\"", "") + "\",\n" +
        "\"item\": \"" + this.item + "\",\n" + "\"ability\": \"" + this.ability + "\",\n" +
                "\"teraType\": \"" + this.teraType + "\",\n" + "\"moves\": [";
        for(int i=0; i<this.moves.size(); i++){
            if(i != 0){
                holder = holder + ", ";
            }
            holder = holder + "\"" + this.moves.get(i) + "\"";
        }
        holder = holder + "]\n}";
        return holder;
    }

}
