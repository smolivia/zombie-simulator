import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.princeton.cs.introcs.StdDraw;
import support.cse131.ArgsProcessor;
import support.cse131.Timing;
import zombies.ZombieSimulationFiles;

public class ZombieSimulator {
	private static final String ZOMBIE_TOKEN_VALUE = "Zombie";
	private List<Entity> entities;
	

	//Construct a zombie simulator with an empty list of Entity objects.
	public ZombieSimulator() {
		entities = new LinkedList<Entity>();
	}

	//returns the list of non-zombies that have not yet been infected
	public List<Entity> getEntities() {
		return entities;
	}

	//reads in starting coordinates of all entities from a known file format
    //and sorts them according to their state of being a zombie or not a zombie
	public void readEntities(ArgsProcessor ap) {
		int n = ap.nextInt(); //find total number of entities
		for(int i = 0; i < n; i++) {
			String next = ap.nextString(); //read in whether the entity is a zombie
			double x = ap.nextDouble(); //read in starting x coordinate
			double y = ap.nextDouble(); //read in starting y coordinate
			if (next.equals(ZOMBIE_TOKEN_VALUE)) { //entity is zombie
				Entity e = new Entity(true, x, y);
				entities.add(e);
			}  else { //entity is not zombie
				Entity e = new Entity(false, x, y);
				entities.add(e);
			}
			
		}
	}

	//count the number of zombies
	public int getZombieCount() {
		int count = 0;
		for(int i = 0; i < entities.size(); i++) { //iterate through entities and count zombies
			Entity e = entities.get(i);
			if(e.isZombie()) {
				count++;
			}
		}
		return count;
	}

	//count the number of non-zombies
	public int getNonzombieCount() {
		return entities.size() - getZombieCount();
	}

	//CODE WRITTEN BY Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
	public void draw() {
		StdDraw.clear(); 

		for (Entity entity : getEntities()) {
			entity.draw();
		}

		StdDraw.show();
	}

    //update the state of the entities by adding all entities that continue to the next round to a list
    //deltaTime is the amount of time since the previous frame of the simulation
	public void update(double deltaTime) {
		List<Entity> newList = new LinkedList<Entity>();
		for(int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			if(e.update(entities, deltaTime)) { //entity continues to the next round
				newList.add(e);
			}
		}
		this.entities = newList;
	}

    //run the zombie simulation
	public static void main(String[] args) {
		StdDraw.enableDoubleBuffering();
        //create new ZombieSimulator object
        ZombieSimulator zombieSimulator = new ZombieSimulator();
        //read in entities from files
		ArgsProcessor ap = ZombieSimulationFiles.createArgsProcessorFromFile(args);
		zombieSimulator.readEntities(ap);
        //draw initial states of entities
		zombieSimulator.draw();
		StdDraw.pause(500);

		double prevTime = Timing.getCurrentTimeInSeconds();
		while (zombieSimulator.getNonzombieCount() > 0) { //continue running simulation while there are non-zombies left
			double currTime = Timing.getCurrentTimeInSeconds();
			double deltaTime = currTime - prevTime; //calculate change in time
			if (deltaTime > 0) { //continue updating and drawing entities only if time has passed, prevents updating too often
				zombieSimulator.update(deltaTime);
				zombieSimulator.draw();
			}
			StdDraw.pause(10);
			prevTime = currTime; //update prevTime
			
		}
	}
}
