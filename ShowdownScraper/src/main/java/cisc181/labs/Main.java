package cisc181.labs;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;

import com.github.cliftonlabs.json_simple.Jsoner;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {
    public static void main(String[] args) throws IOException, JsonException {
        scrapeData();
        //getPokemonList();
        //getBattleItems();
        //getLegalMoves();
        //convertToTeams();
    }

    public static void convertToTeams() throws IOException, JsonException {
        ArrayList<String> fileNames = new ArrayList<>();
        File[] files = new File("src/main/java/cisc181/labs/battles/").listFiles();
        for(File file: files) {
            if (file.isFile()) {
                fileNames.add(file.getName());
            }
        }
        int f = 0;
        for(int i=f; i<f+100/*fileNames.size()*/; i++){
            File battleF = new File("src/main/java/cisc181/labs/battles/" + fileNames.get(i));
            if(battleF.exists()){
                InputStream is = new FileInputStream(battleF);
                JsonObject battleJson = (JsonObject) Jsoner.deserialize(IOUtils.toString(is, "UTF-8"));
                battleData currentBattle = new battleData(battleJson);
                teamsData currentTeams = new teamsData(currentBattle.id, currentBattle.p1, currentBattle.p2);
                parseLog(currentBattle, currentTeams);
            }
        }
    }

    public static void parseLog(battleData dataLog, teamsData teams) throws IOException, JsonException {
        String[] logHolder = dataLog.log.split("\n");
        ArrayList<String> logLines = new ArrayList<>(Arrays.asList(logHolder));
        ArrayList<String> paldeaDex =  paldeaFromJson();
        //removes chat from battle log, and gets battle winner
        ArrayList<String> abilityLines = new ArrayList<>();
        for(Iterator<String> it = logLines.iterator(); it.hasNext();){
            String holder = it.next();
            if(holder.contains("|c|") || holder.contains("|l|")){
                it.remove();
            }
            if(holder.startsWith("|win|")){
                teams.updateOutcome(holder.replace("|win|", "").trim());
            }
            if(holder.contains("ability") && holder.contains("from")){
                abilityLines.add(holder);
            }
        }
        for(int i=0; i<abilityLines.size(); i++){
            System.out.println(abilityLines.get(i));
        }
        //System.out.println(dataLog.log);
        //gets species for p1
        for(int i=0; i< logLines.size(); i++){
            if(logLines.get(i).contains("|poke|p1")){
                String pokeHolder = logLines.get(i).replace("|poke|p1|", "");
                pokeHolder = Array.get(pokeHolder.split(",", 0), 0).toString();
                if(paldeaDex.contains(pokeHolder)){
                    teams.p1.addPokemon(new PokemonInfo(pokeHolder));
                }else{
                    //filtering out forms of Pokemon, such as (Indeedee-F and replacing with base form Indeedee)
                    int j=0;
                    while(!pokeHolder.contains(paldeaDex.get(j))){
                        j++;
                    }
                    teams.p1.addPokemon(new PokemonInfo(paldeaDex.get(j)));
                }
            }
        }
        //gets brought team for p1
        for(int i=0; i<teams.p1.fullTeam.size(); i++){
            if(dataLog.log.contains("|p1a: " + teams.p1.fullTeam.get(i).species) || dataLog.log.contains("|p1b: " + teams.p1.fullTeam.get(i).species)){
                teams.p1.addBrought(teams.p1.fullTeam.get(i).species);
            }
        }
        //gathers the moves,items, teraType for p1 brought pokemon
        for(int i=0; i<teams.p1.broughtTeam.size(); i++){
            String currentSpecies = teams.p1.broughtTeam.get(i);
            PokemonInfo currentPokemon = new PokemonInfo("");
            for(int j=0; j<teams.p1.fullTeam.size(); j++){
                if(teams.p1.fullTeam.get(j).species == currentSpecies){
                    currentPokemon = teams.p1.fullTeam.get(j);
                }
            }
            for(int j=0; j<logLines.size(); j++){
                //gets known moves
                if(logLines.get(j).startsWith("|move|p1a: " + currentSpecies)){
                    String getMove = Arrays.asList(logLines.get(j).replace("|move|p1a: " + currentSpecies + "|", "").split("\\|")).get(0);
                    currentPokemon.addMove(getMove);
                }else if(logLines.get(j).startsWith("|move|p1b: " + currentSpecies)) {
                    String getMove = Arrays.asList(logLines.get(j).replace("|move|p1b: " + currentSpecies + "|", "").split("\\|")).get(0);
                    currentPokemon.addMove(getMove);
                }
                //gets teraType
                if(logLines.get(j).contains("|-terastallize|p1") && logLines.get(j).contains(currentSpecies)){
                    ArrayList<String> splitTera = new ArrayList<>(Arrays.asList(logLines.get(j).split("\\|")));
                    currentPokemon.addTera(splitTera.get(splitTera.size()-1));
                }
                //gets ability
                if(logLines.get(j).contains("ability")){
                    if(logLines.get(j).contains("|-ability|p1") && logLines.get(j).contains(currentSpecies)){
                        ArrayList<String> splitAbility = new ArrayList<>(Arrays.asList(logLines.get(j).split("\\|")));
                        currentPokemon.addAbility(splitAbility.get(3));
                    }else if(logLines.get(j).contains("|-activate|p1") && logLines.get(j).contains(currentSpecies)){
                        ArrayList<String> splitAbility = new ArrayList<>(Arrays.asList(logLines.get(j).split("\\|")));
                        currentPokemon.addAbility(splitAbility.get(splitAbility.size()-1).replace("ability: ", ""));
                    }else if(logLines.get(j).contains("[from] ability:") && logLines.get(j).contains("[of] p1") && logLines.contains(currentSpecies)){

                    }
                }
            }
        }
        //teams.printData();
    }

    public static ArrayList<String> paldeaFromJson() throws IOException, JsonException {
        File paldeaDex = new File("src/main/java/cisc181/labs/lists/PaldeaDex.json");
        ArrayList<String> paldeaPokemon = new ArrayList<>();
        if(paldeaDex.exists()) {
            InputStream is = new FileInputStream(paldeaDex);
            JsonArray battleJson = (JsonArray) Jsoner.deserialize(IOUtils.toString(is, "UTF-8"));
            for(int i=0; i<battleJson.size(); i++){
                paldeaPokemon.add(battleJson.get(i).toString());
            }
        }
        return paldeaPokemon;
    }

    public static void getLegalMoves() throws IOException {
        ArrayList<String> illegal = new ArrayList<>();
        ArrayList<String> legal = new ArrayList<>();
        Document illegalHolder = Jsoup.connect("https://www.serebii.net/scarletviolet/unusableattacks.shtml").get();
        Element illegalTable = illegalHolder.getElementsByClass("tab").get(0);
        Elements illegalCells = illegalTable.getElementsByClass("fooinfo");
        for(int i=0; i<illegalCells.size()/2; i++){
            illegal.add(illegalCells.get(i * 2).text().trim());
        }

        Document allMoves = Jsoup.connect("https://www.serebii.net/attackdex-sv/languages.shtml").get();
        Element allMoveTable = allMoves.getElementsByClass("dextable").get(0);
        Elements allMoveCells = allMoveTable.getElementsByClass("fooinfo");
        for(int i=0; i<allMoveCells.size()/9; i++){
            String checkMove = allMoveCells.get(i*9).text().trim();
            if(!illegal.contains(checkMove)){
                legal.add(checkMove);
            }
        }

        String writeFile ="src/main/java/cisc181/labs/lists/MovesList.json";
        FileWriter fw = new FileWriter(writeFile);
        fw.write("[\"" + legal.get(0) + "\"");
        for(int i=1; i<legal.size(); i++){
            fw.write(", \n\"" + legal.get(i) + "\"");
        }
        fw.write("]");
        fw.close();
    }

    public static void getBattleItems() throws IOException {
        ArrayList<String> itemList = new ArrayList<>();
        Document itemsHolder = Jsoup.connect("https://www.serebii.net/scarletviolet/items.shtml").get();
        Elements tables = itemsHolder.getElementsByClass("dextable");

        Elements cells = tables.get(5).getElementsByClass("fooinfo");
        for(int i=0; i<cells.size()/3; i++){
            itemList.add(cells.get(3 * i).text());
        }
        cells = tables.get(6).getElementsByClass("fooinfo");
        for(int i=0; i<cells.size()/3; i++){
            itemList.add(cells.get(3 * i).text());
        }

        String writeFile ="src/main/java/cisc181/labs/lists/BattleItems.json";
        FileWriter fw = new FileWriter(writeFile);
        fw.write("[\"" + "Adrenaline Orb" + "\"");
        for(int i=0; i<itemList.size(); i++){
            fw.write(", \n\"" + itemList.get(i) + "\"");
        }
        fw.write("]");
        fw.close();
    }

    public static void getPokemonList() throws IOException {
        Document dexHolder = Jsoup.connect("https://www.serebii.net/scarletviolet/paldeapokedex.shtml").get();
        Elements tables = dexHolder.getElementsByClass("tab");
        Element dexTable = tables.get(1);
        Elements cells = dexTable.getElementsByClass("fooinfo");
        ArrayList<String> pokemonList = new ArrayList<>();
        for(int i=0; i< 400; i++){
            pokemonList.add(cells.get((4 * i) + 2).text().replaceAll("[^a-zA-Z-é .]*", "").trim());
        }

        String writeFile ="src/main/java/cisc181/labs/lists/PaldeaDex.json";
        FileWriter fw = new FileWriter(writeFile);
        fw.write("[\"" + pokemonList.get(0) + "\"");
        for(int i=1; i<400; i++){
            fw.write(", \n\"" + pokemonList.get(i) + "\"");
        }
        fw.write("]");
        fw.close();
    }

    public static void scrapeData() throws JsonException, IOException {
        for(int i=1; i<=25; i++) {
            System.out.println("Page: " + i);
            ArrayList<BattleList> idsList = new ArrayList<>();
            Document battleHolder = Jsoup.connect("https://replay.pokemonshowdown.com/search.json?format=gen9vgc2023regulationc&page=" + i).ignoreContentType(true).get();
            String jsonList = battleHolder.body().text();
            JsonArray battleIds = (JsonArray) Jsoner.deserialize(jsonList);
            for (int j = 0; j < battleIds.size(); j++) {
                BattleList currentBattle = new BattleList((JsonObject) battleIds.get(j));
                idsList.add(currentBattle);
            }
            for(int j=0; j<idsList.size(); j++){
                System.out.println("Battle no.: " + j);
                String fileExtension = idsList.get(j).id + ".json";
                String fileName = "src/main/java/cisc181/labs/battles/" + fileExtension;
                Document fullBattle = Jsoup.connect("https://replay.pokemonshowdown.com/" + fileExtension).ignoreContentType(true).get();
                FileWriter fw = new FileWriter(fileName);
                fw.write(fullBattle.body().text());
                fw.close();
                System.out.println("Battle done");
                //Thread.sleep(500);
            }
            System.out.println("Page done");
        }
    }
}
