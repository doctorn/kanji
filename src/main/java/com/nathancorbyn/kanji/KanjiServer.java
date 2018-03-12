package com.nathancorbyn.kanji;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;
import static spark.Spark.*;

public class KanjiServer {
    private static final String DATA = "data/jouyou_decomposition";
    private static List<Kanji> KANJI = new ArrayList<>();
    private static PebbleEngine ENGINE;    

    public static void main(String args[]) {
        try {
            System.out.println("Loading data...");
            loadData();
        } catch(IOException e) {
            System.out.println("Failed!");
            System.exit(1);
        }

        ENGINE = new PebbleEngine.Builder().build();
        get("/all", (req, res) -> {
            Map<String, Object> context = new HashMap<>();
            context.put("kanji", KANJI);
            try {
                return render("./templates/index.html", context);
            } catch(IOException e) {
                e.printStackTrace();
                return e.getMessage();
            } catch(PebbleException e) {
                e.printStackTrace();
                return e.getMessage();
            }
        });
    }

    public static String render(String template, Map<String, Object> context) 
            throws PebbleException, IOException {
        PebbleTemplate compiledTemplate = ENGINE.getTemplate(template);
        Writer writer = new StringWriter();
        compiledTemplate.evaluate(writer, context);
        return writer.toString();
    }

    public static void loadData() throws IOException {
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
                    KANJI.add(temp);
                    System.out.println(temp.toJSON().toString());
                    toResolve.remove(character);
                }
            }
        }
    }
}
