import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import edu.princeton.cs.introcs.StdDraw;
import support.cse131.Timing;

public class Entity {
	
	private double x, y, radius;
	private boolean isZombie;

	//constructor for entity indicating whether it is a zombie and its starting coordinates
	public Entity(boolean isZombie, double x, double y) {
		this.x = x;
		this.y = y;
		this.isZombie = isZombie;
		this.radius = 0.008;
	}

	//returns true if the entity is a zombie
	public boolean isZombie() {
		return this.isZombie;
	}

	//return the x coordinate of the center of an entity (represented by a circle)
	public double getX() {
		return this.x;
	}

	//return the y coordinate of the center of an entity(represented by a circle)
	public double getY() {
		return this.y;
	}

	//getter for entity's radius
	public double getRadius() {
		return this.radius;
	}

	//draws the entity using StdDraw API
    //zombies are drawn to be red and non-zombies are drawn to be blue
    //both types of entities are drawn as a circle
	public void draw() {
		if(isZombie) {
			StdDraw.setPenColor(Color.red);
		} else {
			StdDraw.setPenColor(Color.blue);
		}
		StdDraw.filledCircle(this.x, this.y, this.radius);
	}


    //returns the distance between this entity's center and another point
	public double distanceCenterToPoint(double xOther, double yOther) {
		double xSquared = Math.pow(this.x - xOther, 2);
		double ySquared = Math.pow(this.y - yOther, 2);
		double result = Math.pow(xSquared + ySquared, 0.5);
		return result;
	}

	//written by Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/) returns the distance between this entity's center and another entity's center
	public double distanceCenterToCenter(Entity other) {
		return distanceCenterToPoint(other.getX(), other.getY());
	}

	//returns the distance between the edge of one entity's circle and the edge of another entity's circle
	public double distanceEdgeToEdge(double xOther, double yOther, double radiusOther) {
		double totalDistance = distanceCenterToPoint(xOther, yOther);
		double result = totalDistance - this.radius - radiusOther;
		return result;
	}

	//written by Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/) returns the distance between this entity's edge and another entity's edge
	public double distanceEdgeToEdge(Entity other) {
		return distanceEdgeToEdge(other.getX(), other.getY(), other.getRadius());
	}

	//returns true if this entity is touching another entity
	public boolean isTouching(double xOther, double yOther, double radiusOther) {
		if(distanceEdgeToEdge(xOther, yOther, radiusOther) <= 0) {
			return true;
		} else {
			return false;
		}
		
	}

	//overlaoded isTouching method taking an Entity object as a parameter
	public boolean isTouching(Entity other) {
		return isTouching(other.getX(), other.getY(), other.getRadius());
	}

	//written by Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/), moves one entity towards another
	public void moveToward(double xOther, double yOther, double amount) {
		double xVector = xOther - getX();
		double yVector = yOther - getY();
		double angle = Math.atan2(yVector, xVector);
		double xAmount = amount * Math.cos(angle);
		double yAmount = amount * Math.sin(angle);
		
			
		this.x += xAmount;
		this.y += yAmount;
		
	}

	//overlaoded moveToward method taking Entity object as a parameter
	public void moveToward(Entity other, double amount) {
		if(other != null) {
			moveToward(other.getX(), other.getY(), amount);
		}
	}

	//moves one Entity away from another by a certain amount
	public void moveAwayFrom(double xOther, double yOther, double amount) {
		moveToward(xOther, yOther, -amount);
	}

	//overlaoded moveAwayFrom method taking Entity object as a parameter
	public void moveAwayFrom(Entity other, double amount) {
		moveAwayFrom(other.getX(), other.getY(), amount);
	}

    //finds the closest zombie or non-zombie entity to this entity by iterating through the entire entities list
	private Entity findClosest(List<Entity> entities, boolean includeZombies, boolean includeNonzombies) {
		Entity closest = null;
		double closestDist = Double.MAX_VALUE;
		for (Entity other : entities) {
			if (this != other) {
				if ((other.isZombie() && includeZombies) || (!other.isZombie() && includeNonzombies)) {
					double dist = distanceEdgeToEdge(other);
					if (dist < closestDist) {
						closest = other;
						closestDist = dist;
					}
				}
			}
		}
		return closest;
	}

	//finds closest non-zombie entity using findClosest
	public Entity findClosestNonzombie(List<Entity> entities) {
		return findClosest(entities, false, true);
	}

	//finds closest zombie entity using findClosest
	public Entity findClosestZombie(List<Entity> entities) {
		return findClosest(entities, true, false);
	}

	//finds closest zombie or non-zombie entity using findClosest
	public Entity findClosestEntity(List<Entity> entities) {
		return findClosest(entities, true, true);
	}

    //update the state of the current entity
    //returns true if the entity remains in the next frame, returns false if the entity is consumed
	public boolean update(List<Entity> entities, double deltaTime) {
		boolean result = true;
		if(!this.isZombie) { //for non-zombies
			Entity z = findClosestZombie(entities);
			//non-zombie is touching zombie
			if(z != null && isTouching(z)) {
				if(Math.random() < 0.8) { //80% chance for the non-zombie to get infected
					this.isZombie = true; 
				} else { //20% chance for the non-zombie to get consumed
					if(z.radius * 1.2 < 0.1) {
						z.radius = z.radius * 1.2; //zombie that consumed non-zombie grows in size
					}
					result = false;
				}
			//non-zombie is not touching zombie
			} else { 
				moveAwayFrom(z, 0.005); //run away from zombie
			}
		} 
		if(this.isZombie) { //for zombies and newly infected zombies
			Entity e = findClosestNonzombie(entities);
			moveToward(e, 0.006); //move towards closest non-zombie
			
		}
		//make sure entities do not go off the screen
		if(this.x > 1) {
			this.x = 0.99;
		} else if(this.x < 0) {
			this.x = 0.01;
		} else if (this.y > 1) {
			this.y = 0.99;
		} else if (this.y < 0) {
			this.y = 0.01;
		}
		return result;
	}
}
