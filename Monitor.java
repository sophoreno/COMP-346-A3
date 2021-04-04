/**
 * Class Monitor
 * To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Monitor
{
	/*
	 * ------------
	 * Data members
	 * ------------
	 */
	private final int[] chopsticks;
	//ReentrantLock key;
	private final String[] stateChop;
	private final String[] statePhil;
	private int justAte;
	//private final Condition[] self;
	/**
	 * Constructor
	 */
	public Monitor(int piNumberOfPhilosophers)
	{
		// Initializing the condition variable "self"
//		key = new ReentrantLock();
//		self = new Condition[piNumberOfPhilosophers];
//		for (int i = 0; i < self.length; i++)
//			self[i] = key.newCondition();

		// TODO: set appropriate number of chopsticks based on the # of philosophers
		// There are as many chopsticks as there are philosophers
		// Each chopstick is numbered according to its index
		chopsticks = new int[piNumberOfPhilosophers];
		// Each chopstick has a state (available, occupied)
		stateChop = new String[piNumberOfPhilosophers];
		// All philosophers begin by thinking
		statePhil = new String[piNumberOfPhilosophers];
		// The piTID of the last philosopher to have eaten
		// Starts at 0 since the first piTID is 1.
		justAte = 0;
		for (int i = 0; i < piNumberOfPhilosophers; i++) {
			chopsticks[i] = i;
			stateChop[i] = "available";
			statePhil[i] = "thinking";
		}
	}

	/*
	 * -------------------------------
	 * User-defined monitor procedures
	 * -------------------------------
	 */

	/**
	 * Grants request (returns) to eat when both chopsticks/forks are available.
	 * Else forces the philosopher to wait()
	 */
	public synchronized void pickUp(final int piTID) {
		System.out.println("\n --- Philo " + piTID + " wants to pick Up.");
		if (justAte == piTID) {
			System.out.println(" !! Philo " + piTID + " has just eaten. " +
					"\n    Will skip eating and go back to thinking.");
			return;
		}
		statePhil[piTID - 1] = "hungry";

		// We need to pick up the chopstick with the smallest index
		// If LEFT chopstick < RIGHT chopstick
		if (chopsticks[piTID-1] < chopsticks[piTID % stateChop.length]) {
//			pickUpChopstick(piTID-1); // left
//			pickUpChopstick(piTID % stateChop.length); // right

			pickUpLeftChopstick(piTID);
			pickUpRightChopstick(piTID);
		}
		// right is smaller
		else {
//			pickUpChopstick(piTID % stateChop.length); // right
//			pickUpChopstick(piTID-1); // left

			pickUpRightChopstick(piTID);
			pickUpLeftChopstick(piTID);
		}
		statePhil[piTID - 1] = "eating";
		justAte = piTID;
	}

	public void pickUpChopstick(int i) {
		if (stateChop[i].equals("available")) {
			stateChop[i] = "occupied";
			System.out.println("     Philo picked up chopstick " + (i+1));
		}
		else {
			try {
				System.out.println(" --- Philo can't pick up Chopstick " + (i+1) +
						"\n\t\tWill wait.");
				wait();
			} catch (InterruptedException e) {
				System.out.println(" - interrupted exception :(");
			} catch (IllegalMonitorStateException e) {
				System.out.println(" - illegal monitor state exception :(");
			}
			stateChop[i] = "occupied";
			System.out.println("     Philo finally picked up chopstick " + (i+1));
		}
	}

	/**
	 * When a given philosopher's done eating, they put the chopsticks/forks down
	 * and let others know they are available.
	 */
	public synchronized void putDown(final int piTID)
	{
		int numPhilos = statePhil.length;
		statePhil[piTID - 1] = "thinking";
		stateChop[piTID - 1] = "available"; // left
		stateChop[piTID % numPhilos] = "available"; // right

		System.out.println(" --- Philo " + piTID + " puts down." +
				"\n     Her current state = " + statePhil[piTID-1]);
		System.out.println("     Left Chop " + (piTID) + " state = " +
				stateChop[piTID-1]);
		System.out.println("     Right Chop " + (piTID % numPhilos+1) + " state = " +
				stateChop[piTID % numPhilos]);


		// test if either neighbor can eat
		//System.out.println("     Philo " + piTID + " will check her neighbors.");
		//testIfCanEat((piTID-1 + numPhilos-1) % numPhilos); // left
		//testIfCanEat((piTID) % numPhilos); // right

		notify(); // ??? notify that you're done eating ????
	}

	/**
	 * Only one philosopher at a time is allowed to philosophy
	 * (while she is not eating).
	 */
	public synchronized void requestTalk(int piTID)
	{
		// If anyone is already talking,
		// philosopher has to wait.
//		for (String s : statePhil) {
//			if (s.equals("talking")) {
//				try {
//					wait();
//				} catch (InterruptedException e) {
//					System.out.println(" - interrupted exception :(");
//				} catch (IllegalMonitorStateException e) {
//					System.out.println(" - illegal monitor state exception :(");
//				}
//			}
//		}
//		// Philosopher cannot talk if she is eating
//		if (!statePhil[piTID - 1].equals("eating"))
//			statePhil[piTID - 1] = "talking";
	}

	/**
	 * When one philosopher is done talking stuff, others
	 * can feel free to start talking.
	 */
	public synchronized void endTalk(int piTID)
	{
//		// Changes state to thinking
//		statePhil[piTID - 1] = "thinking";
		// Signals the next philosopher
//		notify();
	}

	/**
	 * Tests whether both chopsticks are available.
	 * Philosophers starts eating if so and signals the next waiting thread.
	 * @param i the philosopher's index in the state array
	 */
	public void testIfCanEat(int i) {
		int numPhilos = statePhil.length;

		// If NOT hungry, return
		if (!statePhil[i].equals("hungry")) {
			System.out.println(" !!! Philo " + (i+1) + " isn't hungry.");
			return;
		}

		System.out.println("\n --> TEST Left neighbor " + ((i + numPhilos-1) % numPhilos + 1) +
				"'s current state is " + statePhil[(i + numPhilos-1) % numPhilos]);
		System.out.println("          Right neighbor " + ((i + 1) % numPhilos + 1) +
				"'s current state is " + statePhil[(i + 1) % numPhilos]);
		System.out.println(" --> TEST Left Chop " + (i+1) + " state = " + stateChop[i]);
		System.out.println("          Right Chop " + ((i+1) % numPhilos + 1) +
				" state = " + stateChop[(i+1) % numPhilos] + "\n");

		// If LEFT < RIGHT, the left chopstick is the first to be picked up
		// We need to check if the right one is free
		if (chopsticks[i] < chopsticks[(i+1) % stateChop.length]) {
			// If right chopstick is occupied, return
			if (stateChop[(i+1) % numPhilos].equals("occupied")) {
				System.out.println("\n !! Right Chopstick cannot be picked up\n");
				return;
			}
			// If left neighbor is eating, return
			if (statePhil[(i + numPhilos-1) % numPhilos].equals("eating")) {
				System.out.println(" !! Left neighbor is eating, can't eat.");
				return;
			}
			// If left chopstick is available, pick it up
			// If it isn't, that's because it has already been picked up
			if (stateChop[i].equals("available"))
				pickUpChopstick(i); // left
			// Then pick up right chopstick
			pickUpChopstick((i+1) % numPhilos); // right
		}
		// Else, the right chopstick is the first to be picked up
		// We need to check if the left one is free
		else {
			// If left chopstick is occupied, return
			if (stateChop[i].equals("occupied")) {
				System.out.println("\n !! Left Chopstick cannot be picked up\n");
				return;
			}
			// If right neighbor is eating, return
			if (statePhil[(i + 1) % numPhilos].equals("eating")) {
				System.out.println(" !! Right neighbor is eating, can't eat.");
				return;
			}
			// If right chopstick is available, pick it up
			// If it isn't, that's because it has already been picked up
			if (stateChop[(i+1) % numPhilos].equals("available"))
				pickUpChopstick((i+1) % numPhilos); // right
			// Then pick up left chopstick
			pickUpChopstick(i); // left
		}
		statePhil[i] = "eating";
	}


	// unused
	public void pickUpLeftChopstick(int piTID) {
		if (stateChop[piTID - 1].equals("available")
				&& piTID == 1 // it's the philosopher with piTID = 1
				&& statePhil[statePhil.length-1].equals("hungry")) { // left neighbor is hungry

			try {
				System.out.println(" ! ! Philo " + piTID + " can't pick up left Chopstick because left neighbor is waiting for it." +
						"\n\t\tWill wait.");
				wait();
			} catch (InterruptedException e) {
				System.out.println(" - interrupted exception :(");
			}
			stateChop[piTID - 1] = "occupied";
			System.out.println("     Philo " + piTID + " finally picked up left chopstick.");
		}

		else if (stateChop[piTID - 1].equals("available")) {
			stateChop[piTID - 1] = "occupied";
			System.out.println("     Philo " + piTID + " picked up left chopstick.");
		}
		else {
			try {
				System.out.println(" --- Philo " + piTID + " can't pick up left Chopstick." +
						"\n\t\tWill wait.");
				wait();
			} catch (InterruptedException e) {
				System.out.println(" - interrupted exception :(");
			} catch (IllegalMonitorStateException e) {
				System.out.println(" - illegal monitor state exception :(");
			}
			stateChop[piTID - 1] = "occupied";
			System.out.println("     Philo " + piTID + " finally picked up left chopstick.");
		}
	}
	public void pickUpRightChopstick(int piTID) {
		if (stateChop[piTID % stateChop.length].equals("available")
				&& piTID == stateChop.length // it's the philosopher with piTID = N
				&& statePhil[piTID % stateChop.length].equals("hungry")) { // right neighbor is hungry

			try {
				System.out.println(" ! ! Philo " + piTID + " can't pick up right Chopstick because right neighbor is waiting for it." +
						"\n\t\tWill wait.");
				wait();
			} catch (InterruptedException e) {
				System.out.println(" - interrupted exception :(");
			}
			stateChop[piTID % stateChop.length] = "occupied";
			System.out.println("     Philo " + piTID + " finally picked up right chopstick.");
		}
		else if (stateChop[piTID % stateChop.length].equals("available")) {
			stateChop[piTID % stateChop.length] = "occupied";
			System.out.println("     Philo " + piTID + " picked up right chopstick.");
		}
		else {
			try {
				System.out.println(" --- Philo " + piTID + " can't pick up right Chopstick." +
						"\n\t\tWill wait.");
				wait();
			} catch (InterruptedException e) {
				System.out.println(" - interrupted exception :(");
			} catch (IllegalMonitorStateException e) {
				System.out.println(" - illegal monitor state exception :(");
			}
			stateChop[piTID % stateChop.length] = "occupied";
			System.out.println("     Philo " + piTID + " finally picked up right chopstick.");
		}
	}
}

// EOF
