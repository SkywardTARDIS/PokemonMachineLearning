package cisc181.labs;

import java.util.ArrayList;

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

}
