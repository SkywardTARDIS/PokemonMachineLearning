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
        //testPrint();
    }

    public static void testPrint(){
        String holder = "|-activate|p2b: Iron Bundle|ability: Quark Drive|[fromitem]";
        ArrayList<String> testing = new ArrayList<>(Arrays.asList(holder.split("\\|")));
        for(int i=0; i<testing.size(); i++){
            System.out.println(testing.get(i));
        }
        System.out.println(Arrays.asList(testing.get(3).split(":")).get(1).trim());
    }

    public static void getUnique(ArrayList<String> lines) throws IOException {
        ArrayList<ArrayList<String>> sorted = new ArrayList<>();
        ArrayList<String> prefix = new ArrayList<>();
        for(int i=0; i<lines.size(); i++){
            String prefixHolder = Arrays.asList(lines.get(i).split("\\|")).get(1);
            if(!prefix.contains(prefixHolder)){
                prefix.add(prefixHolder);
                sorted.add(new ArrayList<String>());
            }
            sorted.get(prefix.indexOf(prefixHolder)).add(lines.get(i));
        }
        for(int i=0; i<prefix.size(); i++){
            File abilityFile = new File("src/main/java/cisc181/labs/lists/ability" + prefix.get(i) + ".txt");
            FileWriter fw = new FileWriter(abilityFile);
            for(int j=0; j<sorted.get(i).size(); j++){
                fw.write(sorted.get(i).get(j) + "\n");
            }
            fw.close();
        }
    }

    public static void convertToTeams() throws IOException, JsonException {
        ArrayList<String> fileNames = new ArrayList<>();
        ArrayList<String> abilityLines = new ArrayList<>();
        File[] files = new File("src/main/java/cisc181/labs/battles/").listFiles();
        for(File file: files) {
            if (file.isFile()) {
                fileNames.add(file.getName());
            }
        }
        int f = fileNames.size();
        System.out.println(fileNames.size());
        for(int i=0; i<f; i++){
            File battleF = new File("src/main/java/cisc181/labs/battles/" + fileNames.get(i));
            if(battleF.exists()){
                InputStream is = new FileInputStream(battleF);
                JsonObject battleJson = (JsonObject) Jsoner.deserialize(IOUtils.toString(is, "UTF-8"));
                battleData currentBattle = new battleData(battleJson);
                teamsData currentTeams = new teamsData(currentBattle.id, currentBattle.p1, currentBattle.p2);
                parseLog(currentBattle, currentTeams, abilityLines);
            }
        }
        getUnique(abilityLines);
    }

    public static void parseLog(battleData dataLog, teamsData teams, ArrayList<String> abilityLines) throws IOException, JsonException {
        String[] logHolder = dataLog.log.split("\n");
        ArrayList<String> logLines = new ArrayList<>(Arrays.asList(logHolder));
        ArrayList<String> paldeaDex =  paldeaFromJson();
        //removes chat from battle log, and gets battle winner
        for(Iterator<String> it = logLines.iterator(); it.hasNext();){
            String holder = it.next();
            if(holder.contains("|c|") || holder.contains("|l|")){
                it.remove();
            }
            if(holder.startsWith("|win|")){
                teams.updateOutcome(holder.replace("|win|", "").trim());
            }
            if(holder.contains("ability")){
                abilityLines.add(holder);
            }
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
            for(int j=0; j<logLines.size(); j++) {
                if (logLines.get(j).contains("|switch|p1") && logLines.get(j).contains(teams.p1.fullTeam.get(i).species)) {
                    teams.p1.addBrought(teams.p1.fullTeam.get(i).species);
                }
            }
        }
        //gathers the nicknames, moves, items, teraType for p1 brought pokemon
        for(int i=0; i<teams.p1.broughtTeam.size(); i++) {
            String currentSpecies = teams.p1.broughtTeam.get(i);
            PokemonInfo currentPokemon = new PokemonInfo("");
            for (int j = 0; j < teams.p1.fullTeam.size(); j++) {
                if (teams.p1.fullTeam.get(j).species == currentSpecies) {
                    currentPokemon = teams.p1.fullTeam.get(j);
                }
            }
            //gets nicknames
            for (int j = 0; j < logLines.size(); j++) {
                if (logLines.get(j).contains("|switch|p1") && logLines.get(j).contains(currentSpecies)) {
                    //fix getting nicknames
                    String getNickname = Arrays.asList(logLines.get(j).split("\\|")).get(2);
                    getNickname = Arrays.asList(getNickname.split(":")).get(1).trim();
                    //System.out.println(logLines.get(j));
                    if (!currentPokemon.species.equals(getNickname)){
                        currentPokemon.setNickname(getNickname);
                        currentSpecies = currentPokemon.nickname;
                        //System.out.println(currentSpecies + " the " + currentPokemon.species);
                    }else{
                        currentPokemon.setNickname(currentPokemon.species);
                    }
                }
            }
            for (int j = 0; j < logLines.size(); j++) {
                //gets known moves
                if (logLines.get(j).startsWith("|move|p1a: " + currentSpecies)) {
                    String getMove = Arrays.asList(logLines.get(j).replace("|move|p1a: " + currentSpecies + "|", "").split("\\|")).get(0);
                    currentPokemon.addMove(getMove);
                } else if (logLines.get(j).startsWith("|move|p1b: " + currentSpecies) && !logLines.get(j).contains("[from]ability")) {
                    String getMove = Arrays.asList(logLines.get(j).replace("|move|p1b: " + currentSpecies + "|", "").split("\\|")).get(0);
                    currentPokemon.addMove(getMove);
                }
                //gets teraType
                if (logLines.get(j).contains("|-terastallize|p1") && logLines.get(j).contains(currentSpecies)) {
                    ArrayList<String> splitTera = new ArrayList<>(Arrays.asList(logLines.get(j).split("\\|")));
                    currentPokemon.addTera(splitTera.get(splitTera.size() - 1));
                }
                //gets abilities
                if (logLines.get(j).contains("ability") && logLines.get(j).contains(currentSpecies)) {
                    ArrayList<String> abilityLine = new ArrayList<>(Arrays.asList(logLines.get(j).split("\\|")));
                    String openClause = abilityLine.get(1);
                    switch (openClause) {
                        case "-ability":
                            if (logLines.get(j).contains("[from] ability") && abilityLine.get(2).contains("p1") && abilityLine.get(2).contains(currentSpecies)) {
                                currentPokemon.addAbility(Arrays.asList(abilityLine.get(4).split(":")).get(1).trim());
                            } else if (logLines.get(j).contains("[from] ability") && abilityLine.get(abilityLine.size() - 1).contains("[of] p1") && abilityLine.get(abilityLine.size() - 1).contains(currentSpecies)) {
                                currentPokemon.addAbility(abilityLine.get(3));
                            } else if (abilityLine.get(2).contains(currentSpecies) && abilityLine.get(2).contains("p1")) {
                                currentPokemon.addAbility(abilityLine.get(3));
                            }
                            break;
                        case "-activate":
                            if(logLines.get(j).contains(currentSpecies) && logLines.get(j).contains("p1")){
                                currentPokemon.addAbility(Arrays.asList(abilityLine.get(3).split(":")).get(1).trim());
                            }
                            break;
                        case "cant":
                            if(abilityLine.get(2).contains(currentSpecies) && abilityLine.get(2).contains("p1")){
                                currentPokemon.addAbility(Arrays.asList(abilityLine.get(3).split(":")).get(1).trim());
                            }
                            break;
                        case "-copyboost":
                            if(abilityLine.get(2).contains(currentSpecies) && abilityLine.get(2).contains("p1")){
                                currentPokemon.addAbility("Costar");
                            }
                            break;
                        case "-curestatus":
                            if(abilityLine.get(2).contains(currentSpecies) && abilityLine.get(2).contains("p1")){
                                currentPokemon.addAbility("Natural Cure");
                            }
                            break;
                        case "-damage":
                            if(abilityLine.get(abilityLine.size()-1).contains(currentSpecies) && abilityLine.get(abilityLine.size()-1).contains("p1")){
                                currentPokemon.addAbility(Arrays.asList(abilityLine.get(4).split(":")).get(1).trim());
                            }
                            break;
                        case "-fail":
                            if(abilityLine.get(2).contains("p1") && abilityLine.get(2).contains(currentSpecies)){
                                currentPokemon.addAbility(Arrays.asList(abilityLine.get(5).split(":")).get(1).trim());
                            }
                            break;
                        case "-fieldstart":
                            if(abilityLine.get(abilityLine.size()-1).contains(currentSpecies) && abilityLine.get(abilityLine.size()-1).contains("p1")){
                                currentPokemon.addAbility(Arrays.asList(abilityLine.get(3).split(":")).get(1).trim());
                            }
                            break;
                        case "-heal":
                            if(abilityLine.get(2).contains(currentSpecies) && abilityLine.get(2).contains("p1")){
                                currentPokemon.addAbility(Arrays.asList(abilityLine.get(4).split(":")).get(1).trim());
                            }
                            break;
                        case "-immune":
                            if(abilityLine.get(2).contains(currentSpecies) && abilityLine.get(2).contains("p1")){
                                currentPokemon.addAbility(Arrays.asList(abilityLine.get(abilityLine.size()-1).split(":")).get(1).trim());
                            }
                            break;
                        case "-item":
                            if(abilityLine.size() == 5){
                                if(abilityLine.get(2).contains(currentSpecies) && abilityLine.get(2).contains("p1")){
                                    currentPokemon.addAbility(Arrays.asList(abilityLine.get(4).split(":")).get(1).trim());
                                }
                            }else{
                                if(abilityLine.get(5).contains(currentSpecies) && abilityLine.get(5).contains("p1")){
                                    currentPokemon.addAbility(Arrays.asList(abilityLine.get(5).split(":")).get(1).trim());
                                }
                            }
                            break;
                        case "move":
                            if(abilityLine.get(2).contains(currentSpecies) && abilityLine.get(2).contains("p1")){
                                currentPokemon.addAbility(Arrays.asList(abilityLine.get(5).split(":")).get(1).trim());
                            }
                            break;
                        case "-setboost":
                            if(abilityLine.get(2).contains(currentSpecies) && abilityLine.get(2).contains("p1")){
                                currentPokemon.addAbility(Arrays.asList(abilityLine.get(abilityLine.size()-1).split(":")).get(1).trim());
                            }
                            break;
                        case "-start":
                            if(abilityLine.size() == 7){
                                if(abilityLine.get(6).contains(currentSpecies) && abilityLine.get(6).contains("p1")){
                                    currentPokemon.addAbility("Cursed Body");
                                }
                            }else if(abilityLine.get(2).contains(currentSpecies) && abilityLine.get(2).contains("p1")){
                                currentPokemon.addAbility(Arrays.asList(abilityLine.get(abilityLine.size()-1).split(":")).get(1).trim());
                            }
                            break;
                        case "-status":
                            if(abilityLine.get(abilityLine.size()-1).contains(currentSpecies) && abilityLine.get(abilityLine.size()-1).contains("p1")){
                                currentPokemon.addAbility(Arrays.asList(abilityLine.get(abilityLine.size()-2).split(":")).get(1).trim());
                            }
                            break;
                        case "-transform":
                            if(abilityLine.get(2).contains(currentSpecies) && abilityLine.get(2).contains("p1")){
                                currentPokemon.addAbility("Transform");
                            }
                            break;
                        case "-weather":
                            if(abilityLine.get(4).contains(currentSpecies) && abilityLine.get(4).contains("p1")){
                                currentPokemon.addAbility(Arrays.asList(abilityLine.get(3).split(":")).get(1).trim());
                            }
                            break;
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
        int prevFiles = new File("src/main/java/cisc181/labs/battles/").listFiles().length;
        int currentFiles;
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
            currentFiles = new File("src/main/java/cisc181/labs/battles/").listFiles().length;
            if(currentFiles == prevFiles){
                System.out.println("No more new battles");
                System.out.println(currentFiles + " battles in the data set");
                return;
            }
            prevFiles = currentFiles;
            System.out.println("Page done");
        }
    }
}
