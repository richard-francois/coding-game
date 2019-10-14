import static java.lang.Math.*;

import java.util.*;


enum EntityType {
    NOTHING, ALLY_ROBOT, ENEMY_ROBOT, RADAR, TRAP, AMADEUSIUM;

    static EntityType valueOf(int id) {
        return values()[id + 1];
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

class Cell {
    boolean known;
    int ore;
    boolean hole;

    Cell(boolean known, int ore, boolean hole) {
        this.known = known;
        this.ore = ore;
        this.hole = hole;
    }

    Cell(Scanner in) {
        String oreStr = in.next();
        if (oreStr.charAt(0) == '?') {
            known = false;
            ore = 0;
        } else {
            known = true;
            ore = Integer.parseInt(oreStr);
        }
        String holeStr = in.next();
        hole = (holeStr.charAt(0) != '0');
    }
}

class Action {

    final String command;
    final Coord pos;
    final EntityType item;
    String message;

    private Action(String command, Coord pos, EntityType item) {
        this.command = command;
        this.pos = pos;
        this.item = item;
    }

    static Action none() {
        return new Action("WAIT", null, null);
    }

    static Action move(Coord pos) {
        return new Action("MOVE", pos, null);
    }

    static Action dig(Coord pos) {
        return new Action("DIG", pos, null);
    }

    static Action request(EntityType item) {
        return new Action("REQUEST", null, item);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(command);
        if (pos != null) {
            builder.append(' ').append(pos);
        }
        if (item != null) {
            builder.append(' ').append(item);
        }
        if (message != null) {
            builder.append(' ').append(message);
        }
        return builder.toString();
    }
}

class Entity {
    private static final Coord DEAD_POS = new Coord(-1, -1);

    // Updated every turn
    final int id;
    final EntityType type;
    final Coord pos;
    final EntityType item;

    // Computed for my robots
    Action action;

    Entity(Scanner in) {
        id = in.nextInt();
        type = EntityType.valueOf(in.nextInt());
        pos = new Coord(in);
        item = EntityType.valueOf(in.nextInt());
    }

    boolean isAlive() {
        return !DEAD_POS.equals(pos);
    }

    public String toString() {
        return id + " " + type + " " + pos + " " + item + " " + action;
    }
}


class Team {
    int score;
    Collection<Entity> robots;

    void readScore(Scanner in) {
        score = in.nextInt();
        robots = new ArrayList<>();
    }

    public boolean isActive(Entity robot) {
        return ((robot.pos.x + robot.pos.y) != -2);
    }

    public int activeRobotsAtHQ() {
        int based = 0;
        for (Entity robot : robots) {
            if (isActive(robot) && robot.pos.x == 0)
                based++;
        }
        return based;
    }

    public int activeRobotsOnGround() {
        int outside = 0;
        for (Entity robot : robots) {
            if ((robot.pos.x > 0) && isActive(robot))
                outside++;
        }
        return outside;
    }
}


class Board {
    // Given at startup
    final int width;
    final int height;

    // Updated each turn
    final Team myTeam = new Team();
    final Team opponentTeam = new Team();
    int myRadarCooldown;
    int myTrapCooldown;
    Map<Integer, Entity> entitiesById;
    Collection<Coord> myRadarPos;
    Collection<Coord> myTrapPos;
    Collection<Coord> plentyCellsInRange = new ArrayList<Coord>();
    Collection<Coord> excludedCells = new ArrayList<Coord>();

    private Cell[][] cells;

    Board(Scanner in) {
        width = in.nextInt();
        height = in.nextInt();
    }

    void update(Scanner in) {
        // Read new data
        myTeam.readScore(in);
        opponentTeam.readScore(in);
        cells = new Cell[height][width];
        plentyCellsInRange = new ArrayList<Coord>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Cell current = new Cell(in);
                cells[y][x] = current;

                // System.err.println(current.ore);
                if (current.ore > 0) {
                    plentyCellsInRange.add(new Coord(x, y));
                } else {

                }
            }
        }

        int entityCount = in.nextInt();
        myRadarCooldown = in.nextInt();
        myTrapCooldown = in.nextInt();

        entitiesById = new HashMap<>();
        myRadarPos = new ArrayList<>();
        myTrapPos = new ArrayList<>();
        for (int i = 0; i < entityCount; i++) {
            Entity entity = new Entity(in);
            entitiesById.put(entity.id, entity);
            if (entity.type == EntityType.ALLY_ROBOT) {
                myTeam.robots.add(entity);
            } else if (entity.type == EntityType.ENEMY_ROBOT) {
                opponentTeam.robots.add(entity);
            } else if (entity.type == EntityType.RADAR) {
                myRadarPos.add(entity.pos);
            } else if (entity.type == EntityType.TRAP) {
                myTrapPos.add(entity.pos);
            }
        }
    }

    boolean cellExist(Coord pos) {
        return (pos.x >= 0) && (pos.y >= 0) && (pos.x < width) && (pos.y < height);
    }

    Cell getCell(Coord pos) {
        return cells[pos.y][pos.x];
    }

    Collection<Coord> getPlentyCellsInRange() {
        return plentyCellsInRange;
    }

    public Collection<Coord> getMyRadarPos() {
        return myRadarPos;
    }

    public Collection<Coord> getMyTrapPos() {
        return myTrapPos;
    }

    public Collection<Coord> getExcludedCells() {
        return excludedCells;
    }
}


class MacroTask {

    private Board board;

    public void setBoard(Board board) {
        this.board = board;
    }

    // determine sillon plus proche du robot, sans piege
    public Coord getNearestSafeHoleWithOre(Entity robot, Coord trap) {
        if (trap != null)
            board.getExcludedCells().add(trap);
        Collection<Coord> plentyCells = board.getPlentyCellsInRange();
        plentyCells.removeAll(board.getExcludedCells());
        plentyCells.removeAll(board.getMyTrapPos());

        Coord nearestCellPos = robot.pos;
        int nearestCellDistance = 65534;

        for (Coord currentPos : plentyCells) {
            int currentDistance = robot.pos.distance(currentPos);
            if (currentDistance < nearestCellDistance) {
                nearestCellDistance = currentDistance;
                if (Collections.frequency(board.myTrapPos, currentPos) == 0)
                    nearestCellPos = new Coord(currentPos);
            }
        }

        System.err.println("Nearest: "+ nearestCellPos);
        return nearestCellPos;
    }

    public boolean isOnSafeCell(Entity robot) {
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


class MacroAction {

    private Board board;
    public MacroTask task = new MacroTask();
    private HashMap<Integer, Coord> robotsInitialPositions;
    private HashMap<Integer, Coord> robotsLastCalculatedRadarPositionToReach = new HashMap<Integer, Coord>();
    private HashMap<Integer, Action> robotsLastAction = new HashMap<Integer, Action>();
    private HashMap<Integer, Coord> robotsLastDig = new HashMap<Integer, Coord>();
    Collection<Coord> enemyBombersLastPositions = new ArrayList<>();

    int horizonX = 0;
    int horizonY = 0;
    int horizon_increment = 4;
    LinkedList <Coord> nextPotentialDetectedRadarPositions = new LinkedList <>();

    public void setBoard(Board b) {
        board = b;
        task.setBoard(b);
    }

    public Board getBoard() {
        return board;
    }

    public void setRobotsInitialPositions(HashMap<Integer, Coord> robotsInitialPositions) {
        this.robotsInitialPositions = robotsInitialPositions;
    }

    public void setRobotsLastAction(HashMap<Integer, Action> lastAction) {
        this.robotsLastAction = lastAction;
    }

    public void setRobotsLastDig(HashMap<Integer, Coord> robotsLastDig) {
        this.robotsLastDig = robotsLastDig;
    }

    private void updateHorizon(Entity robot) {
        //horizon = Math.max(horizon, robot.pos.x);
    }

    public Action returnToHQ(Entity robot) {
        return Action.move(new Coord(0, robot.pos.y));
    }

    // go to explore
    public Action exploreX(Entity robot) {
        Coord dest;
        // extend map cover
        int newHorizon = horizonX + horizon_increment/2;
        if (newHorizon >= board.width) {
            newHorizon = 0;

            System.err.println("Horizon X reached");
            // crazy mode - should be replaced by other tactic - (!)
            //dest = new Coord(task.getRandomNumberInRange(1, board.width - 1),
            //    task.getRandomNumberInRange(1, board.height - 1));
            return returnToHQ(robot);
        } else {
            dest =new Coord(robot.pos.x + horizon_increment/2, robot.pos.y);
        }
        return Action.move(dest);
    }

    public Action dig(Entity robot) {
        updateHorizon(robot);
        //check dangerous cells before !!
        checkCellSafeForDig(robot.pos);
        return Action.dig(robot.pos);
    }

    public Action digNearestHoleWithOre(Entity robot) {
        updateHorizon(robot);
        int retries = 0;
        Coord target = task.getNearestSafeHoleWithOre(robot, null);
        //check dangerous cells before dig, at last instant !
        while (!checkCellSafeForDig(target) && ++retries<3) {
            target = task.getNearestSafeHoleWithOre(robot, target);
        }
        if (robot.pos.distance(target) ==0) {
            return exploreX(robot);
        } else {
            return Action.dig(target);
        }
    }

    public Action placeRadar(Entity robot) {
        Coord dest = robotsLastCalculatedRadarPositionToReach.get(robot.id);
        if (dest == null) {
            if (nextPotentialDetectedRadarPositions.size() >0) {
                // new detected sillon by rangers
                dest = nextPotentialDetectedRadarPositions.pollFirst();

            } else {
                // extend map cover
                int newHorizon = horizonX + horizon_increment;
                if (newHorizon >= board.width) {
                    newHorizon = 0;

                    System.err.println("Horizon reached");
                    // crazy mode - should be replaced by other tactic - (!)
                    dest = new Coord(task.getRandomNumberInRange(1, board.width - 1),
                        task.getRandomNumberInRange(1, board.height - 1));
                } else {
                    dest = new Coord(newHorizon, robotsInitialPositions.get(robot.id).y);
                }
            }
            robotsLastCalculatedRadarPositionToReach.put(robot.id, dest);
        }

        if (robot.pos.x < dest.x) {
            return Action.move(dest);
        } else {
            robotsLastCalculatedRadarPositionToReach.put(robot.id, null);
            ////check dangerous cells before !!
            checkCellSafeForDig(robot.pos);
            return Action.dig(dest);
        }
    }

    public void setNextRadarPosition(Entity robot) {
        if (robotsLastAction.get(robot.id).command.startsWith("DIG")) {
            // always retain nearest !!!  normaly in insertion order
            nextPotentialDetectedRadarPositions.add(robot.pos);
        }

        //todo : determine if position is already whitin a scanner range
        System.err.println(robot.id + "-nextRadarPos: " + nextPotentialDetectedRadarPositions);
        System.err.println(robot.id + "-dump-ore-cells: " + board.getPlentyCellsInRange());
    }

    // enemy item and action masked . Determine if positions tracing match holes in match
    // if yes, hole is possible dangerous
    public void trackEnemyRobots() {
        for (Entity robot : board.opponentTeam.robots) {
            if (Collections.frequency(enemyBombersLastPositions, robot.pos) ==0)
                enemyBombersLastPositions.add(robot.pos);
        }
    }

    public boolean checkCellSafeForDig(Coord position) {
        boolean test = true;
        Cell cell = board.getCell(position);
        if (cell.hole) {
            // si pb de performance
            /*
            Collection<Coord> colScan = enemyBombersLastPositions.stream()
                .filter(epos -> epos.x == position.x)
                .collect(Collectors.toList());

            Collection<Coord> rowScan = colScan.stream()
                .filter(epos -> epos.y == position.y)
                .collect(Collectors.toList());

            if ((rowScan != null) && rowScan.size()>0)
                test = false;
            */
            if (Collections.frequency(enemyBombersLastPositions, position) == 0 )
                test = false;
        }
        System.err.println("Cell safe @"+position+":"+test);
        return test;
    }
}

/*

 *** MAIN ***

 */

class Player {

    final Scanner in = new Scanner(System.in);

    MacroAction ma = new MacroAction();

    public static void main(String args[]) {
        new Player().run();
    }

    void run() {
        // Parse initial conditions
        Board board = new Board(in);
        HashMap<Integer, Coord> robotsInitialPositions = new HashMap<Integer, Coord>();
        HashMap<Integer, Action> robotsLastAction = new HashMap<Integer, Action>();
        HashMap<Integer, Coord> robotsLastDig = new HashMap<Integer, Coord>();

        boolean storeGameInitialConditions = true;

        //
        // Main LOOP
        //

        while (true) {

            // Parse current state of the game
            board.update(in);
            ma.setBoard(board);
            ma.setRobotsLastAction(robotsLastAction);
            ma.setRobotsLastDig(robotsLastDig);

            if (storeGameInitialConditions) {
                for (Entity robot : board.myTeam.robots) {
                    robotsInitialPositions.put(robot.id, robot.pos);
                }
                storeGameInitialConditions = false;
                ma.setRobotsInitialPositions(robotsInitialPositions);
            }

            // ----------------------------
            // Insert your strategy here
            // ----------------------------

            // track opponent moves
            ma.trackEnemyRobots();

            // drive my robots
            for (Entity robot : board.myTeam.robots) {
                robot.action = Action.none();
                robot.action.message = "Tolan " + robot.id;

                if (robot.item == EntityType.AMADEUSIUM) {
                    ma.setNextRadarPosition(robot);
                    robot.action = ma.returnToHQ(robot);

                } else if ((robot.item == EntityType.NOTHING)) { // Free to DO SOMETHING
                    if (board.getPlentyCellsInRange().isEmpty()) {
                        if (robot.pos.x == 0) { // at HQ
                            System.err.println("radar count down : " + board.myRadarCooldown);
                            if (board.myRadarCooldown == 0) {
                                //board.myTeam.activeRobotsAtHQ() < board.myTeam.activeRobotsOnGround()) {
                                robot.action = Action.request(EntityType.RADAR);
                            } else {
                                robot.action = ma.exploreX(robot);
                            }
                        } else { // on ground scan
                            if (board.myTeam.isActive(robot)) {
                                if (ma.task.isOnSafeCell(robot) && !board.getCell(robot.pos).hole) {
                                    robot.action = ma.dig(robot);
                                } else {
//                                    if ((board.myRadarPos.size() > 0) || (board.myRadarCooldown == 0))
//                                        robot.action = ma.returnToHQ(robot);
//                                    else
                                        robot.action = ma.returnToHQ(robot);
                                }
                            }
                        }
                    } else { // something to collect somewhere
                            robot.action = ma.digNearestHoleWithOre(robot);
                    }

                } else if (robot.item == EntityType.RADAR) { // RADAR To Borrow
                    robot.action = ma.placeRadar(robot);

                } else if (robot.item == EntityType.TRAP) { // TRAP To Borrow
                }
            } // robots

            // Send your actions for this turn
            for (Entity robot : board.myTeam.robots) {
                robotsLastAction.put(robot.id, robot.action);
                System.out.println(robot.action);
            }
        } // main loop
    } // run
} // player
