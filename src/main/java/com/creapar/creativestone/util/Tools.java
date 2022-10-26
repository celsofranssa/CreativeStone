/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creapar.creativestone.util;

import com.creapar.creativestone.data.Dataset;
import com.creapar.creativestone.model.*;
import com.creapar.creativestone.novelty.BayesianNovelty;
import com.creapar.creativestone.novelty.Novelty;
import com.creapar.creativestone.predicate.*;
import com.creapar.creativestone.synergy.Synergy;
import com.creapar.creativestone.value.SynergyValueModel;
import com.creapar.creativestone.value.ValueModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.Variance;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Celso
 */
public class Tools {

    private static Gson gson =
            GsonUtils.getGson();

    /**
     * Retuns a Gson object
     *
     * @return
     */
    public static Gson getGson() {
        return GsonUtils.getGson();
    }


    /**
     * Get the synegy as a graph
     *
     * @return
     */
    public static Graph<Integer, Edge> getGraphSynergy() {
        Synergy synergy = new Synergy();
        return synergy.getGraphSynergy();
    }

    /**
     * Return the means of the Dataset of CardCollections
     */
    public static List<Mean> getMeans() {
        Type type = new TypeToken<List<Mean>>() {
        }.getType();
        return gson.fromJson(getReader("/stats/means.json"), type);
    }


    /**
     * Return the variances of the Dataset of CardCollections
     */
    public static List<Variance> getVariances() {
        Type type = new TypeToken<List<Variance>>() {
        }.getType();
        return gson.fromJson(getReader("/stats/variances.json"), type);
    }

    /**
     * Returns a Map card name to Card
     *
     * @return
     */
    public static HashMap<String, Card> getCardNameMap() {
        Type cardsNamesMapType = new TypeToken<HashMap<String, Card>>() {
        }.getType();
        return gson.fromJson(getReader("/cards/cardNameMap_v01.json"), cardsNamesMapType);
    }

    /**
     * Returns a Map card id to Card
     *
     * @return
     */
    public static HashMap<Integer, Card> getCardIdMap() {
        Type type = new TypeToken<HashMap<Integer, Card>>() {
        }.getType();

        return gson.fromJson(getReader("/cards/cardIdMap_v01.json"), type);
    }

    public static HashMap<String, List<Integer>> getCardNameToIds() {
        Type cardIdsType = new TypeToken<HashMap<String, List<Integer>>>() {
        }.getType();

        return gson.fromJson(getReader("/cards/cardNameToIds_v01.json"), cardIdsType);
    }

    /**
     * Return a BufferedReader
     *
     * @param fileDescription
     * @return
     */
    public static BufferedReader getReader(String fileDescription) {
        InputStream is = Tools.class.getResourceAsStream(fileDescription);
        return new BufferedReader(new InputStreamReader(is));
    }

    /**
     * Return a BufferedWriter
     *
     * @param fileDescription
     * @return
     */
    public static BufferedWriter getWriter(String fileDescription) {
        try {
            return new BufferedWriter(new FileWriter("src/main/resources" + fileDescription));
        } catch (IOException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }


    public static List<Integer> getCardset(String setName) {
        Type type = new TypeToken<List<Integer>>() {
        }.getType();
        return gson.fromJson(
                getReader("/cards/" + setName + "/" + setName + "Set_v02.json"),
                type);
    }

    public static List<Integer> getRecipe(String heroName, String recipeName) {
        Type type = new TypeToken<List<Integer>>() {
        }.getType();
        return gson.fromJson(
                getReader("/recipes/" + heroName + "/" + recipeName + ".json"),
                type);
    }

    public static List<Integer> getCardsIds(List<String> cardsNames) {
        Set<Integer> cardsIds = new HashSet<>();
        HashMap<String, List<Integer>> cardNameToIds = getCardNameToIds();
        for (String cardName : cardsNames
                ) {
            if (!cardNameToIds.containsKey(cardName))
                System.out.println(cardName);
            if (!cardsIds.add(cardNameToIds.get(cardName).get(0))) {
                cardsIds.add(cardNameToIds.get(cardName).get(1));
            }
        }
        return new ArrayList<>(cardsIds);

    }

    public static List<Integer> getCore(List<Integer> cardsIds) {
        List<Integer> core = new ArrayList<>();
        Integer trials = cardsIds.size();

        SynergyValueModel valueModel = new SynergyValueModel(getGraphSynergy());
        Double vi = valueModel.getValue(cardsIds);
        System.out.println("vi: " + vi);
        for (int i = 0; i < trials; i++) {
            Integer cardId = cardsIds.remove(0);
            Double vf = valueModel.getValue(cardsIds);
            cardsIds.add(cardId);
            if (vf < vi)
                core.add(cardId);
        }

        return core;
    }

    /**
     *
     */
    public static List<CardCollection> getCardCollections() {
        Type type = new TypeToken<ArrayList<CardCollection>>() {
        }.getType();
        List<CardCollection> cardCollections = gson.fromJson(getReader("/cardCollections/cardCollections.json"), type);
        return cardCollections;
    }

    /**
     * Calculates the novelty of a card collection
     *
     * @param cardsNames - list of card's names
     * @return - novelty of the card list
     */
    public static Double getNovelty(List<String> cardsNames) {

        HashMap<Integer, Card> cardIdMap = getCardIdMap();

        // parser to convert CardCollection to Instance
        Parser parser = new Parser(cardIdMap);

        Integer numberOfAttributes = parser.attributes.size() - 1;

        List<Mean> means = getMeans();
        List<Variance> variances = getVariances();
        Novelty novelty = new BayesianNovelty(means, variances, 0.005, numberOfAttributes, parser, cardIdMap);

        List<Integer> cardsIds = getCardsIds(cardsNames);
        return novelty.getNovelty(cardsIds);

    }

    /**
     * Calculates the novelty of a card collection
     *
     * @param cardsNames - list of card's names
     * @return - novelty of the card list
     */
    public static Double getValue(List<String> cardsNames) {

        Graph<Integer, Edge> synergyGraph = getGraphSynergy();
        ValueModel value = new SynergyValueModel(synergyGraph);
        List<Integer> cardsIds = getCardsIds(cardsNames);
        return value.getValue(cardsIds);

    }

    public static void updateMeansAndVariances() {

        // container to all card until 06/08/2015
        HashMap<String, Card> cardNameMap = getCardNameMap();
        List<CardCollection> cardsCollections = getCardCollections();
        Parser parser = new Parser(cardNameMap.values());
        System.out.println("updated " + parser.attributes.size());
        Dataset dataset = new Dataset(parser.attributes.size() - 1, 1);

        for (CardCollection cardsCollection : cardsCollections) {
            dataset.addInstance(parser.getInstance(cardsCollection, cardNameMap));

        }

        try (BufferedWriter writer = getWriter("/stats/means.json")) {
            gson.toJson(dataset.getMeans(), writer);

        } catch (IOException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }

        try (BufferedWriter writer2 = getWriter("/stats/variances.json")) {
            gson.toJson(dataset.getVariances(), writer2);

        } catch (IOException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    public static void upDateCardNameMap2() {
        Type cardNameToIdsType = new TypeToken<HashMap<String, List<Integer>>>() {
        }.getType();

        Type cardMapType1 = new TypeToken<HashMap<String, Card>>() {
        }.getType();

        Type cardMapType2 = new TypeToken<HashMap<Integer, Card>>() {
        }.getType();

        HashMap<String, List<Integer>> cardNameToIds = gson.fromJson(getReader("/cards/cardNameToIds_v01.json"), cardNameToIdsType);
        HashMap<String, Card> cardMap1 = gson.fromJson(getReader("/cards/allCardsWithAbilities.json"), cardMapType1);
        HashMap<Integer, Card> cardMap2 = gson.fromJson(getReader("/cards/cardIdMap_v01.json"), cardMapType2);

        for (String cardName : cardMap1.keySet()) {
            Integer cardId = cardNameToIds.get(cardName).get(0);
            ArrayList<CardAbility> abilities = cardMap2.get(cardId).getAbilities();
            cardMap1.get(cardName).setAbilities(abilities);
        }

        try (BufferedWriter writer = getWriter("/cards/cardNameMap_v01.json")) {
            gson.toJson(cardMap1, writer);
        } catch (IOException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void upDateCardNameMap() {

        HashMap<Integer, Card> cardIdMap = getCardIdMap();
        HashMap<String, Card> cardNameMap = new HashMap<>();

        for (Integer cardId : cardIdMap.keySet()
                ) {
            Card card = cardIdMap.get(cardId);
            String cardName = card.getName();
            if (!cardNameMap.containsKey(cardName)) {
                cardNameMap.put(cardName, card);
            }
        }

        try (BufferedWriter writer = getWriter("/cards/cardNameMap_v01.json")) {
            gson.toJson(cardNameMap, writer);
        } catch (IOException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static Graph<String, Edge> getGraphSynergy2() {

        Gson gson = new Gson();

        Type synergiesType = new TypeToken<HashMap<String, List<String>>>() {
        }.getType();

        Type cardsParingType = new TypeToken<HashMap<String, Double>>() {
        }.getType();

        HashMap<String, List<String>> synergies = gson.fromJson(getReader("/synergySets/synergy_v01.json"), synergiesType);
        HashMap<String, Double> cardsParing = gson.fromJson(getReader("/cardsParings/cardsParings_v01.json"), cardsParingType);

        Graph<String, Edge> graph = new DirectedSparseMultigraph<>();

        for (String cardName : synergies.keySet()) {
            graph.addVertex(cardName);
        }

        for (String vName : synergies.keySet()) {
            for (String uName : synergies.get(vName)) {
                Double weight = cardsParing.get(vName + " # " + uName);
                if (weight == null)
                    weight = 1.0;
                Edge edge = new Edge(graph.getEdgeCount() + 1, weight);
                graph.addEdge(edge, vName, uName, EdgeType.DIRECTED);

            }

        }

        return graph;
    }

    /**
     *
     */
    public static void see(String searchedAbility) {
        HashMap<Integer, Card> cardsIds = getCardIdMap();
        Set<String> targets = new HashSet<>();

        for (Integer cardId : cardsIds.keySet()) {
            Card card = cardsIds.get(cardId);
            for (CardAbility ability : card.getAbilities()) {
                if (ability.getAbility().equals(searchedAbility)) {
                    System.out.println(card.getName() + " " + card.getType() + " :: " + ability.getAbility() + "-> " + ability.getTarget());
                    targets.add(ability.getTarget());
                }

            }
        }


        for (String target : targets) {
            System.out.println(target);
        }

    }

    /**
     * Returns a Map card id to Card
     *
     * @return
     */
    public static List<CardPair> getCardPairs() {
        Type type = new TypeToken<List<CardPair>>() {
        }.getType();
        RuntimeTypeAdapterFactory<CardPredicate> predAdapter = RuntimeTypeAdapterFactory.of(CardPredicate.class)
                .registerSubtype(AbilityPredicate.class)
                .registerSubtype(TypePredicate.class)
                .registerSubtype(RacePredicate.class)
                .registerSubtype(RarityPredicate.class)
                .registerSubtype(MinCostPredicate.class);
        GsonUtils.registerType(predAdapter);

        return GsonUtils.getGson().fromJson(getReader("/cardPairs/cardPairs_v01.json"), type);
    }

    /**
     * Read json of allset of cards and create a map of all cards which key is
     * the card's name.
     */
    public void readCardFromJson() {
        // json interface to read and write json objects
        Gson gson = new Gson();

        // type to read - map which has set name as key maped to a list
        // of card
        Type cardsType = new TypeToken<HashMap<String, ArrayList<Card>>>() {
        }.getType();

        // container to all card until 06/08/2015
        HashMap<String, ArrayList<Card>> jsonCards = gson.fromJson(getReader("/cards/allCardsBySets.json"), cardsType);

        // map a card's name to the card
        HashMap<String, Card> cards = new HashMap<>();
        for (String key : jsonCards.keySet()) {
            ArrayList<Card> cardsBySet = jsonCards.get(key);
            for (Card card : cardsBySet) {
                if (card.isCollectible()) { // only collectible cards
                    cards.put(card.getName(), card);
                    System.out.println(card.getName());
                }
            }
        }

        // write map of card in json file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("cards/allCollectibleCards_v01.json"))) {
            gson.toJson(cards, writer);
        } catch (IOException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void saveCardAbilities(String inputFile, String outputFile) {
        try {
            // reader an writer
            BufferedReader cardsReader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter cardsWriter = new BufferedWriter(new FileWriter(outputFile));

            // json interface to read and write json objects
            Gson gson = new Gson();

            // type to read - map which has set name as key maped to a list
            // of card
            Type cardsType = new TypeToken<HashMap<String, Card>>() {
            }.getType();

            // container to all card until 06/08/2015
            HashMap<String, Card> jsonCards = gson.fromJson(cardsReader, cardsType);

            // map a card's name to the card
            HashMap<String, ArrayList<CardAbility>> cardsAndAbilities = new HashMap<>();
            Card card;

            for (String key : jsonCards.keySet()) {
                card = jsonCards.get(key);
                cardsAndAbilities.put(card.getName(), card.getAbilities());
            }

            // write map of card in json file
            gson.toJson(cardsAndAbilities, cardsWriter);

            cardsReader.close();
            cardsWriter.close();

        } catch (IOException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void putAbilitiesInCards(String cardsAbilityFile, String cardsFile, String outputFile) {
        try {
            // reader an writer
            BufferedReader cardsAbilityReader = new BufferedReader(new FileReader(cardsAbilityFile));
            BufferedReader cardsReader = new BufferedReader(new FileReader(cardsFile));
            BufferedWriter cardsWriter = new BufferedWriter(new FileWriter(outputFile));

            // json interface to read and write json objects
            Gson gson = new Gson();

            // type to read - map which has set name as key maped to a list
            // of card
            Type cardsAbilityType = new TypeToken<HashMap<String, ArrayList<CardAbility>>>() {
            }.getType();

            // type to read - map which has set name as key maped to a list
            // of card
            Type cardsType = new TypeToken<HashMap<String, Card>>() {
            }.getType();

            // container to all card until 18/09/2015
            HashMap<String, Card> cards = gson.fromJson(cardsReader, cardsType);
            HashMap<String, ArrayList<CardAbility>> cardsAbilities = gson.fromJson(cardsAbilityReader, cardsAbilityType);

            for (String cardName : cards.keySet()) {
                cards.get(cardName).setAbilities(cardsAbilities.get(cardName));

            }

            // write map of card in json file
            gson.toJson(cards, cardsWriter);

            cardsReader.close();
            cardsWriter.close();

        } catch (IOException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void dataset(String cardsFile, String cardsCollectionFile, String datasetFile) {
        try {
            // reader and writer
            BufferedReader cardsReader = new BufferedReader(new FileReader(cardsFile));
            BufferedReader cardsCollectionReader = new BufferedReader(new FileReader(cardsCollectionFile));

            // json interface to read and write json objects
            Gson gson = new Gson();

            // type to read
            Type cardsType = new TypeToken<HashMap<Integer, Card>>() {
            }.getType();

            // type to read
            Type cardsCollectionType = new TypeToken<ArrayList<CardCollection>>() {
            }.getType();

            // container to all card until 06/08/2015
            HashMap<Integer, Card> cards = gson.fromJson(cardsReader, cardsType);
            ArrayList<CardCollection> cardsCollections = gson.fromJson(cardsCollectionReader, cardsCollectionType);

            Parser parser = new Parser(cards);

//            System.out.println("atributes ::: "+parser.attributes.size());
//
//            List<Map.Entry<String, Integer>> attributesEntry = parser.getAttributesEntryAbilityOrdened();
//            for (Map.Entry<String, Integer> entry : attributesEntry) {
//                System.out.println(entry.toString());
//            }
            Dataset dataset = new Dataset(parser.attributes.size() - 1, 1);

            for (CardCollection cardsCollection : cardsCollections) {
                if (cardsCollection.getWins() > 0) {
                    //dataset.addInstance(parser.getInstance(cardsCollection, cards));
                }

            }

            System.out.println("Number of Instances: " + dataset.getNumberOfInstances());

            dataset.toFile(datasetFile);
            // write map of card in json file
            cardsReader.close();
            cardsCollectionReader.close();

        } catch (IOException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Creates id to cards
     */
    public void createCardId() {

        // type of cards in json - is a Hash which key is the card's name
        // and the value is a complete card
        Type cardsType = new TypeToken<HashMap<String, Card>>() {
        }.getType();

        // Gson to manager jsson file
        Gson gson = new Gson();

        //all cards - Card's Name --> Card itself
        Map<String, Card> cards = gson.fromJson(getReader("/cards/allCardsWithAbilities.json"), cardsType);
        // all cards - Card's Id --> Card
        Map<Integer, Card> cardsIntegerMap = new HashMap<>();

        Card card;
        Integer index = 1;
        for (String key : cards.keySet()) {
            card = cards.get(key);
            if (!card.getRarity().equals("Legendary")) {
                cardsIntegerMap.put(index++, card);
                cardsIntegerMap.put(index++, card);
            } else {
                cardsIntegerMap.put(index++, card);
            }
        }

        //write in file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("cards/cardset_v01.json"))) {
            gson.toJson(cardsIntegerMap, writer);
        } catch (IOException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void cardNameToId() {
        // type of cards in json - is a Hash which key is the card's name
        // and the value is a complete card
        Type cardsType = new TypeToken<HashMap<Integer, Card>>() {
        }.getType();

        // Gson to manager jsson file
        Gson gson = new Gson();

        //all cards - Card's Name --> Card itself
        Map<Integer, Card> cards = gson.fromJson(getReader("/cards/cardset_v01.json"), cardsType);
        // all cards - Card's Id --> Card
        Map<String, List<Integer>> cardNameToIds = new HashMap<>();

        String cardName;

        for (Integer key : cards.keySet()) {
            cardName = cards.get(key).getName();

            if (cardNameToIds.containsKey(cardName)) {
                cardNameToIds.get(cardName).add(key);
            } else {
                List<Integer> l = new ArrayList<>();
                l.add(key);
                cardNameToIds.put(cardName, l);
            }

        }

        //write in file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("cards/cardNameToIds_v01.json"))) {
            gson.toJson(cardNameToIds, writer);
        } catch (IOException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Filter all cards by each hero
     *
     * @param heroName
     */
    public void filterCardsByHero(String heroName) {

        String fileDescription = "/cards/" + heroName.toLowerCase() + "/" + heroName.toLowerCase() + "Set_v02.json";
        Type cardsType = new TypeToken<HashMap<Integer, Card>>() {
        }.getType();

        // Gson to manager json file
        Gson gson = new Gson();

        Map<Integer, Card> cards = gson.fromJson(getReader("/cards/cardset_v01.json"), cardsType);

        List<Integer> cardsIds = new ArrayList<>();
        Card card;

        // put only specific hero cards
        for (Integer key : cards.keySet()) {
            card = cards.get(key);
            if (card.getPlayerClass() == null) {
                cardsIds.add(key);
                if (card.getRarity().equals("Legendary")) {
                    cardsIds.add(key);
                }
            }
        }

        try (BufferedWriter writer = getWriter(fileDescription)) {
            System.out.println(heroName + " " + cardsIds.size());
            gson.toJson(cardsIds, writer);
        } catch (IOException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Redefine the set of cards for each hero
     *
     * @param toReadFileDescription  - ids of cards for each hero
     * @param toReadFileDescription2 - cards map
     * @param toWriteFileDescription - new ids
     */
    public void doubleChance(String toReadFileDescription,
                             String toReadFileDescription2,
                             String toWriteFileDescription) {
        BufferedReader reader;
        BufferedReader reader2;
        BufferedWriter writer;
        try {
            // read json which hava cards (all cards already have ability)
            reader = new BufferedReader(new FileReader(toReadFileDescription));
            reader2 = new BufferedReader(new FileReader(toReadFileDescription2));
            writer = new BufferedWriter(new FileWriter(toWriteFileDescription));

            // type of cards in json - is a Hash which key is the card's name
            // and the value is a complete card
            Type cardsType = new TypeToken<HashMap<Integer, Card>>() {
            }.getType();

            Type cardsType2 = new TypeToken<List<Integer>>() {
            }.getType();

            // Gson to manager jsson file
            Gson gson = new Gson();

            //
            Map<Integer, Card> cards = gson.fromJson(reader, cardsType);
            List<Integer> cardsSet = gson.fromJson(reader2, cardsType2);

            // list of possible cards to this test
            List<Integer> cardsIds = new ArrayList<>();
            Card card;

            for (Integer cardId : cardsSet) {
                if (cards.get(cardId).getRarity().equals("Legendary")) {
                    cardsIds.add(cardId);
                    cardsIds.add(cardId);
                } else {
                    cardsIds.add(cardId);
                }

            }

            //write in file
            gson.toJson(cardsIds, writer);
            reader.close();
            writer.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void createSynergy(String toReadFileDescription, String toWriteFileDescription) {
        BufferedReader reader;
        BufferedWriter writer;

        try {
            // reader and writer json
            reader = new BufferedReader(new FileReader(toReadFileDescription));
            writer = new BufferedWriter(new FileWriter(toWriteFileDescription));

            // type of cards in json - is a Hash which key is the card's name
            // and the value is a complete card
            Type cardsType = new TypeToken<HashMap<String, Card>>() {
            }.getType();
            // Gson to manager jsson file
            Gson gson = new Gson();

            Map<String, Card> cardsNames = gson.fromJson(reader, cardsType);

            Map<String, List<String>> synergySets = new HashMap<>();

            ArrayList<String> relatedTo;
            for (String cardName : cardsNames.keySet()) {
                relatedTo = new ArrayList<>();
                relatedTo.add("");
                synergySets.put(cardName, relatedTo);

            }

            gson.toJson(synergySets, writer);
            reader.close();
            writer.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * @param toReadFileDescription1
     * @param toReadFileDescription2
     * @param toWriteFileDescription
     */
    public void defineSinergyList(String toReadFileDescription1,
                                  String toReadFileDescription2,
                                  String toWriteFileDescription
    ) {
        BufferedReader reader1;
        BufferedReader reader2;
        BufferedWriter writer;

        try {
            // reader and writer json
            reader1 = new BufferedReader(new FileReader(toReadFileDescription1));
            reader2 = new BufferedReader(new FileReader(toReadFileDescription2));
            writer = new BufferedWriter(new FileWriter(toWriteFileDescription));

            // types
            Type type1 = new TypeToken<HashMap<String, List<String>>>() {
            }.getType();
            Type type2 = new TypeToken<HashMap<String, List<Integer>>>() {
            }.getType();

            // Gson to manager jsson file
            Gson gson = new Gson();

            // containers
            Map<String, List<String>> synergyListNames = gson.fromJson(reader1, type1); //synegy.json
            Map<String, List<Integer>> cardsNamesToIds = gson.fromJson(reader2, type2); // map card name to ids
            Map<Integer, List<Integer>> synergies = new HashMap<>(); // synergies by ids

            for (String key : synergyListNames.keySet()) {
                List<String> synergyCards = synergyListNames.get(key);

                // ids from the key card
                List<Integer> keyIds = cardsNamesToIds.get(key);

                List<Integer> r = new ArrayList<>();
                for (String cardName : synergyCards) {
                    List<Integer> gotIds = cardsNamesToIds.get(cardName);
                    if (gotIds != null) {
                        r.addAll(cardsNamesToIds.get(cardName));
                    }

                }

                for (Integer kk : keyIds) {
                    synergies.put(kk, r);
                }
            }

            gson.toJson(synergies, writer);
            reader1.close();
            reader2.close();
            writer.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cardNamesToIds(String toReadFileDescription,
                               String toWriteFileDescription) {

        BufferedReader reader;
        BufferedWriter writer;

        try {
            // reader and writer json
            reader = new BufferedReader(new FileReader(toReadFileDescription));
            writer = new BufferedWriter(new FileWriter(toWriteFileDescription));

            // type
            Type cardsIdsType = new TypeToken<HashMap<Integer, Card>>() {
            }.getType();

            // Gson to manager jsson file
            Gson gson = new Gson();

            // containers
            Map<Integer, Card> cardsId = gson.fromJson(reader, cardsIdsType);
            Map<String, List<Integer>> cardsNamesToIds = new HashMap<>();
            Card card;
            String cardName;
            List<Integer> ids;

            for (Integer key : cardsId.keySet()) {
                cardName = cardsId.get(key).getName();
                if (cardsNamesToIds.containsKey(cardName)) {
                    cardsNamesToIds.get(cardName).add(key);
                } else {
                    ids = new ArrayList<>();
                    ids.add(key);
                    cardsNamesToIds.put(cardName, ids);
                }
            }

            gson.toJson(cardsNamesToIds, writer);
            reader.close();
            writer.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void createResultStats() {
        // to input files
        Gson gson = new Gson();
        Type resultsType = new TypeToken<List<Result>>() {
        }.getType();

        // all Heros
        List<String> heroes = Arrays.asList("druid", "hunter", "mage",
                "paladin", "priest", "rogue", "shaman", "warlock", "warrior");

        String inFileDescription, outFileDescription, stat;

        for (String heroName : heroes) {
            stat = "";
            inFileDescription = "results/" + heroName + "/" + heroName + "SimulatedResults.json";
            outFileDescription = "resultStats/" + heroName + "Stats.dat";
            try (BufferedReader reader = new BufferedReader(new FileReader(inFileDescription))) {
                List<Result> results = gson.fromJson(reader, resultsType);
                for (Result result : results) {
                    stat += result.getCreativity() + "\t" + result.getWinRate() + "\n";
                }
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFileDescription))) {
                    writer.write(stat);
                }
            } catch (IOException ex) {
                Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void setTotalMana() {
        // to input files
        Gson gson = new Gson();
        Type resultsType = new TypeToken<List<Result>>() {
        }.getType();

        Type cardsType = new TypeToken<HashMap<String, Card>>() {
        }.getType();

        HashMap<String, Card> cards = gson.fromJson(getReader("/cards/allCollectibleCards.json"), cardsType);

        int totalMana;

        // all Heros
        List<String> heroes = Arrays.asList("druid", "hunter", "mage",
                "paladin", "priest", "rogue", "shaman", "warlock", "warrior");

        String inFileDescription, outFileDescription;

        for (String heroName : heroes) {
            for (int candidateSize = 2; candidateSize < 13; candidateSize++) {
                inFileDescription = "resultsPaper/" + heroName + "/" + heroName + "_" + candidateSize + "_CardsCombo_Results.json";
                outFileDescription = "combosPaper/" + heroName + "/" + heroName + "_" + candidateSize + "_CardsCombo_Results.json";
                try (BufferedReader reader = new BufferedReader(new FileReader(inFileDescription))) {
                    List<Result> results = gson.fromJson(reader, resultsType);

                    for (Result result : results) {
                        totalMana = 0;
                        totalMana = result.getCardsNames().stream().map((cardName) -> cards.get(cardName).getCost()).reduce(totalMana, Integer::sum);
                        result.setTotalManaCost(1.0 * totalMana);
                    }
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFileDescription))) {
                        gson.toJson(results, writer);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }

    }

    /**
     * tirar cartas impossiveis
     */
    public void redefineSynergy() {
        Gson gson = new Gson();
        Type synergyType = new TypeToken<HashMap<String, List<String>>>() {
        }.getType();
        Type cardsType = new TypeToken<HashMap<String, Card>>() {
        }.getType();

        HashMap<String, List<String>> synergies = gson.fromJson(getReader("/synergySets/synergySource.json"), synergyType);
        HashMap<String, Card> cards = gson.fromJson(getReader("/cards/allCollectibleCards_v01.json"), cardsType);
        String kpc, vpc;
        List<String> sinergyCardName;
        for (String key : synergies.keySet()) {
            System.out.println("String key: " + key);
            kpc = cards.get(key).getPlayerClass();
            sinergyCardName = synergies.get(key);
            List<String> nSynergies = new ArrayList<>();
            for (String cardName : sinergyCardName) {
                System.out.println("cardName:" + cardName);
                if (cards.containsKey(cardName)) {
                    vpc = cards.get(cardName).getPlayerClass();
                    if (kpc == null || vpc == null || vpc.equals(kpc)) {
                        nSynergies.add(cardName);
                    }
                }
            }
            synergies.put(key, nSynergies);

        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("synergySets/synergy_v01.json"))) {
            gson.toJson(synergies, writer);
        } catch (IOException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Create cardsParing
     */
    public void cardsParings() {
        Gson gson = new Gson();

        Type cardsType = new TypeToken<HashMap<String, Card>>() {
        }.getType();
        HashMap<String, Card> cards = gson.fromJson(getReader("/cards/allCollectibleCards_v01.json"), cardsType);

        Map<String, Double> cardsParings = new HashMap<>();
        String cardsParing;

        for (String cardName1 : cards.keySet()) {
            for (String cardName2 : cards.keySet()) {
                cardsParing = cardName1 + " # " + cardName2;
                if (!cardsParings.containsKey(cardsParing)) {
                    cardsParings.put(cardsParing, 0.0);
                }
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("cardsParings/cardsParings.json"))) {
            gson.toJson(cardsParings, writer);
        } catch (IOException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void putWeight() {
        Gson gson = new Gson();

        Type cardCollectionType = new TypeToken<List<CardCollection>>() {
        }.getType();

        Type relationsType = new TypeToken<Map<String, Double>>() {
        }.getType();

        List<CardCollection> cardCollections = gson.fromJson(getReader("/cardCollections/cardCollections_v02.json"), cardCollectionType);
        Map<String, Double> cardsParings = gson.fromJson(getReader("/cardsParings/cardsParings.json"), relationsType);

        String card1;
        String card2;
        String[] splited;
        Integer c1, c2;

        int index = 0;
        for (CardCollection collection : cardCollections) {
            System.out.println("cardCollectin: " + (++index));

            for (String cardsParing : cardsParings.keySet()) {
                splited = cardsParing.split(" # ");
                card1 = splited[0];
                card2 = splited[1];
                c1 = collection.cardCount(card1);
                c2 = collection.cardCount(card2);

                if (card1.equals(card2)) {
                    if (c1 > 1) {
                        cardsParings.put(cardsParing, cardsParings.getOrDefault(cardsParing, 0.0) + 1);
                    }
                } else if (c1 > 0 && c2 > 0) {
                    cardsParings.put(cardsParing, cardsParings.getOrDefault(cardsParing, 0.0) + 1);
                }
            }

        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("cardsParings/cardsParings_v02.json"))) {
            gson.toJson(cardsParings, writer);
        } catch (IOException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cardCollection() {
        Gson gson = new Gson();

        Type deckType = new TypeToken<List<DeckJson>>() {
        }.getType();

        List<DeckJson> decks = gson.fromJson(getReader("/cardCollections/decksOTK.json"), deckType);

        CardCollection collection;
        List<CardCollection> collections = new ArrayList<>();
        String cardName;
        Integer count;
        for (DeckJson deck : decks) {
            collection = new CardCollection();
            collection.setHero(deck.hero);

            for (CardJson cardJson : deck.getCards()) {
                cardName = cardJson.getName();
                count = Integer.parseInt(cardJson.getQuantity());
                for (int i = 0; i < count; i++) {
                    collection.add(cardName);
                }
            }
            collections.add(collection);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("cardCollections/cardCollections_v01.json"))) {
            gson.toJson(collections, writer);
        } catch (IOException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return
     */
    public Graph<Integer, Edge> getIdsGraphs() {
        Gson gson = new Gson();

        Type cardIdsType = new TypeToken<HashMap<String, List<Integer>>>() {
        }.getType();

        Graph<String, Edge> graphNames = getGraphSynergy2();
        HashMap<String, List<Integer>> cardIds = gson.fromJson(getReader("/cards/cardNameToIds_v01.json"), cardIdsType);
        Graph<Integer, Edge> graphId = new DirectedSparseMultigraph<>();

        //add vertex. total of verteces = 1065
//        for (int i = 1; i <= 1065; i++) {
//            graphId.addVertex(i);
//        }

        Integer edgesCount = 1;
        Double weight;
        for (String uName : graphNames.getVertices()) {
            List<Integer> uIds = cardIds.get(uName);
            addVertex(graphId, uIds);
            for (String vName : graphNames.getSuccessors(uName)) {
                weight = graphNames.findEdge(uName, vName).getWeight();
                List<Integer> vIds = cardIds.get(vName);
                addVertex(graphId, vIds);
                for (Integer uId : uIds) {
                    for (Integer vId : vIds) {
                        if (!uId.equals(vId)) {
                            graphId.addEdge(new Edge(edgesCount++, weight), uId, vId);
                        }
                    }
                }
            }
        }
        return graphId;
    }

    public void addVertex(Graph<Integer, Edge> graphId, List<Integer> vertices) {
        for (Integer vertex : vertices
                ) {
            if (!graphId.containsVertex(vertex))
                graphId.addVertex(vertex);

        }
    }

    /**
     * Normalized ids graph synergy
     *
     * @return
     */
    public Graph<Integer, Edge> getNormalizedIdsGraphs() {
        Graph<Integer, Edge> idsGraphs = getIdsGraphs();

        for (Integer u : idsGraphs.getVertices()) {
            Double minWeight = minWeight(idsGraphs, u, idsGraphs.getSuccessors(u));
            Double maxWeight = maxWeight(idsGraphs, u, idsGraphs.getSuccessors(u));

            for (Integer v : idsGraphs.getSuccessors(u)) {
                Double weight = idsGraphs.findEdge(u, v).getWeight();
                Double nWeight = norm(weight, minWeight, maxWeight);
                idsGraphs.findEdge(u, v).setWeight(nWeight);
            }

        }

        System.out.println(idsGraphs.toString());

        return idsGraphs;
    }

    private Double minWeight(Graph<Integer, Edge> idsGraphs, Integer u, Collection<Integer> successors) {
        Double min = Double.MAX_VALUE;
        for (Integer successor : successors) {
            Double weight = idsGraphs.findEdge(u, successor).getWeight();
            if (weight < min) {
                min = weight;
            }
        }

        return min;
    }

    private Double maxWeight(Graph<Integer, Edge> idsGraphs, Integer u, Collection<Integer> successors) {
        Double max = Double.MIN_VALUE;
        for (Integer successor : successors) {
            Double weight = idsGraphs.findEdge(u, successor).getWeight();
            if (weight > max) {
                max = weight;
            }
        }

        return max;
    }

    private Double norm(Double weight, Double minWeight, Double maxWeight) {
        return 9.0 * ((weight - minWeight) / (maxWeight - minWeight + 0.0000001)) + 1;
    }

//    public List<Integer> getCardsIds(List<String> cardsNames) {
//
//        List<Integer> cardsIds = new ArrayList<>();
//
//        // types
//        Type cardsNameToIdsType = new TypeToken<HashMap<String, List<Integer>>>() {
//        }.getType();
//
//        // all cards - Card's Id --> Card
//        Map<String, List<Integer>> cardNameToIds = gson.fromJson(getReader("/cards/cardNameToIds_v01.json"), cardsNameToIdsType);
//
//        for (String cardName : cardsNames
//                ) {
//            //if(cardNameToIds.containsKey(cardName))
//            System.out.println(cardName);
//            cardsIds.add(cardNameToIds.get(cardName).get(0));
//        }
//
//        // cardsIds.addAll(cardsNames.stream().map(cardName -> cardNameToIds.get(cardName).get(0)).collect(Collectors.toList()));
//
//        return cardsIds;
//    }

    public void updateTarget() {


        Type cardsetType = new TypeToken<HashMap<Integer, Card>>() {
        }.getType();

        HashMap<Integer, Card> cardsIds = gson.fromJson(getReader("/cards/cardIdMap_v01.json"), cardsetType);

        Set<String> targets = new HashSet<>();

        for (Integer cardId : cardsIds.keySet()) {
            Card card = cardsIds.get(cardId);
            for (CardAbility ability : card.getAbilities()) {
                if (ability.getAbility().equals("secret")) {
                    // String target = ability.getTarget();
                    // if(target.equals("ownHand")||target.equals("ownDeck"))
                    ability.setTarget("self");
                    System.out.println(card.getName() + " " + card.getType() + " :: " + ability.getAbility() + "-> " + ability.getTarget());
                    targets.add(ability.getTarget());
                }

            }
        }

        try (BufferedWriter writer = getWriter("/cards/cardIdMap_v01.json")) {
            gson.toJson(cardsIds, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String target : targets) {
            System.out.println(target);
        }

    }

    public List<Integer> fiterCards(List<Integer> cardset, String predicateName) {
        Gson gson = new Gson();
        Type cardsetType = new TypeToken<HashMap<Integer, Card>>() {
        }.getType();

        Type predicateType = new TypeToken<Predicate<Card>>() {
        }.getType();

        Predicate<Card> predicate = gson.fromJson(getReader("/predicates/" + predicateName + ".json"), predicateType);
        HashMap<Integer, Card> cardsIds = gson.fromJson(getReader("/cards/cardIdMap_v01.json"), cardsetType);

        List<Integer> filteredCards = new ArrayList<>();
        for (Integer cardId : cardset) {
            Card card = cardsIds.get(cardId);
            if (predicate.test(card)) {
                filteredCards.add(cardId);
                System.out.println("" + card.getName());
            }

        }
        return filteredCards;
    }

    public void getRDC(Combo combo) {

        // containers
        HashMap<Integer, Card> cardIdMap = getCardIdMap();

        // parser to convert CardCollection to Instance
        Parser parser = new Parser(cardIdMap);
        Integer numberOfAttributes = parser.attributes.size() - 1;

        List<Mean> means = getMeans();

        List<Variance> variances = getVariances();

        Graph<Integer, Edge> synergyGraph = Tools.getGraphSynergy();


        Novelty novelty = new BayesianNovelty(means, variances, 0.005, numberOfAttributes, parser, cardIdMap);
        ValueModel value = new SynergyValueModel(synergyGraph);

        List<Integer> cardsIds = getCardsIds(combo.getCardsNames());

        combo.setRdcNovelty(novelty.getNovelty(cardsIds));
        combo.setRdcValue(value.getValue(cardsIds));

    }

    /**
     * get the combos
     */
    public void getCombos() {
        // types
        Type type = new TypeToken<List<JsonElement>>() {
        }.getType();
        Type type2 = new TypeToken<HashMap<String, Double>>() {
        }.getType();

        List<JsonElement> elements = gson.fromJson(getReader("/combos.json"), type);
        Map<String, Double> answer = gson.fromJson(getReader("/answers.json"), type2);

        Map<Integer, Combo> combosMap = new HashMap<>();


        for (JsonElement element : elements
                ) {
            Integer id = element.getId_combo();
            if (combosMap.containsKey(id)) {
                Combo combo = combosMap.get(id);
                Double novelty = answer.get(element.getQuestao_um());
                Double value = answer.get(element.getQuestao_dois());
                combo.addAssessment(new ComboAssessment(novelty, value));


            } else {
                Combo combo = new Combo();
                combo.setId(id);
                combo.setHeroName(element.getHero());
                if (element.isHuman() == 1) {
                    combo.setMadeByHuman(true);
                } else {
                    combo.setMadeByHuman(false);
                }
                String[] splited = element.getCardList().split(",");
                for (String cardName : splited
                        ) {
                    combo.addCard(cardName);
                }
                Double novelty = answer.get(element.getQuestao_um());
                Double value = answer.get(element.getQuestao_dois());
                combo.addAssessment(new ComboAssessment(novelty, value));
                combosMap.put(id, combo);
            }
        }
        for (Combo combo : combosMap.values()
                ) {
            getRDC(combo);
            combo.setHumanNovelty(combo.getHumanNovelty());
            combo.setHumanValue(combo.getHumanValue());
        }
        for (Combo combo : combosMap.values()) {
            System.out.println(combo.toString());
        }

        try (BufferedWriter writer = getWriter("/formatedCombos.json")) {
            gson.toJson(combosMap, writer);
        } catch (IOException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * @return
     */
    public Graph<Integer, Edge> getIdsUnitaryGraphs() {

        Type cardIdsType = new TypeToken<HashMap<String, List<Integer>>>() {
        }.getType();

        Graph<String, Edge> graphNames = getGraphSynergy2();
        HashMap<String, List<Integer>> cardIds = gson.fromJson(getReader("/cards/cardNameToIds_v01.json"), cardIdsType);
        Graph<Integer, Edge> graphId = new DirectedSparseMultigraph<>();


        Integer edgesCount = 1;

        for (String uName : graphNames.getVertices()) {
            List<Integer> uIds = cardIds.get(uName);
            addVertex(graphId, uIds);
            for (String vName : graphNames.getSuccessors(uName)) {
                List<Integer> vIds = cardIds.get(vName);
                addVertex(graphId, vIds);
                for (Integer uId : uIds) {
                    for (Integer vId : vIds) {
                        if (!uId.equals(vId)) {
                            graphId.addEdge(new Edge(edgesCount++, 1.0), uId, vId);
                        }
                    }
                }
            }
        }
        return graphId;
    }

    /**
     * @return
     */
    public Graph<Integer, Edge> getWeithedGraphs() {

        Graph<String, Edge> graphNames = getGraphSynergy2();
        HashMap<String, List<Integer>> cardIds = getCardNameToIds();
        Graph<Integer, Edge> graphId = new DirectedSparseMultigraph<>();

        Integer edgesCount = 1;
        Double weight;
        List<CardPair> cardPairs = getCardsPairs();
        for (String uName : graphNames.getVertices()) {
            List<Integer> uIds = cardIds.get(uName);
            addVertex(graphId, uIds);
            for (String vName : graphNames.getSuccessors(uName)) {
                List<Integer> vIds = cardIds.get(vName);
                addVertex(graphId, vIds);
                for (Integer uId : uIds) {
                    for (Integer vId : vIds) {
                        if (!uId.equals(vId)) {
                            weight = getWeight(uId, vId, cardPairs);
                            graphId.addEdge(new Edge(edgesCount++, weight), uId, vId);
                        }
                    }
                }
            }
        }
        return graphId;
    }

    private Double getWeight(Integer uId, Integer vId, List<CardPair> cardPairs) {
        Double weight = 1.0;
        Map<String, Card> cardNameMap = Tools.getCardNameMap();
        Card c1 = cardNameMap.get(uId);
        Card c2 = cardNameMap.get(vId);
        for (CardPair cardpair : cardPairs
                ) {
            if (cardpair.isAPair(c1, c2))
                weight++;
        }
        return weight;
    }

    public List<CardPair> getCardsPairs() {
        List<CardPair> cardPairs = new ArrayList<>();
        // types
        cardPairs.add(new CardPair(new TypePredicate("Minion"), new TypePredicate("Spell")));
        cardPairs.add(new CardPair(new TypePredicate("Minion"), new TypePredicate("Weapon")));
        cardPairs.add(new CardPair(new TypePredicate("Weapon"), new TypePredicate("Spell")));

        // class
        cardPairs.add(new CardPair(new ClassPredicate("Druid"), new ClassPredicate("Druid")));
        cardPairs.add(new CardPair(new ClassPredicate("Hunter"), new ClassPredicate("Hunter")));
        cardPairs.add(new CardPair(new ClassPredicate("Mage"), new ClassPredicate("Mage")));
        cardPairs.add(new CardPair(new ClassPredicate("Paladin"), new ClassPredicate("Paladin")));
        cardPairs.add(new CardPair(new ClassPredicate("Priest"), new ClassPredicate("Priest")));
        cardPairs.add(new CardPair(new ClassPredicate("Rogue"), new ClassPredicate("Rogue")));
        cardPairs.add(new CardPair(new ClassPredicate("Shaman"), new ClassPredicate("Shaman")));
        cardPairs.add(new CardPair(new ClassPredicate("Warlock"), new ClassPredicate("Warlock")));
        cardPairs.add(new CardPair(new ClassPredicate("Warrior"), new ClassPredicate("Warrior")));

        // ability
        // plusSpellDamage and spellDamage
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("plusSpellDamage", "alliedSpellDamage")),
                new AbilityPredicate(new CardAbility("spellDamage", "enemyHero"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("plusSpellDamage", "alliedSpellDamage")),
                new AbilityPredicate(new CardAbility("spellDamage", "allCharacter"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("plusSpellDamage", "alliedSpellDamage")),
                new AbilityPredicate(new CardAbility("spellDamage", "character"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("plusSpellDamage", "alliedSpellDamage")),
                new AbilityPredicate(new CardAbility("spellDamage", "splitAllEnemyMinion"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("plusSpellDamage", "alliedSpellDamage")),
                new AbilityPredicate(new CardAbility("spellDamage", "allEnemyCharacter"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("plusSpellDamage", "alliedSpellDamage")),
                new AbilityPredicate(new CardAbility("spellDamage", "enemyMinion"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("plusSpellDamage", "alliedSpellDamage")),
                new AbilityPredicate(new CardAbility("spellDamage", "enemyCharacter"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("plusSpellDamage", "alliedSpellDamage")),
                new AbilityPredicate(new CardAbility("spellDamage", "allMinion"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("plusSpellDamage", "alliedSpellDamage")),
                new AbilityPredicate(new CardAbility("spellDamage", "splitAllEnemy"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("plusSpellDamage", "alliedSpellDamage")),
                new AbilityPredicate(new CardAbility("spellDamage", "nextMinions"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("plusSpellDamage", "alliedSpellDamage")),
                new AbilityPredicate(new CardAbility("spellDamage", "minion"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("plusSpellDamage", "alliedSpellDamage")),
                new AbilityPredicate(new CardAbility("spellDamage", "allEnemyMinions"))));

        // dealDamage and enrage
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("enrage", "self")),
                new AbilityPredicate(new CardAbility("spellDamage", "character"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("enrage", "self")),
                new AbilityPredicate(new CardAbility("spellDamage", "allMinion"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("enrage", "self")),
                new AbilityPredicate(new CardAbility("spellDamage", "nextMinions"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("enrage", "self")),
                new AbilityPredicate(new CardAbility("spellDamage", "allCharacter"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("enrage", "self")),
                new AbilityPredicate(new CardAbility("spellDamage", "minion"))));

        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("enrage", "self")),
                new AbilityPredicate(new CardAbility("dealDamage", "alliedMinion"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("enrage", "self")),
                new AbilityPredicate(new CardAbility("dealDamage", "nextMinions"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("enrage", "self")),
                new AbilityPredicate(new CardAbility("dealDamage", "minion"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("enrage", "self")),
                new AbilityPredicate(new CardAbility("dealDamage", "splitAll"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("enrage", "self")),
                new AbilityPredicate(new CardAbility("dealDamage", "character"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("enrage", "self")),
                new AbilityPredicate(new CardAbility("dealDamage", "allMinion"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("enrage", "self")),
                new AbilityPredicate(new CardAbility("dealDamage", "allCharacter"))));

        // taunt and giveDinine Shield
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("taunt", "self")),
                new AbilityPredicate(new CardAbility("giveDivineShield", "alliedMinion"))));

        // taunt and plusHealth
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("taunt", "self")),
                new AbilityPredicate(new CardAbility("plusHealth", "pirates"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("taunt", "self")),
                new AbilityPredicate(new CardAbility("plusHealth", "alliedMinion"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("taunt", "self")),
                new AbilityPredicate(new CardAbility("plusHealth", "beast"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("taunt", "self")),
                new AbilityPredicate(new CardAbility("plusHealth", "allAlliedMinions"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("taunt", "self")),
                new AbilityPredicate(new CardAbility("plusHealth", "nextMinions"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("taunt", "self")),
                new AbilityPredicate(new CardAbility("plusHealth", "minion"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("taunt", "self")),
                new AbilityPredicate(new CardAbility("plusHealth", "mech"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("taunt", "self")),
                new AbilityPredicate(new CardAbility("plusHealth", "demons"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("taunt", "self")),
                new AbilityPredicate(new CardAbility("plusHealth", "character"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("taunt", "self")),
                new AbilityPredicate(new CardAbility("plusHealth", "alliedCharacter"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("taunt", "self")),
                new AbilityPredicate(new CardAbility("plusHealth", "allMinion"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("taunt", "self")),
                new AbilityPredicate(new CardAbility("plusHealth", "murlocs"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("taunt", "self")),
                new AbilityPredicate(new CardAbility("plusHealth", "demon"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("taunt", "self")),
                new AbilityPredicate(new CardAbility("plusHealth", "allAlliedCharacter"))));

        //charge and plusAttack
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("charge", "self")),
                new AbilityPredicate(new CardAbility("plusAttack", "pirates"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("charge", "self")),
                new AbilityPredicate(new CardAbility("plusAttack", "alliedMinion"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("charge", "self")),
                new AbilityPredicate(new CardAbility("plusAttack", "beast"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("charge", "self")),
                new AbilityPredicate(new CardAbility("plusAttack", "allAlliedMinions"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("charge", "self")),
                new AbilityPredicate(new CardAbility("plusAttack", "beasts"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("charge", "self")),
                new AbilityPredicate(new CardAbility("plusAttack", "nextMinions"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("charge", "self")),
                new AbilityPredicate(new CardAbility("plusAttack", "minion"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("charge", "self")),
                new AbilityPredicate(new CardAbility("plusAttack", "mech"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("charge", "self")),
                new AbilityPredicate(new CardAbility("plusAttack", "demons"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("charge", "self")),
                new AbilityPredicate(new CardAbility("plusAttack", "alliedCharacter"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("charge", "self")),
                new AbilityPredicate(new CardAbility("plusAttack", "mechs"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("charge", "self")),
                new AbilityPredicate(new CardAbility("plusAttack", "murlocs"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("charge", "self")),
                new AbilityPredicate(new CardAbility("plusAttack", "demon"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("charge", "self")),
                new AbilityPredicate(new CardAbility("plusAttack", "allAlliedCharacter"))));

        //charge and return to hand
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("charge", "self")),
                new AbilityPredicate(new CardAbility("returnToHand", "alliedMinion"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("charge", "self")),
                new AbilityPredicate(new CardAbility("returnToHand", "allAlliedMinions"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("charge", "self")),
                new AbilityPredicate(new CardAbility("returnToHand", "minion"))));

        //windfury and plusAttack
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("windfury", "self")),
                new AbilityPredicate(new CardAbility("plusAttack", "pirates"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("windfury", "self")),
                new AbilityPredicate(new CardAbility("plusAttack", "alliedMinion"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("windfury", "self")),
                new AbilityPredicate(new CardAbility("plusAttack", "beast"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("windfury", "self")),
                new AbilityPredicate(new CardAbility("plusAttack", "allAlliedMinions"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("windfury", "self")),
                new AbilityPredicate(new CardAbility("plusAttack", "beasts"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("windfury", "self")),
                new AbilityPredicate(new CardAbility("plusAttack", "nextMinions"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("windfury", "self")),
                new AbilityPredicate(new CardAbility("plusAttack", "minion"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("windfury", "self")),
                new AbilityPredicate(new CardAbility("plusAttack", "mech"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("windfury", "self")),
                new AbilityPredicate(new CardAbility("plusAttack", "demons"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("windfury", "self")),
                new AbilityPredicate(new CardAbility("plusAttack", "alliedCharacter"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("windfury", "self")),
                new AbilityPredicate(new CardAbility("plusAttack", "mechs"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("windfury", "self")),
                new AbilityPredicate(new CardAbility("plusAttack", "murlocs"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("windfury", "self")),
                new AbilityPredicate(new CardAbility("plusAttack", "demon"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("windfury", "self")),
                new AbilityPredicate(new CardAbility("plusAttack", "allAlliedCharacter"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("windfury", "self")),
                new AbilityPredicate(new CardAbility("plusAttack", "ownWeapon"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("windfury", "self")),
                new AbilityPredicate(new CardAbility("plusAttack", "alliedHero"))));

        //give windfury and plusAttack
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("giveWindfury", "alliedMinion")),
                new AbilityPredicate(new CardAbility("plusAttack", "pirates"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("giveWindfury", "alliedMinion")),
                new AbilityPredicate(new CardAbility("plusAttack", "alliedMinion"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("giveWindfury", "alliedMinion")),
                new AbilityPredicate(new CardAbility("plusAttack", "beast"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("giveWindfury", "alliedMinion")),
                new AbilityPredicate(new CardAbility("plusAttack", "allAlliedMinions"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("giveWindfury", "alliedMinion")),
                new AbilityPredicate(new CardAbility("plusAttack", "beasts"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("giveWindfury", "alliedMinion")),
                new AbilityPredicate(new CardAbility("plusAttack", "nextMinions"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("giveWindfury", "alliedMinion")),
                new AbilityPredicate(new CardAbility("plusAttack", "minion"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("giveWindfury", "alliedMinion")),
                new AbilityPredicate(new CardAbility("plusAttack", "mech"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("giveWindfury", "alliedMinion")),
                new AbilityPredicate(new CardAbility("plusAttack", "demons"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("giveWindfury", "alliedMinion")),
                new AbilityPredicate(new CardAbility("plusAttack", "alliedCharacter"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("giveWindfury", "alliedMinion")),
                new AbilityPredicate(new CardAbility("plusAttack", "mechs"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("giveWindfury", "alliedMinion")),
                new AbilityPredicate(new CardAbility("plusAttack", "murlocs"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("giveWindfury", "alliedMinion")),
                new AbilityPredicate(new CardAbility("plusAttack", "demon"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("giveWindfury", "alliedMinion")),
                new AbilityPredicate(new CardAbility("plusAttack", "allAlliedCharacter"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("giveWindfury", "alliedMinion")),
                new AbilityPredicate(new CardAbility("plusAttack", "alliedHero"))));

        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("giveWindfury", "minion")),
                new AbilityPredicate(new CardAbility("plusAttack", "pirates"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("giveWindfury", "minion")),
                new AbilityPredicate(new CardAbility("plusAttack", "minion"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("giveWindfury", "minion")),
                new AbilityPredicate(new CardAbility("plusAttack", "beast"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("giveWindfury", "minion")),
                new AbilityPredicate(new CardAbility("plusAttack", "allminions"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("giveWindfury", "minion")),
                new AbilityPredicate(new CardAbility("plusAttack", "beasts"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("giveWindfury", "minion")),
                new AbilityPredicate(new CardAbility("plusAttack", "nextMinions"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("giveWindfury", "minion")),
                new AbilityPredicate(new CardAbility("plusAttack", "minion"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("giveWindfury", "minion")),
                new AbilityPredicate(new CardAbility("plusAttack", "mech"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("giveWindfury", "minion")),
                new AbilityPredicate(new CardAbility("plusAttack", "demons"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("giveWindfury", "minion")),
                new AbilityPredicate(new CardAbility("plusAttack", "alliedCharacter"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("giveWindfury", "minion")),
                new AbilityPredicate(new CardAbility("plusAttack", "mechs"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("giveWindfury", "minion")),
                new AbilityPredicate(new CardAbility("plusAttack", "murlocs"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("giveWindfury", "minion")),
                new AbilityPredicate(new CardAbility("plusAttack", "demon"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("giveWindfury", "minion")),
                new AbilityPredicate(new CardAbility("plusAttack", "allAlliedCharacter"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("giveWindfury", "minion")),
                new AbilityPredicate(new CardAbility("plusAttack", "alliedHero"))));

        //overload
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("overload", "ownManaCrystals")),
                new AbilityPredicate(new CardAbility("unlockOverloaded", "ownManaCrystals"))));

        // change health and deal damage
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("changeHealth", "allMinion")),
                new AbilityPredicate(new CardAbility("spellDamage", "character"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("changeHealth", "allMinion")),
                new AbilityPredicate(new CardAbility("spellDamage", "splitAllEnemyMinion"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("changeHealth", "allMinion")),
                new AbilityPredicate(new CardAbility("spellDamage", "allEnemyCharacter"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("changeHealth", "allMinion")),
                new AbilityPredicate(new CardAbility("spellDamage", "enemyMinion"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("changeHealth", "allMinion")),
                new AbilityPredicate(new CardAbility("spellDamage", "enemyCharacter"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("changeHealth", "allMinion")),
                new AbilityPredicate(new CardAbility("spellDamage", "allMinion"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("changeHealth", "allMinion")),
                new AbilityPredicate(new CardAbility("spellDamage", "splitAllEnemy"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("changeHealth", "allMinion")),
                new AbilityPredicate(new CardAbility("spellDamage", "nextMinions"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("changeHealth", "allMinion")),
                new AbilityPredicate(new CardAbility("spellDamage", "allCharacter"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("changeHealth", "allMinion")),
                new AbilityPredicate(new CardAbility("spellDamage", "minion"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("changeHealth", "allMinion")),
                new AbilityPredicate(new CardAbility("spellDamage", "allEnemyMinions"))));

        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("changeHealth", "minion")),
                new AbilityPredicate(new CardAbility("dealDamage", "enemyMinion"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("changeHealth", "minion")),
                new AbilityPredicate(new CardAbility("dealDamage", "enemyCharacter"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("changeHealth", "minion")),
                new AbilityPredicate(new CardAbility("dealDamage", "splitAllEnemy"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("changeHealth", "minion")),
                new AbilityPredicate(new CardAbility("dealDamage", "nextMinions"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("changeHealth", "minion")),
                new AbilityPredicate(new CardAbility("dealDamage", "minion"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("changeHealth", "minion")),
                new AbilityPredicate(new CardAbility("dealDamage", "allEnemyMinions"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("changeHealth", "minion")),
                new AbilityPredicate(new CardAbility("dealDamage", "splitAll"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("changeHealth", "minion")),
                new AbilityPredicate(new CardAbility("dealDamage", "character"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("changeHealth", "minion")),
                new AbilityPredicate(new CardAbility("dealDamage", "allMinion"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("changeHealth", "minion")),
                new AbilityPredicate(new CardAbility("dealDamage", "allCharacter"))));

        // giveTaunt can't attack
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("cantAttack", "self")),
                new AbilityPredicate(new CardAbility("giveTaunt", "alliedMinion"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("cantAttack", "self")),
                new AbilityPredicate(new CardAbility("giveTaunt", "beast"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("cantAttack", "self")),
                new AbilityPredicate(new CardAbility("giveTaunt", "nextMinions"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("cantAttack", "self")),
                new AbilityPredicate(new CardAbility("giveTaunt", "minion"))));

        // cantAttack and silence
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("cantAttack", "self")),
                new AbilityPredicate(new CardAbility("silence", "allAlliedMinions"))));
        cardPairs.add(new CardPair(
                new AbilityPredicate(new CardAbility("cantAttack", "self")),
                new AbilityPredicate(new CardAbility("silence", "minion"))));


        try (BufferedWriter writer = getWriter("/cardPairs/cardPair_v01.json")) {
            gson.toJson(cardPairs, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Type type = new TypeToken<List<CardPair>>() {
        }.getType();
        gson.fromJson(getReader("/cardPairs/cardPair_v01.json"), type);


        return cardPairs;
    }

}
