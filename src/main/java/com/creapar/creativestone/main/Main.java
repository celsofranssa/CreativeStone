/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creapar.creativestone.main;

import com.creapar.creativestone.util.Result;
import com.creapar.creativestone.util.Tester;
import com.creapar.creativestone.util.Tools;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Celso
 */
public class Main {


    /**
     * Main
     *
     * @param args possible arguments
     */
    public static void main(String[] args) {
        List<String> cards = Arrays.asList("Southsea Deckhand", "Southsea Deckhand", "Blessing of Might", "Blessing of Might", "Argent Squire", "Argent Squire", "Leper Gnome", "Leper Gnome", "Abusive Sergeant", "Abusive Sergeant", "Knife Juggler", "Knife Juggler", "Ironbeak Owl", "Ironbeak Owl", "Annoy-o-Tron", "Shielded Minibot", "Shielded Minibot", "Arcane Golem", "Arcane Golem", "Divine Favor", "Divine Favor", "Muster for Battle", "Muster for Battle", "Coghammer", "Blessing of Kings", "Consecration", "Consecration", "Truesilver Champion", "Truesilver Champion", "Defender of Argus");

        System.out.println("novelty: " + Tools.getNovelty(cards));
        System.out.println("value: " + Tools.getValue(cards));


    }


    private static void geneticTest() {
        Tester t = new Tester();
        Gson gson = new Gson();

        // List<String> heroNames = Arrays.asList("warlock");//, "hunter");//, "mage",
        //  "paladin", "priest", "rogue", "shaman", "warlock",
        //"warrior");
//        List<String> heroNames = Arrays.asList("hunter",  
//                "paladin", "rogue", "shaman", 
//                "warrior");

        String fileDescription;
        Integer candidateSize = 30;
        Integer experiments = 5;
        Integer generations = 500;
        String recipe = "warlockClassicRecipe_02";

        String heroName = "warlock";

        System.out.println("Runing for " + heroName);


        List<Result> results = t.geneticTest(experiments, heroName, recipe, generations, candidateSize, true);
        fileDescription = "/results/" + heroName + "/" + heroName + "Results.json";

        try (BufferedWriter writer = Tools.getWriter(fileDescription)) {
            gson.toJson(results, writer);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }


    }


}
