import java.util.*;
import java.io.*;
import java.math.*;

enum UnitType { QUEEN, KNIGHT, ARCHER};
enum StructureType { UNDEFINED, BARRACKS};
enum OwnerType { UNDEFINED, FRIENDLY, ENEMY};

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        //board?scanner

        // game loop
        while (true) {

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");


            // First line: A valid queen action
            // Second line: A set of training instructions
            System.out.println("WAIT");
            System.out.println("TRAIN");
        }
    }
}

class Coord {
    int x;
    int y;

    Coord (int x, int y) {
        this.x = x;
        this.y = y;
    }
}

class Board {

    //private Cell[][] cells;
    private int width = 1920;
    private int height = 1000;

    Collection<Site> sites = new ArrayList<>();
    private int numSites = 0;
    private int gold = 0;
    private int touchedSite = 0;

    Board(Scanner in) {
        numSites = in.nextInt();
        for (int i = 0; i < numSites; i++) {

            int siteId = in.nextInt();
            int x = in.nextInt();
            int y = in.nextInt();
            int radius = in.nextInt();

            Site site = new Site(siteId, x, y, radius);
            sites.add(site);
        }
    }

    void update(Scanner in) {
        // Read new data
        //myTeam.readScore(in);
        //opponentTeam.readScore(in);

        gold = in.nextInt();
        touchedSite = in.nextInt(); // -1 if none
        for (Site site : sites) {
            site.update(in);
        }
        int numUnits = in.nextInt();
        for (int i = 0; i < numUnits; i++) {
            int x = in.nextInt();
            int y = in.nextInt();
            int owner = in.nextInt();
            int unitType = in.nextInt(); // -1 = QUEEN, 0 = KNIGHT, 1 = ARCHER
            int health = in.nextInt();
        }
    }
}

class Site {
/*
    siteId: The numeric identifier of the site
    x y: The numeric coordinates of the site's center
    radius: The radius of the site
    */
    private int siteId = 0;
    private int x = 0;
    private int y = 0;
    private int radius = 0;

    int ignore1 = 0;
    int ignore2 = 0;
    int structureType = 0;
    int owner = 0;
    int param1 = 0;
    int param2 = 0;

    Site(int s, int x, int y, int r) {
        siteId = s;
        this.x = x;
        this.y = y;
        this.radius = r;
    }

    public void update(Scanner in) {
        siteId = in.nextInt();
        ignore1 = in.nextInt(); // used in future leagues
        ignore2 = in.nextInt(); // used in future leagues
        structureType = in.nextInt(); // -1 = No structure, 2 = Barracks
        owner = in.nextInt(); // -1 = No structure, 0 = Friendly, 1 = Enemy
        param1 = in.nextInt();
        param2 = in.nextInt();
    }
}

class Unit {
    int x = 0;
    int y = 0;
    int owner = 0;
    int unitType = 0;
    int health = 0;
}

class Team {

    List<Unit> units = new ArrayList<>();
    public void update(Scanner in) {
        int siteId = in.nextInt();
        int ignore1 = in.nextInt(); // used in future leagues
        int ignore2 = in.nextInt(); // used in future leagues
        int structureType = in.nextInt(); // -1 = No structure, 2 = Barracks
        int owner = in.nextInt(); // -1 = No structure, 0 = Friendly, 1 = Enemy
        int param1 = in.nextInt();
        int param2 = in.nextInt();
    }
}
