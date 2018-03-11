package com.nathancorbyn.kanji;

import static spark.Spark.*;

public class KanjiServer {
    public static void main(String args[]) {
        get("/all", (req, res) -> "Hello world");
    }
}
