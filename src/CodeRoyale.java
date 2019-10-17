import java.util.*;
import java.io.*;
import java.math.*;

import static java.lang.Math.abs;

enum UnitType {
    UNDEFINED(-2), QUEEN(-1), KNIGHT(0), ARCHER(1);

    public final int id;
     UnitType(int id) {
        this.id = id;
    }

    static UnitType valueOf(int id) {
        for (UnitType u : values()) {
            if (u.id == id) {
                return u;
            }
        }
        return null;
    }
};

enum StructureType {
    UNDEFINED(-1), BARRACKS(2);
    public final int id;

     StructureType(int id) {
        this.id = id;
    }

    static StructureType valueOf(int id) {
        for (StructureType st : values()) {
            if (st.id == id) {
                return st;
            }
        }
        return null;
    }
};

enum OwnerType {
    UNDEFINED(-1), FRIENDLY(0), ENEMY(1);

    public final int id;

     OwnerType(int id) {
        this.id = id;
    }

    static OwnerType valueOf(int id) {
        for (OwnerType ot : values()) {
            if (ot.id == id) {
                return ot;
            }
        }
        return null;
    }
};

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        Board board = new Board(in);

        // game loop
        while (true) {
            board.update(in);

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
    final int x;
    final int y;

    Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    Coord(Scanner in) {
        this(in.nextInt(), in.nextInt());
    }

    Coord(Coord coord) {
        this.x = coord.x;
        this.y = coord.y;
    }

    Coord add(Coord other) {
        return new Coord(x + other.x, y + other.y);
    }

    // Manhattan distance (for 4 directions maps)
    // see: https://en.wikipedia.org/wiki/Taxicab_geometry
    int distance(Coord other) {
        return abs(x - other.x) + abs(y - other.y);
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + x;
        result = PRIME * result + y;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Coord other = (Coord) obj;
        return (x == other.x) && (y == other.y);
    }

    public String toString() {
        return x + " " + y;
    }
}

class Board {

    // Cell[][] cells;
     int width = 1920;
     int height = 1000;

    Collection<Site> sites = new ArrayList<>();
    Collection<Unit> myTeam = new ArrayList<>();
    Collection<Unit> opponentTeam = new ArrayList<>();

     int numSites = 0;
     int gold = 0;
     int touchedSite = 0;

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
        //for (Site site : sites) { site.update(in); }
        sites.stream().forEach(s -> s.update(in));
        int numUnits = in.nextInt();
        for (int i = 0; i < numUnits; i++) {

                int x = in.nextInt();
                int y = in.nextInt();
                int owner = in.nextInt();
            UnitType unitType = UnitType.valueOf(in.nextInt());
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
     int siteId = 0;
     int x = 0;
     int y = 0;
     int radius = 0;

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
    UnitType unitType = UnitType.UNDEFINED;
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
