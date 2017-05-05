package pipe.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import pipe.dataLayer.DataLayer;
import pipe.dataLayer.Transition;

/**
 * This class is used to process clicks by the user to manually step through
 * enabled transitions in the net.
 * 
 * @author unspecified wrote this code
 * @author David Patterson fixed a bug with double-firing transitions in the
 *         doRandomFiring method. I also renamed the fireTransition method to
 *         recordFiredTransition to better describe what it does.
 * 
 * @author Pere Bonet modified the recordFiredTransition method to fix the
 *         unexcepted behaviour observed during animation playback. The method
 *         is renamed back to fireTransition.
 * 
 * @author Edwin Chung fixed the bug where users can still step forward to
 *         previous firing sequence even though it has been reset. The issue
 *         where an unexpected behaviour will occur when the firing sequence has
 *         been altered has been resolved. The problem where animation will
 *         freeze halfway while stepping back a firing sequence has also been
 *         fixed (Feb 2007)
 * 
 * @author Dave Patterson The code now outputs an error message in the status
 *         bar if there is no transition to be found when picking a random
 *         transition to fire. This is related to the problem described in bug
 *         1699546.
 */
public class Animator {

	Timer timer;
	int numberSequences;
	public static ArrayList firedTransitions;
	public static int count = 0;

	public Animator() {
		firedTransitions = new ArrayList();

		timer = new Timer(0, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if ((getNumberSequences() < 1)
						|| !CreateGui.getView().isInAnimationMode()) {
					timer.stop();
					CreateGui.getApp().setRandomAnimationMode(false);
					return;
				}
				doRandomFiring();
				setNumberSequences(getNumberSequences() - 1);
			}
		});
	}

	/**
	 * Highlights enabled transitions
	 */
	public void highlightEnabledTransitions() {
		/* rewritten by wjk 03/10/2007 */
		DataLayer current = CreateGui.currentPNMLData();

		// current.setEnabledTransitions();

		Iterator transitionIterator = current.returnTransitions();
		while (transitionIterator.hasNext()) {
			Transition tempTransition = (Transition) transitionIterator.next();
			if (tempTransition.isEnabled(true) == true) {
				current.notifyObservers();
				tempTransition.repaint();
			}
		}
	}

	/**
	 * Called during animation to unhighlight previously highlighted transitions
	 */
	public void unhighlightDisabledTransitions() {
		DataLayer current = CreateGui.currentPNMLData();

		// current.setEnabledTransitions();

		Iterator transitionIterator = current.returnTransitions();
		while (transitionIterator.hasNext()) {
			Transition tempTransition = (Transition) transitionIterator.next();
			if (tempTransition.isEnabled(true) == false) {
				current.notifyObservers();
				tempTransition.repaint();
			}
		}
	}

	/**
	 * Called at end of animation and resets all Transitions to false and
	 * unhighlighted
	 */
	private void disableTransitions() {
		Iterator transitionIterator = CreateGui.currentPNMLData()
				.returnTransitions();
		while (transitionIterator.hasNext()) {
			Transition tempTransition = (Transition) transitionIterator.next();
			tempTransition.setEnabledFalse();
			CreateGui.currentPNMLData().notifyObservers();
			tempTransition.repaint();
		}
	}

	/**
	 * Stores model at start of animation
	 */
	public void storeModel() {
		CreateGui.currentPNMLData().storeState();
	}

	/**
	 * Restores model at end of animation and sets all transitions to false and
	 * unhighlighted
	 */
	public void restoreModel() {
		CreateGui.currentPNMLData().restoreState();
		disableTransitions();
		count = 0;
	}

	public void startRandomFiring() {
		if (getNumberSequences() > 0) {
			// stop animation
			setNumberSequences(0);
		} else {
			try {
				String s = JOptionPane.showInputDialog(
						"Enter number of firings to perform", "1");
				this.numberSequences = Integer.parseInt(s);
				s = JOptionPane.showInputDialog(
						"Enter time delay between firing /ms", "50");
				timer.setDelay(Integer.parseInt(s));
				timer.start();
			} catch (NumberFormatException e) {
				CreateGui.getApp().setRandomAnimationMode(false);
			}
		}
	}

	public void stopRandomFiring() {
		numberSequences = 0;
	}

	/**
	 * This method randomly fires one of the enabled transitions. It then
	 * records the information about this by calling the recordFiredTransition
	 * method.
	 * 
	 * @author Dave Patterson Apr 29, 2007 I changed the code to keep the random
	 *         transition found by the DataLayer. If it is not null, I call the
	 *         fireTransition method, otherwise I put out an error message in
	 *         the status bar.
	 */
	public void doRandomFiring() {
		DataLayer data = CreateGui.currentPNMLData();
		Transition t = data.fireRandomTransition(); // revisar
		// CreateGui.getAnimationHistory().clearStepsForward(); //ok - igual
		// removeStoredTransitions(); //ok - igual
		if (t != null) {
			fireTransition(t); // revisar
			// unhighlightDisabledTransitions();
			// highlightEnabledTransitions();
		} else {
			CreateGui.getApp().getStatusBar().changeText(
					"ERROR: No transition to fire.");
		}
	}

	/**
	 * Steps back through previously fired transitions
	 */
	public void stepBack() {
		if (count > 0) {
			Transition lastTransition = (Transition) firedTransitions
					.get(--count);
			CreateGui.currentPNMLData().fireTransitionBackwards(lastTransition);
			CreateGui.currentPNMLData().setEnabledTransitions();
			unhighlightDisabledTransitions();
			highlightEnabledTransitions();
		}
	}

	/**
	 * Steps forward through previously fired transitions
	 */
	public void stepForward() {
		if (count < firedTransitions.size()) {
			Transition nextTransition = (Transition) firedTransitions
					.get(count++);
			CreateGui.currentPNMLData().fireTransition(nextTransition);
			CreateGui.currentPNMLData().setEnabledTransitions();
			unhighlightDisabledTransitions();
			highlightEnabledTransitions();
		}
	}

	/**
	 * This method keeps track of a fired transition in the AnimationHistory
	 * object, enables transitions after the recent firing, and properly
	 * displays the transitions.
	 * 
	 * @author David Patterson renamed this method and changed the
	 *         AnimationHandler to make it fire the transition before calling
	 *         this method. This prevents double-firing a transition.
	 * 
	 * @author Pere Bonet modified this method so that it now stores transitions
	 *         that has just been fired in an array so that it can be accessed
	 *         during backwards and stepping to fix the unexcepted behaviour
	 *         observed during animation playback. The method is renamed back to
	 *         fireTransition.
	 */
	public void fireTransition(Transition transition) {
		Animator animator = CreateGui.getAnimator();

		CreateGui.getAnimationHistory().addHistoryItem(transition.getName());
		CreateGui.currentPNMLData().fireTransition(transition);
		CreateGui.currentPNMLData().setEnabledTransitions();
		animator.highlightEnabledTransitions();
		animator.unhighlightDisabledTransitions();
		if (count == firedTransitions.size()) {
			firedTransitions.add(transition);
			count++;
		} else {
			removeStoredTransitions(count + 1);
			firedTransitions.set(count++, transition);

		}
	}

	private void removeStoredTransitions(int start) {
		for (int i = start; i < firedTransitions.size(); i++) {
			firedTransitions.remove(i);
		}
	}

	public synchronized int getNumberSequences() {
		return numberSequences;
	}

	public synchronized void setNumberSequences(int numberSequences) {
		this.numberSequences = numberSequences;
	}

}
