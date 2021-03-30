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
	public int[] chopsticks;
	//ReentrantLock key;
	private final String[] stateChop;
	private final String[] statePhil;
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
		// Each chopstick has a state (available, occupied)
		chopsticks = new int[piNumberOfPhilosophers];
		stateChop = new String[piNumberOfPhilosophers];
		for (int i = 0; i < chopsticks.length; i++) {
			chopsticks[i] = i;
			stateChop[i] = "available";
		}

		// All philosophers begin by thinking
		statePhil = new String[piNumberOfPhilosophers];
		for (int i = 0; i < piNumberOfPhilosophers; i++)
			statePhil[i] = "thinking";

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
		System.out.println("\n --- Philo " + piTID + " wants to pick Up." +
				"\n     Her current state is " + statePhil[piTID - 1]);

		statePhil[piTID - 1] = "hungry";

		System.out.println(" --- Philo " + piTID + "'s current state is " + statePhil[piTID - 1]);

		test(piTID - 1);
		if (!statePhil[piTID - 1].equals("eating")) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.out.println(" - interrupted exception :(");
			} catch (IllegalMonitorStateException e) {
				System.out.println(" - illegal monitor state exception :(");
			}
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
				"\n     Her current state is " + statePhil[piTID-1]);
		System.out.println("     Left chopstick " + (piTID-1) + " state is " +
				stateChop[piTID-1]);
		System.out.println("     Right chopstick " + (piTID % numPhilos) + " state is " +
				stateChop[piTID % numPhilos]);

		// test if left neighbor can eat
		test((piTID-1 + numPhilos-1) % numPhilos);
		// test if right neighbor can eat
		test((piTID) % numPhilos); // (piTID-1 + 1) --> piTID
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
					wait();
				} catch (InterruptedException e) {
					System.out.println(" - interrupted exception :(");
				} catch (IllegalMonitorStateException e) {
					System.out.println(" - illegal monitor state exception :(");
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
		// Signals to others that they can try talking
		notifyAll();
	}

	/**
	 * Tests whether both chopsticks are available.
	 * Philosophers starts eating if so and signals the next waiting thread.
	 * @param i the philosopher's index in the state array
	 */
	public synchronized void test(int i) {
		int numPhilos = statePhil.length;

		System.out.println(" --- TEST Left neighbor " + ((i + numPhilos-1) % numPhilos + 1) +
				"'s current state is " + statePhil[(i + numPhilos-1) % numPhilos]);
		System.out.println("          Left chopstick " + (i+1) +
				"'s current state is " + stateChop[i]);
		System.out.println(" --- TEST Right neighbor " + ((i + 1) % numPhilos + 1) +
				"'s current state is " + statePhil[(i + 1) % numPhilos]);
		System.out.println("          Right chopstick " + ((i+1) % numPhilos + 1) +
				"'s current state is " + stateChop[(i+1) % numPhilos] + "\n");

		if (stateChop[i].equals("available") // left chopstick is free
			&& stateChop[(i+1) % numPhilos].equals("available") // right chopstick is free
			&& statePhil[i].equals("hungry")) // philosopher wants to eat
		{
			// Change both chopsticks' states to occupied
			stateChop[i] = "occupied"; // left
			stateChop[(i+1) % numPhilos] = "occupied"; // right
			// Change the philosopher's state to eating
			statePhil[i] = "eating";

			System.out.println(" --- Philo " + (i+1) + "' state is now " + statePhil[i]);
			System.out.println("     Chopstick "+ (i+1) // left chopstick
					+ " state is " + stateChop[i]);
			System.out.println("     Chopstick "+ ((i+1) % numPhilos + 1) // right chopstick
					+ " state is " +stateChop[(i+1) % numPhilos]);
		}
		else {
			System.out.println(" --- Philo " + (i+1) + " cant eat right now." +
					"\n     Her state stays " + statePhil[i]);
		}
		// Philosopher signals a waiting philosopher
		// that they can take action
		notifyAll();

	}
}

// EOF
