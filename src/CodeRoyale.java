import java.util.*;
import java.util.stream.Collectors;

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

        boolean DEBUG = false;
        boolean DEV_TEST = true;

        Scanner in = new Scanner(System.in);
        Board board = new Board(in);
        MacroAction ma = new MacroAction(board);

        if (DEV_TEST) {
        }

        // game loop
        while (true) {
            board.update(in);
            System.err.println(board.myTeam);
            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            Unit queen = board.myTeam.getQueen();
            //int nearestSiteId =

            // First line: A valid queen action
            // Second line: A set of training instr
            System.out.println("WAIT");
            System.out.println("TRAIN");
        }
    }
}

class Board {

    // Cell[][] cells;
    int width = 1920;
    int height = 1000;

    Collection<Site> sites = new ArrayList<>();
    Team myTeam = new Team();
    Team opponentTeam = new Team();
    Team ghostTeam = new Team();

    int numSites = 0;
    int gold = 0;
    int touchedSite = 0;

    Board(Scanner in) {
        numSites = in.nextInt();
        for (int i = 0; i < numSites; i++) {
            sites.add(new Site(in));
        }
    }

    void update(Scanner in) {
        // Read new data
        gold = in.nextInt();
        touchedSite = in.nextInt(); // -1 if none
        //for (Site site : sites) { site.update(in); }
        sites.stream().forEach(s -> s.update(in));

        myTeam.clearUnits();
        opponentTeam.clearUnits();
        int numUnits = in.nextInt();
        for (int i = 0; i < numUnits; i++) {
            Unit unit = new Unit(in);
            if (unit.owner == OwnerType.FRIENDLY) {
                myTeam.add(unit);
            } else if (unit.owner == OwnerType.ENEMY) {
                opponentTeam.add(unit);
            } else {
                ghostTeam.add(unit);
            }
        }
    }

    public void print() {

    }
}

// IA Intelligence
class MacroAction {

   Board board;

   MacroAction(Board board) {
       this.board = board;
   }
}

class MacroTask {

    Board board;

    public void setBoard(Board board) {
        this.board = board;
    }

    // determine site le plus proche a construire
    public int getNearestFreeSite(coding.game.finished.Entity robot, coding.game.finished.Coord trap) {
        if (trap != null)
            board.getExcludedCells().add(trap);
        Collection<coding.game.finished.Coord> plentyCells = board.getPlentyCellsInRange();
        plentyCells.removeAll(board.getExcludedCells());
        plentyCells.removeAll(board.getMyTrapPos());

        coding.game.finished.Coord nearestCellPos = robot.pos;
        int nearestCellDistance = 65534;

        for (coding.game.finished.Coord currentPos : plentyCells) {
            int currentDistance = robot.pos.distance(currentPos);
            if (currentDistance < nearestCellDistance) {
                nearestCellDistance = currentDistance;
                if (Collections.frequency(board.myTrapPos, currentPos) == 0)
                    nearestCellPos = new coding.game.finished.Coord(currentPos);
            }
        }

        System.err.println("Nearest: "+ nearestCellPos);
        return nearestCellPos;
    }

    public boolean isOnSafeCell(coding.game.finished.Entity robot) {
        return (Collections.frequency(board.myRadarPos, robot.pos) == 0);
    }

    public int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}

class Action {

    final String command;
    final Coord pos;
    final List<Integer> sites;
    UnitType type;

    String message;

    // Order of params: COMMAND  POSITION    SITES
    Action(String command, Coord pos, List<Integer> sites, UnitType type) {
        this.command = command;
        this.pos = pos;
        this.sites = sites;
        this.type = type;
    }

    static Action none() {
        return new Action("WAIT", null, null, null);
    }

    static Action move(Coord pos) {
        return new Action("MOVE", pos, null, null);
    }

    static Action build(int siteId, UnitType type) {
        List<Integer> sites = new ArrayList<Integer>();sites.add(siteId);
        return new Action("BUILD", null, sites, type);

    }

    static Action train(List<Integer> sites) {
        return new Action("TRAIN", null, sites, null);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(command);
        if (pos != null) {
            builder.append(' ').append(pos);
        }
        if (sites != null) {
            builder.append(' ').append(sites);
        }
        if (type != null) {
            builder.append(' ').append(type);
        }
        if (message != null) {
            builder.append(' ').append(message);
        }
        return builder.toString();
    }
}

class Team {

    List<Unit> units;

    Team() {
        clearUnits();
    }

    void clearUnits() {
        units = new ArrayList<>();
    }

    void add(Unit u) {
        units.add(u);
    }

    public Unit getQueen() {
        return units.stream().filter(u->UnitType.QUEEN.equals(u.unitType)).findAny().orElse(null);
    }

    public long getKnightsNumber() {
        return units.stream().filter(u->UnitType.KNIGHT.equals(u.unitType)).count();
    }

    public long getArchersNumber() {
        return units.stream().filter(u->UnitType.ARCHER.equals(u.unitType)).count();
    }

    public String toString() {
        units.stream().forEach(u -> System.err.println(u));
        return "";
    }
}

class Site {
    /*
    siteId: The numeric identifier of the site
    x y: The numeric coordinates of the site's center
    radius: The radius of the site
    */
    int siteId = 0;
    Coord pos;
    int radius = 0;

    int ignore1 = 0;
    int ignore2 = 0;
    StructureType structureType = StructureType.UNDEFINED;
    OwnerType owner = OwnerType.UNDEFINED;
    int param1 = 0;
    int param2 = 0;

    Site(Scanner in) {
        siteId = in.nextInt();
        pos = new Coord(in);
        radius = in.nextInt();
    }

    public void update(Scanner in) {
        siteId = in.nextInt();
        ignore1 = in.nextInt(); // used in future leagues
        ignore2 = in.nextInt(); // used in future leagues
        structureType = StructureType.valueOf(in.nextInt()); // -1 = No structure, 2 = Barracks
        owner = OwnerType.valueOf(in.nextInt()); // -1 = No structure, 0 = Friendly, 1 = Enemy
        param1 = in.nextInt();
        param2 = in.nextInt();
    }

    public String toString() {
        return "site: "+siteId+" "+pos+ " "+radius+" "+structureType+" "+owner+" "+param1+ " "+param2;
    }
}

class Unit {

    Coord pos;
    OwnerType owner = OwnerType.UNDEFINED;
    UnitType unitType = UnitType.UNDEFINED;
    int health = 0;

    Unit (Scanner in) {
        pos = new Coord(in);
        owner = OwnerType.valueOf(in.nextInt());
        unitType = UnitType.valueOf(in.nextInt());
        health = in.nextInt();
    }
    public String toString() {
        return "unit: "+pos+ " "+owner+ " "+unitType+ " "+health;
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


