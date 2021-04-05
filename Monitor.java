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
	private final String[] stateChop;
	private final String[] statePhil;
	private int philosWaitingToTalk = 0;
	/**
	 * Constructor
	 */
	public Monitor(int piNumberOfPhilosophers)
	{
		// There are as many chopsticks as there are philosophers
		chopsticks = new int[piNumberOfPhilosophers];
		// Every chopstick has a state (available, occupied)
		stateChop = new String[piNumberOfPhilosophers];
		// Every philosopher has a state (thinking, eating, hungry, talking)
		statePhil = new String[piNumberOfPhilosophers];
		for (int i = 0; i < piNumberOfPhilosophers; i++) {
			chopsticks[i] = i; // Each chopstick is numbered according to its index
			stateChop[i] = "available"; // Each chopstick begins as available
			statePhil[i] = "thinking"; // All philosophers begin by thinking
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
//		System.out.println("\n --- Philo " + piTID + " wants to pick Up.");
		statePhil[piTID - 1] = "hungry";

		// Philosopher first picks up the chopstick with the smallest index
		// If LEFT chopstick < RIGHT chopstick
		if (chopsticks[piTID-1] < chopsticks[piTID % stateChop.length]) {
			pickUpLeftChopstick(piTID);
			pickUpRightChopstick(piTID);
		}
		// Else RIGHT < LEFT
		else {
			pickUpRightChopstick(piTID);
			pickUpLeftChopstick(piTID);
		}
		statePhil[piTID - 1] = "eating";
	}


	/**
	 * When a given philosopher's done eating, they put the chopsticks/forks down
	 * and let others know they are available.
	 */
	public synchronized void putDown(final int piTID)
	{
//		int numPhilos = statePhil.length; // to remove at the end
		// Philosopher state goes back to thinking
		statePhil[piTID - 1] = "thinking";
		// Both chopsticks become available
		stateChop[piTID - 1] = "available"; // left
		stateChop[piTID % stateChop.length] = "available"; // right

//		System.out.println(" --- Philo " + piTID + " puts down." +
//				"\n     Her current state = " + statePhil[piTID-1]);
//		System.out.println("     Left Chop " + (piTID) + " state = " +
//				stateChop[piTID-1]);
//		System.out.println("     Right Chop " + (piTID % numPhilos+1) + " state = " +
//				stateChop[piTID % numPhilos]);
//
//		System.out.println("CALL TO NOTIFYALL()");
		// Notifies all hungry philosophers to check
		// for a new available chopstick
		notifyAll();
	}

	/**
	 * Only one philosopher at a time is allowed to philosophy
	 * (while she is not eating).
	 */
	public synchronized void requestTalk(int piTID)
	{
		// If anyone is already talking,
		// philosopher has to wait.
		for (String s : statePhil) {
			if (s.equals("talking")) {
				try {
					System.out.println(" ! ! Someone is already talking. Will wait.");
					philosWaitingToTalk++; // simple counter for calling notify in endTalk()
					wait();
				} catch (InterruptedException e) {
					System.out.println(" - interrupted exception :(");
				}
			}
		}
		// Philosopher cannot talk if she is eating
		if (!statePhil[piTID - 1].equals("eating"))
			statePhil[piTID - 1] = "talking";
	}

	/**
	 * When one philosopher is done talking stuff, others
	 * can feel free to start talking.
	 */
	public synchronized void endTalk(int piTID)
	{
		// Changes state to thinking
		statePhil[piTID - 1] = "thinking";
		// Notifies the next philosopher only
		// if one is waiting to talk
		if (philosWaitingToTalk > 0) {
			philosWaitingToTalk--;
//			System.out.println("CALL TO NOTIFY FROM ENDTALK");
			notify();
		}

	}

	/**
	 * Picks up the current philosopher's left chopstick
	 * @param piTID the piTID of the philosopher picking up
	 */
	public void pickUpLeftChopstick(int piTID) {
		if (stateChop[piTID - 1].equals("available")) {
			stateChop[piTID - 1] = "occupied";
//			System.out.println("     Philo " + piTID + " picked up left chopstick.");
		}
		else {
			while (!stateChop[piTID - 1].equals("available")) {
				try {
//					System.out.println(" --- Philo " + piTID + " can't pick up left Chopstick." +
//							"\n\t\tWill wait.");
					wait();
				} catch (InterruptedException e) {
					System.out.println(" - interrupted exception :(");
				}
			}
			stateChop[piTID - 1] = "occupied";
//			System.out.println("     Philo " + piTID + " finally picked up left chopstick.");
		}
	}

	/**
	 * Picks up the current philosopher's right chopstick
	 * @param piTID the piTID of the philosopher picking up
	 */
	public void pickUpRightChopstick(int piTID) {
		if (stateChop[piTID % stateChop.length].equals("available")) {
			stateChop[piTID % stateChop.length] = "occupied";
//			System.out.println("     Philo " + piTID + " picked up right chopstick.");
		}
		else {
			while (!stateChop[piTID % stateChop.length].equals("available")) {
				try {
//					System.out.println(" --- Philo " + piTID + " can't pick up right Chopstick." +
//							"\n\t\tWill wait.");
					wait();
				} catch (InterruptedException e) {
					System.out.println(" - interrupted exception :(");
				}
			}
			stateChop[piTID % stateChop.length] = "occupied";
//			System.out.println("     Philo " + piTID + " finally picked up right chopstick.");
		}
	}
}

// EOF
