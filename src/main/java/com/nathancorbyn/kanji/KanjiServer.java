package com.nathancorbyn.kanji;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;
import static spark.Spark.*;

public class KanjiServer {
    private static final String DATA = "data/jouyou_decomposition";
    private static List<Kanji> kanji;

    public static void main(String args[]) {
        try {
            System.out.println("Loading data...");
            loadData();
        } catch(IOException e) {
            System.out.println("Failed!");
            System.exit(1);
        }

        get("/all", (req, res) -> "Hello world");
    }

    public static void loadData() throws IOException {
        kanji = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(new File(DATA)));

        String line;
        Map<String, List<String>> characters = new HashMap<>();

        while ((line = br.readLine()) != null) {
            String[] split = line.split(":"); 
            String character = split[0];
            
            if (split.length == 2) {
                String[] components = split[1].split(",");
                List<String> temp = new ArrayList<>();
                for (String component : components) 
                    temp.add(component);
                characters.put(character, temp);
            } else characters.put(character, new ArrayList<>());

            System.out.print(character + " ");
        }

        SortedSet<String> toResolve = new TreeSet<>(characters.keySet());
        Map<String, Kanji> resolved = new HashMap<>();

        while (!toResolve.isEmpty()) {
            String current = toResolve.first();
            toResolve.remove(current);
            Stack<String> stack = new Stack<>();
            stack.push(current);

            while (!stack.isEmpty()) {
                String character = stack.pop();
                if (resolved.keySet().contains(character))
                    continue;

                boolean brokenDependency = false;
                List<Kanji> components = new ArrayList<>();
                for (String component : characters.getOrDefault(character, new ArrayList<String>())) {
                    if (!resolved.keySet().contains(component)) {
                        if (!brokenDependency) {
                            stack.push(current);
                            brokenDependency = true;
                        }

                        stack.push(component);
                    } else components.add(resolved.get(component));
                }

                if (!brokenDependency) {
                    Kanji temp = new Kanji(character, components);
                    resolved.put(character, temp);
                    kanji.add(temp);
                    System.out.println(temp.toJSON().toString());
                    toResolve.remove(character);
                }
            }
        }
    }
}
