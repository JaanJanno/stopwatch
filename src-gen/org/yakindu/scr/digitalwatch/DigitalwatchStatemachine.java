package org.yakindu.scr.digitalwatch;
import org.yakindu.scr.ITimer;

public class DigitalwatchStatemachine implements IDigitalwatchStatemachine {

	private final boolean[] timeEvents = new boolean[12];

	private final class SCIButtonsImpl implements SCIButtons {

		private boolean topLeftPressed;

		public void raiseTopLeftPressed() {
			topLeftPressed = true;
		}

		private boolean topLeftReleased;

		public void raiseTopLeftReleased() {
			topLeftReleased = true;
		}

		private boolean topRightPressed;

		public void raiseTopRightPressed() {
			topRightPressed = true;
		}

		private boolean topRightReleased;

		public void raiseTopRightReleased() {
			topRightReleased = true;
		}

		private boolean bottomLeftPressed;

		public void raiseBottomLeftPressed() {
			bottomLeftPressed = true;
		}

		private boolean bottomLeftReleased;

		public void raiseBottomLeftReleased() {
			bottomLeftReleased = true;
		}

		private boolean bottomRightPressed;

		public void raiseBottomRightPressed() {
			bottomRightPressed = true;
		}

		private boolean bottomRightReleased;

		public void raiseBottomRightReleased() {
			bottomRightReleased = true;
		}

		public void clearEvents() {
			topLeftPressed = false;
			topLeftReleased = false;
			topRightPressed = false;
			topRightReleased = false;
			bottomLeftPressed = false;
			bottomLeftReleased = false;
			bottomRightPressed = false;
			bottomRightReleased = false;
		}

	}

	private SCIButtonsImpl sCIButtons;
	private final class SCIDisplayImpl implements SCIDisplay {

		private SCIDisplayOperationCallback operationCallback;

		public void setSCIDisplayOperationCallback(
				SCIDisplayOperationCallback operationCallback) {
			this.operationCallback = operationCallback;
		}

	}

	private SCIDisplayImpl sCIDisplay;
	private final class SCILogicUnitImpl implements SCILogicUnit {

		private SCILogicUnitOperationCallback operationCallback;

		public void setSCILogicUnitOperationCallback(
				SCILogicUnitOperationCallback operationCallback) {
			this.operationCallback = operationCallback;
		}

		private boolean startAlarm;

		public void raiseStartAlarm() {
			startAlarm = true;
		}

		public void clearEvents() {
			startAlarm = false;
		}

	}

	private SCILogicUnitImpl sCILogicUnit;
	private final class SCIHelperImpl implements SCIHelper {

		private boolean isEditingTime;
		public boolean getIsEditingTime() {
			return isEditingTime;
		}

		public void setIsEditingTime(boolean value) {
			this.isEditingTime = value;
		}

	}

	private SCIHelperImpl sCIHelper;

	public enum State {
		main_region_digitalwatch, main_region_digitalwatch_timeCounting_Counting, main_region_digitalwatch_chronoCounting_Inactive, main_region_digitalwatch_chronoCounting_Active, main_region_digitalwatch_displayRefreshing_showTime, main_region_digitalwatch_displayRefreshing_showChrono, main_region_digitalwatch_displayRefreshing_waitTimeEdit, main_region_digitalwatch_displayRefreshing_editMode, main_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink1, main_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink2, main_region_digitalwatch_displayRefreshing_waitAlarmEdit, main_region_digitalwatch_displayRefreshing_switchSelect, main_region_digitalwatch_displayRefreshing_increaseSelection, main_region_digitalwatch_displayGlowing_GlowOff, main_region_digitalwatch_displayGlowing_GlowOn, main_region_digitalwatch_displayGlowing_GlowDelay, $NullState$
	};

	private final State[] stateVector = new State[4];

	private int nextStateIndex;

	private ITimer timer;

	static {
	}

	public DigitalwatchStatemachine() {

		sCIButtons = new SCIButtonsImpl();
		sCIDisplay = new SCIDisplayImpl();
		sCILogicUnit = new SCILogicUnitImpl();
		sCIHelper = new SCIHelperImpl();
	}

	public void init() {
		if (timer == null) {
			throw new IllegalStateException("timer not set.");
		}
		for (int i = 0; i < 4; i++) {
			stateVector[i] = State.$NullState$;
		}

		clearEvents();
		clearOutEvents();

		sCIHelper.isEditingTime = false;
	}

	public void enter() {
		if (timer == null) {
			throw new IllegalStateException("timer not set.");
		}
		entryAction();

		sCIHelper.isEditingTime = false;

		timer.setTimer(this, 0, 1 * 1000, false);

		sCILogicUnit.operationCallback.increaseTimeByOne();

		nextStateIndex = 0;
		stateVector[0] = State.main_region_digitalwatch_timeCounting_Counting;

		nextStateIndex = 1;
		stateVector[1] = State.main_region_digitalwatch_chronoCounting_Inactive;

		timer.setTimer(this, 2, 1 * 1000, false);

		sCIDisplay.operationCallback.refreshTimeDisplay();

		sCIDisplay.operationCallback.refreshDateDisplay();

		sCIDisplay.operationCallback.refreshAlarmDisplay();

		sCIHelper.isEditingTime = false;

		nextStateIndex = 2;
		stateVector[2] = State.main_region_digitalwatch_displayRefreshing_showTime;

		sCIDisplay.operationCallback.unsetIndiglo();

		nextStateIndex = 3;
		stateVector[3] = State.main_region_digitalwatch_displayGlowing_GlowOff;
	}

	public void exit() {
		switch (stateVector[0]) {
			case main_region_digitalwatch_timeCounting_Counting :
				nextStateIndex = 0;
				stateVector[0] = State.$NullState$;

				timer.unsetTimer(this, 0);
				break;

			default :
				break;
		}

		switch (stateVector[1]) {
			case main_region_digitalwatch_chronoCounting_Inactive :
				nextStateIndex = 1;
				stateVector[1] = State.$NullState$;
				break;

			case main_region_digitalwatch_chronoCounting_Active :
				nextStateIndex = 1;
				stateVector[1] = State.$NullState$;

				timer.unsetTimer(this, 1);
				break;

			default :
				break;
		}

		switch (stateVector[2]) {
			case main_region_digitalwatch_displayRefreshing_showTime :
				nextStateIndex = 2;
				stateVector[2] = State.$NullState$;

				timer.unsetTimer(this, 2);
				break;

			case main_region_digitalwatch_displayRefreshing_showChrono :
				nextStateIndex = 2;
				stateVector[2] = State.$NullState$;

				timer.unsetTimer(this, 3);
				break;

			case main_region_digitalwatch_displayRefreshing_waitTimeEdit :
				nextStateIndex = 2;
				stateVector[2] = State.$NullState$;

				timer.unsetTimer(this, 4);
				break;

			case main_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink1 :
				nextStateIndex = 2;
				stateVector[2] = State.$NullState$;

				timer.unsetTimer(this, 6);

				timer.unsetTimer(this, 5);
				break;

			case main_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink2 :
				nextStateIndex = 2;
				stateVector[2] = State.$NullState$;

				timer.unsetTimer(this, 7);

				timer.unsetTimer(this, 5);
				break;

			case main_region_digitalwatch_displayRefreshing_waitAlarmEdit :
				nextStateIndex = 2;
				stateVector[2] = State.$NullState$;

				timer.unsetTimer(this, 8);
				break;

			case main_region_digitalwatch_displayRefreshing_switchSelect :
				nextStateIndex = 2;
				stateVector[2] = State.$NullState$;

				timer.unsetTimer(this, 9);
				break;

			case main_region_digitalwatch_displayRefreshing_increaseSelection :
				nextStateIndex = 2;
				stateVector[2] = State.$NullState$;

				timer.unsetTimer(this, 10);
				break;

			default :
				break;
		}

		switch (stateVector[3]) {
			case main_region_digitalwatch_displayGlowing_GlowOff :
				nextStateIndex = 3;
				stateVector[3] = State.$NullState$;
				break;

			case main_region_digitalwatch_displayGlowing_GlowOn :
				nextStateIndex = 3;
				stateVector[3] = State.$NullState$;
				break;

			case main_region_digitalwatch_displayGlowing_GlowDelay :
				nextStateIndex = 3;
				stateVector[3] = State.$NullState$;

				timer.unsetTimer(this, 11);
				break;

			default :
				break;
		}

		exitAction();
	}

	/**
	 * This method resets the incoming events (time events included).
	 */
	protected void clearEvents() {
		sCIButtons.clearEvents();
		sCILogicUnit.clearEvents();

		for (int i = 0; i < timeEvents.length; i++) {
			timeEvents[i] = false;
		}
	}

	/**
	 * This method resets the outgoing events.
	 */
	protected void clearOutEvents() {
	}

	/**
	 * Returns true if the given state is currently active otherwise false.
	 */
	public boolean isStateActive(State state) {
		switch (state) {
			case main_region_digitalwatch :
				return stateVector[0].ordinal() >= State.main_region_digitalwatch
						.ordinal()
						&& stateVector[0].ordinal() <= State.main_region_digitalwatch_displayGlowing_GlowDelay
								.ordinal();
			case main_region_digitalwatch_timeCounting_Counting :
				return stateVector[0] == State.main_region_digitalwatch_timeCounting_Counting;
			case main_region_digitalwatch_chronoCounting_Inactive :
				return stateVector[1] == State.main_region_digitalwatch_chronoCounting_Inactive;
			case main_region_digitalwatch_chronoCounting_Active :
				return stateVector[1] == State.main_region_digitalwatch_chronoCounting_Active;
			case main_region_digitalwatch_displayRefreshing_showTime :
				return stateVector[2] == State.main_region_digitalwatch_displayRefreshing_showTime;
			case main_region_digitalwatch_displayRefreshing_showChrono :
				return stateVector[2] == State.main_region_digitalwatch_displayRefreshing_showChrono;
			case main_region_digitalwatch_displayRefreshing_waitTimeEdit :
				return stateVector[2] == State.main_region_digitalwatch_displayRefreshing_waitTimeEdit;
			case main_region_digitalwatch_displayRefreshing_editMode :
				return stateVector[2].ordinal() >= State.main_region_digitalwatch_displayRefreshing_editMode
						.ordinal()
						&& stateVector[2].ordinal() <= State.main_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink2
								.ordinal();
			case main_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink1 :
				return stateVector[2] == State.main_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink1;
			case main_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink2 :
				return stateVector[2] == State.main_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink2;
			case main_region_digitalwatch_displayRefreshing_waitAlarmEdit :
				return stateVector[2] == State.main_region_digitalwatch_displayRefreshing_waitAlarmEdit;
			case main_region_digitalwatch_displayRefreshing_switchSelect :
				return stateVector[2] == State.main_region_digitalwatch_displayRefreshing_switchSelect;
			case main_region_digitalwatch_displayRefreshing_increaseSelection :
				return stateVector[2] == State.main_region_digitalwatch_displayRefreshing_increaseSelection;
			case main_region_digitalwatch_displayGlowing_GlowOff :
				return stateVector[3] == State.main_region_digitalwatch_displayGlowing_GlowOff;
			case main_region_digitalwatch_displayGlowing_GlowOn :
				return stateVector[3] == State.main_region_digitalwatch_displayGlowing_GlowOn;
			case main_region_digitalwatch_displayGlowing_GlowDelay :
				return stateVector[3] == State.main_region_digitalwatch_displayGlowing_GlowDelay;
			default :
				return false;
		}
	}

	/**
	 * Set the {@link ITimer} for the state machine. It must be set
	 * externally on a timed state machine before a run cycle can be correct
	 * executed.
	 * 
	 * @param timer
	 */
	public void setTimer(ITimer timer) {
		this.timer = timer;
	}

	/**
	 * Returns the currently used timer.
	 * 
	 * @return {@link ITimer}
	 */
	public ITimer getTimer() {
		return timer;
	}

	public void timeElapsed(int eventID) {
		timeEvents[eventID] = true;
	}

	public SCIButtons getSCIButtons() {
		return sCIButtons;
	}
	public SCIDisplay getSCIDisplay() {
		return sCIDisplay;
	}
	public SCILogicUnit getSCILogicUnit() {
		return sCILogicUnit;
	}
	public SCIHelper getSCIHelper() {
		return sCIHelper;
	}

	/* Entry action for statechart 'digitalwatch'. */
	private void entryAction() {
	}

	/* Exit action for state 'digitalwatch'. */
	private void exitAction() {
	}

	/* The reactions of state Counting. */
	private void reactMain_region_digitalwatch_timeCounting_Counting() {
		if ((timeEvents[0]) && !sCIHelper.isEditingTime) {
			nextStateIndex = 0;
			stateVector[0] = State.$NullState$;

			timer.unsetTimer(this, 0);

			timer.setTimer(this, 0, 1 * 1000, false);

			sCILogicUnit.operationCallback.increaseTimeByOne();

			nextStateIndex = 0;
			stateVector[0] = State.main_region_digitalwatch_timeCounting_Counting;
		}
	}

	/* The reactions of state Inactive. */
	private void reactMain_region_digitalwatch_chronoCounting_Inactive() {
		if ((sCIButtons.bottomRightPressed)
				&& isStateActive(State.main_region_digitalwatch_displayRefreshing_showChrono)) {
			nextStateIndex = 1;
			stateVector[1] = State.$NullState$;

			timer.setTimer(this, 1, 1 * 1000, false);

			nextStateIndex = 1;
			stateVector[1] = State.main_region_digitalwatch_chronoCounting_Active;
		}
	}

	/* The reactions of state Active. */
	private void reactMain_region_digitalwatch_chronoCounting_Active() {
		if (timeEvents[1]) {
			nextStateIndex = 1;
			stateVector[1] = State.$NullState$;

			timer.unsetTimer(this, 1);

			sCILogicUnit.operationCallback.increaseChronoByOne();

			timer.setTimer(this, 1, 1 * 1000, false);

			nextStateIndex = 1;
			stateVector[1] = State.main_region_digitalwatch_chronoCounting_Active;
		} else {
			if ((sCIButtons.bottomRightPressed)
					&& isStateActive(State.main_region_digitalwatch_displayRefreshing_showChrono)) {
				nextStateIndex = 1;
				stateVector[1] = State.$NullState$;

				timer.unsetTimer(this, 1);

				nextStateIndex = 1;
				stateVector[1] = State.main_region_digitalwatch_chronoCounting_Inactive;
			}
		}
	}

	/* The reactions of state showTime. */
	private void reactMain_region_digitalwatch_displayRefreshing_showTime() {
		if (timeEvents[2]) {
			nextStateIndex = 2;
			stateVector[2] = State.$NullState$;

			timer.unsetTimer(this, 2);

			timer.setTimer(this, 2, 1 * 1000, false);

			sCIDisplay.operationCallback.refreshTimeDisplay();

			sCIDisplay.operationCallback.refreshDateDisplay();

			sCIDisplay.operationCallback.refreshAlarmDisplay();

			sCIHelper.isEditingTime = false;

			nextStateIndex = 2;
			stateVector[2] = State.main_region_digitalwatch_displayRefreshing_showTime;
		} else {
			if (sCIButtons.topLeftPressed) {
				nextStateIndex = 2;
				stateVector[2] = State.$NullState$;

				timer.unsetTimer(this, 2);

				timer.setTimer(this, 3, 1 * 1000, false);

				nextStateIndex = 2;
				stateVector[2] = State.main_region_digitalwatch_displayRefreshing_showChrono;
			} else {
				if (sCIButtons.bottomRightPressed) {
					nextStateIndex = 2;
					stateVector[2] = State.$NullState$;

					timer.unsetTimer(this, 2);

					timer.setTimer(this, 4, 1500, false);

					nextStateIndex = 2;
					stateVector[2] = State.main_region_digitalwatch_displayRefreshing_waitTimeEdit;
				} else {
					if (sCIButtons.bottomLeftPressed) {
						nextStateIndex = 2;
						stateVector[2] = State.$NullState$;

						timer.unsetTimer(this, 2);

						sCILogicUnit.operationCallback.setAlarm();

						timer.setTimer(this, 8, 1500, false);

						nextStateIndex = 2;
						stateVector[2] = State.main_region_digitalwatch_displayRefreshing_waitAlarmEdit;
					}
				}
			}
		}
	}

	/* The reactions of state showChrono. */
	private void reactMain_region_digitalwatch_displayRefreshing_showChrono() {
		if (timeEvents[3]) {
			nextStateIndex = 2;
			stateVector[2] = State.$NullState$;

			timer.unsetTimer(this, 3);

			sCIDisplay.operationCallback.refreshChronoDisplay();

			timer.setTimer(this, 3, 1 * 1000, false);

			nextStateIndex = 2;
			stateVector[2] = State.main_region_digitalwatch_displayRefreshing_showChrono;
		} else {
			if (sCIButtons.topLeftPressed) {
				nextStateIndex = 2;
				stateVector[2] = State.$NullState$;

				timer.unsetTimer(this, 3);

				timer.setTimer(this, 2, 1 * 1000, false);

				sCIDisplay.operationCallback.refreshTimeDisplay();

				sCIDisplay.operationCallback.refreshDateDisplay();

				sCIDisplay.operationCallback.refreshAlarmDisplay();

				sCIHelper.isEditingTime = false;

				nextStateIndex = 2;
				stateVector[2] = State.main_region_digitalwatch_displayRefreshing_showTime;
			} else {
				if (sCIButtons.bottomLeftPressed) {
					nextStateIndex = 2;
					stateVector[2] = State.$NullState$;

					timer.unsetTimer(this, 3);

					sCILogicUnit.operationCallback.resetChrono();

					timer.setTimer(this, 3, 1 * 1000, false);

					nextStateIndex = 2;
					stateVector[2] = State.main_region_digitalwatch_displayRefreshing_showChrono;
				}
			}
		}
	}

	/* The reactions of state waitTimeEdit. */
	private void reactMain_region_digitalwatch_displayRefreshing_waitTimeEdit() {
		if (timeEvents[4]) {
			nextStateIndex = 2;
			stateVector[2] = State.$NullState$;

			timer.unsetTimer(this, 4);

			sCILogicUnit.operationCallback.startTimeEditMode();

			sCIHelper.isEditingTime = true;

			timer.setTimer(this, 5, 5 * 1000, false);

			timer.setTimer(this, 6, 500, false);

			sCIDisplay.operationCallback.showSelection();

			nextStateIndex = 2;
			stateVector[2] = State.main_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink1;
		} else {
			if (sCIButtons.bottomRightReleased) {
				nextStateIndex = 2;
				stateVector[2] = State.$NullState$;

				timer.unsetTimer(this, 4);

				timer.setTimer(this, 2, 1 * 1000, false);

				sCIDisplay.operationCallback.refreshTimeDisplay();

				sCIDisplay.operationCallback.refreshDateDisplay();

				sCIDisplay.operationCallback.refreshAlarmDisplay();

				sCIHelper.isEditingTime = false;

				nextStateIndex = 2;
				stateVector[2] = State.main_region_digitalwatch_displayRefreshing_showTime;
			}
		}
	}

	/* The reactions of state Blink1. */
	private void reactMain_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink1() {
		if (timeEvents[5]) {
			switch (stateVector[2]) {
				case main_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink1 :
					nextStateIndex = 2;
					stateVector[2] = State.$NullState$;

					timer.unsetTimer(this, 6);
					break;

				case main_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink2 :
					nextStateIndex = 2;
					stateVector[2] = State.$NullState$;

					timer.unsetTimer(this, 7);
					break;

				default :
					break;
			}

			timer.unsetTimer(this, 5);

			sCIHelper.isEditingTime = false;

			timer.setTimer(this, 2, 1 * 1000, false);

			sCIDisplay.operationCallback.refreshTimeDisplay();

			sCIDisplay.operationCallback.refreshDateDisplay();

			sCIDisplay.operationCallback.refreshAlarmDisplay();

			sCIHelper.isEditingTime = false;

			nextStateIndex = 2;
			stateVector[2] = State.main_region_digitalwatch_displayRefreshing_showTime;
		} else {
			if (sCIButtons.bottomRightPressed) {
				switch (stateVector[2]) {
					case main_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink1 :
						nextStateIndex = 2;
						stateVector[2] = State.$NullState$;

						timer.unsetTimer(this, 6);
						break;

					case main_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink2 :
						nextStateIndex = 2;
						stateVector[2] = State.$NullState$;

						timer.unsetTimer(this, 7);
						break;

					default :
						break;
				}

				timer.unsetTimer(this, 5);

				sCILogicUnit.operationCallback.selectNext();

				timer.setTimer(this, 9, 2 * 1000, false);

				nextStateIndex = 2;
				stateVector[2] = State.main_region_digitalwatch_displayRefreshing_switchSelect;
			} else {
				if (sCIButtons.bottomLeftPressed) {
					switch (stateVector[2]) {
						case main_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink1 :
							nextStateIndex = 2;
							stateVector[2] = State.$NullState$;

							timer.unsetTimer(this, 6);
							break;

						case main_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink2 :
							nextStateIndex = 2;
							stateVector[2] = State.$NullState$;

							timer.unsetTimer(this, 7);
							break;

						default :
							break;
					}

					timer.unsetTimer(this, 5);

					timer.setTimer(this, 10, 300, false);

					sCILogicUnit.operationCallback.increaseSelection();

					sCIDisplay.operationCallback.showSelection();

					nextStateIndex = 2;
					stateVector[2] = State.main_region_digitalwatch_displayRefreshing_increaseSelection;
				} else {
					if (timeEvents[6]) {
						nextStateIndex = 2;
						stateVector[2] = State.$NullState$;

						timer.unsetTimer(this, 6);

						timer.setTimer(this, 7, 500, false);

						sCIDisplay.operationCallback.hideSelection();

						nextStateIndex = 2;
						stateVector[2] = State.main_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink2;
					}
				}
			}
		}
	}

	/* The reactions of state Blink2. */
	private void reactMain_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink2() {
		if (timeEvents[5]) {
			switch (stateVector[2]) {
				case main_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink1 :
					nextStateIndex = 2;
					stateVector[2] = State.$NullState$;

					timer.unsetTimer(this, 6);
					break;

				case main_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink2 :
					nextStateIndex = 2;
					stateVector[2] = State.$NullState$;

					timer.unsetTimer(this, 7);
					break;

				default :
					break;
			}

			timer.unsetTimer(this, 5);

			sCIHelper.isEditingTime = false;

			timer.setTimer(this, 2, 1 * 1000, false);

			sCIDisplay.operationCallback.refreshTimeDisplay();

			sCIDisplay.operationCallback.refreshDateDisplay();

			sCIDisplay.operationCallback.refreshAlarmDisplay();

			sCIHelper.isEditingTime = false;

			nextStateIndex = 2;
			stateVector[2] = State.main_region_digitalwatch_displayRefreshing_showTime;
		} else {
			if (sCIButtons.bottomRightPressed) {
				switch (stateVector[2]) {
					case main_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink1 :
						nextStateIndex = 2;
						stateVector[2] = State.$NullState$;

						timer.unsetTimer(this, 6);
						break;

					case main_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink2 :
						nextStateIndex = 2;
						stateVector[2] = State.$NullState$;

						timer.unsetTimer(this, 7);
						break;

					default :
						break;
				}

				timer.unsetTimer(this, 5);

				sCILogicUnit.operationCallback.selectNext();

				timer.setTimer(this, 9, 2 * 1000, false);

				nextStateIndex = 2;
				stateVector[2] = State.main_region_digitalwatch_displayRefreshing_switchSelect;
			} else {
				if (sCIButtons.bottomLeftPressed) {
					switch (stateVector[2]) {
						case main_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink1 :
							nextStateIndex = 2;
							stateVector[2] = State.$NullState$;

							timer.unsetTimer(this, 6);
							break;

						case main_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink2 :
							nextStateIndex = 2;
							stateVector[2] = State.$NullState$;

							timer.unsetTimer(this, 7);
							break;

						default :
							break;
					}

					timer.unsetTimer(this, 5);

					timer.setTimer(this, 10, 300, false);

					sCILogicUnit.operationCallback.increaseSelection();

					sCIDisplay.operationCallback.showSelection();

					nextStateIndex = 2;
					stateVector[2] = State.main_region_digitalwatch_displayRefreshing_increaseSelection;
				} else {
					if (timeEvents[7]) {
						nextStateIndex = 2;
						stateVector[2] = State.$NullState$;

						timer.unsetTimer(this, 7);

						timer.setTimer(this, 6, 500, false);

						sCIDisplay.operationCallback.showSelection();

						nextStateIndex = 2;
						stateVector[2] = State.main_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink1;
					}
				}
			}
		}
	}

	/* The reactions of state waitAlarmEdit. */
	private void reactMain_region_digitalwatch_displayRefreshing_waitAlarmEdit() {
		if (sCIButtons.bottomLeftReleased) {
			nextStateIndex = 2;
			stateVector[2] = State.$NullState$;

			timer.unsetTimer(this, 8);

			timer.setTimer(this, 2, 1 * 1000, false);

			sCIDisplay.operationCallback.refreshTimeDisplay();

			sCIDisplay.operationCallback.refreshDateDisplay();

			sCIDisplay.operationCallback.refreshAlarmDisplay();

			sCIHelper.isEditingTime = false;

			nextStateIndex = 2;
			stateVector[2] = State.main_region_digitalwatch_displayRefreshing_showTime;
		} else {
			if (timeEvents[8]) {
				nextStateIndex = 2;
				stateVector[2] = State.$NullState$;

				timer.unsetTimer(this, 8);

				sCILogicUnit.operationCallback.startAlarmEditMode();

				timer.setTimer(this, 5, 5 * 1000, false);

				timer.setTimer(this, 6, 500, false);

				sCIDisplay.operationCallback.showSelection();

				nextStateIndex = 2;
				stateVector[2] = State.main_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink1;
			}
		}
	}

	/* The reactions of state switchSelect. */
	private void reactMain_region_digitalwatch_displayRefreshing_switchSelect() {
		if (sCIButtons.bottomRightReleased) {
			nextStateIndex = 2;
			stateVector[2] = State.$NullState$;

			timer.unsetTimer(this, 9);

			timer.setTimer(this, 5, 5 * 1000, false);

			timer.setTimer(this, 6, 500, false);

			sCIDisplay.operationCallback.showSelection();

			nextStateIndex = 2;
			stateVector[2] = State.main_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink1;
		} else {
			if (timeEvents[9]) {
				nextStateIndex = 2;
				stateVector[2] = State.$NullState$;

				timer.unsetTimer(this, 9);

				timer.setTimer(this, 2, 1 * 1000, false);

				sCIDisplay.operationCallback.refreshTimeDisplay();

				sCIDisplay.operationCallback.refreshDateDisplay();

				sCIDisplay.operationCallback.refreshAlarmDisplay();

				sCIHelper.isEditingTime = false;

				nextStateIndex = 2;
				stateVector[2] = State.main_region_digitalwatch_displayRefreshing_showTime;
			}
		}
	}

	/* The reactions of state increaseSelection. */
	private void reactMain_region_digitalwatch_displayRefreshing_increaseSelection() {
		if (timeEvents[10]) {
			nextStateIndex = 2;
			stateVector[2] = State.$NullState$;

			timer.unsetTimer(this, 10);

			sCIDisplay.operationCallback.hideSelection();

			timer.setTimer(this, 10, 300, false);

			sCILogicUnit.operationCallback.increaseSelection();

			sCIDisplay.operationCallback.showSelection();

			nextStateIndex = 2;
			stateVector[2] = State.main_region_digitalwatch_displayRefreshing_increaseSelection;
		} else {
			if (sCIButtons.bottomLeftReleased) {
				nextStateIndex = 2;
				stateVector[2] = State.$NullState$;

				timer.unsetTimer(this, 10);

				timer.setTimer(this, 5, 5 * 1000, false);

				timer.setTimer(this, 6, 500, false);

				sCIDisplay.operationCallback.showSelection();

				nextStateIndex = 2;
				stateVector[2] = State.main_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink1;
			}
		}
	}

	/* The reactions of state GlowOff. */
	private void reactMain_region_digitalwatch_displayGlowing_GlowOff() {
		if (sCIButtons.topRightPressed) {
			nextStateIndex = 3;
			stateVector[3] = State.$NullState$;

			sCIDisplay.operationCallback.setIndiglo();

			nextStateIndex = 3;
			stateVector[3] = State.main_region_digitalwatch_displayGlowing_GlowOn;
		}
	}

	/* The reactions of state GlowOn. */
	private void reactMain_region_digitalwatch_displayGlowing_GlowOn() {
		if (sCIButtons.topRightReleased) {
			nextStateIndex = 3;
			stateVector[3] = State.$NullState$;

			timer.setTimer(this, 11, 2 * 1000, false);

			nextStateIndex = 3;
			stateVector[3] = State.main_region_digitalwatch_displayGlowing_GlowDelay;
		}
	}

	/* The reactions of state GlowDelay. */
	private void reactMain_region_digitalwatch_displayGlowing_GlowDelay() {
		if (timeEvents[11]) {
			nextStateIndex = 3;
			stateVector[3] = State.$NullState$;

			timer.unsetTimer(this, 11);

			sCIDisplay.operationCallback.unsetIndiglo();

			nextStateIndex = 3;
			stateVector[3] = State.main_region_digitalwatch_displayGlowing_GlowOff;
		} else {
			if (sCIButtons.topRightPressed) {
				nextStateIndex = 3;
				stateVector[3] = State.$NullState$;

				timer.unsetTimer(this, 11);

				sCIDisplay.operationCallback.setIndiglo();

				nextStateIndex = 3;
				stateVector[3] = State.main_region_digitalwatch_displayGlowing_GlowOn;
			}
		}
	}

	public void runCycle() {

		clearOutEvents();

		for (nextStateIndex = 0; nextStateIndex < stateVector.length; nextStateIndex++) {

			switch (stateVector[nextStateIndex]) {
				case main_region_digitalwatch_timeCounting_Counting :
					reactMain_region_digitalwatch_timeCounting_Counting();
					break;
				case main_region_digitalwatch_chronoCounting_Inactive :
					reactMain_region_digitalwatch_chronoCounting_Inactive();
					break;
				case main_region_digitalwatch_chronoCounting_Active :
					reactMain_region_digitalwatch_chronoCounting_Active();
					break;
				case main_region_digitalwatch_displayRefreshing_showTime :
					reactMain_region_digitalwatch_displayRefreshing_showTime();
					break;
				case main_region_digitalwatch_displayRefreshing_showChrono :
					reactMain_region_digitalwatch_displayRefreshing_showChrono();
					break;
				case main_region_digitalwatch_displayRefreshing_waitTimeEdit :
					reactMain_region_digitalwatch_displayRefreshing_waitTimeEdit();
					break;
				case main_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink1 :
					reactMain_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink1();
					break;
				case main_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink2 :
					reactMain_region_digitalwatch_displayRefreshing_editMode_blinkingEdit_Blink2();
					break;
				case main_region_digitalwatch_displayRefreshing_waitAlarmEdit :
					reactMain_region_digitalwatch_displayRefreshing_waitAlarmEdit();
					break;
				case main_region_digitalwatch_displayRefreshing_switchSelect :
					reactMain_region_digitalwatch_displayRefreshing_switchSelect();
					break;
				case main_region_digitalwatch_displayRefreshing_increaseSelection :
					reactMain_region_digitalwatch_displayRefreshing_increaseSelection();
					break;
				case main_region_digitalwatch_displayGlowing_GlowOff :
					reactMain_region_digitalwatch_displayGlowing_GlowOff();
					break;
				case main_region_digitalwatch_displayGlowing_GlowOn :
					reactMain_region_digitalwatch_displayGlowing_GlowOn();
					break;
				case main_region_digitalwatch_displayGlowing_GlowDelay :
					reactMain_region_digitalwatch_displayGlowing_GlowDelay();
					break;
				default :
					// $NullState$
			}
		}

		clearEvents();
	}
}
