package myleap;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.event.InputEvent;

import com.leapmotion.leap.CircleGesture;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.InteractionBox;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Vector;
import com.leapmotion.leap.Gesture.Type;
public class MouseListener extends Listener 
{
	public Robot robot;
	private enum State {
		NORMAL,
		LEFTDOWN,
		RIGHTDOWN
	}
	State state;
	int leftdown_buffer;
	int leftup_buffer;
	int rightdown_buffer;
	int rightup_buffer;
	boolean debug;
	int MAX_BUFFER = 2;
	
	public void onInit(Controller controller) {
		System.out.println("init called");
		
		debug = controller.config().getBool("MyLeap.Debug");
		
		controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
		controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
		controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
		controller.config().setFloat("Gesture.KeyTap.MinDistance", 0.5f);
		controller.config().setFloat("Gesture.ScreenTap.MinDistance", 0.5f);
		controller.config().save();
		System.out.println("mouse activated");
		
		state = State.NORMAL;
		leftdown_buffer = 0;
		leftup_buffer = 0;
		rightdown_buffer = 0;
		rightup_buffer = 0;
	}
	//on leap device connected
	public void onConnect(Controller controller) {	
		System.out.println("connect called");
	}
	
	//getting gestures performed on frame	
	public void onFrame(Controller controller)
	{
		// System.out.println("frame called");
		
		Frame frame = controller.frame();
		InteractionBox box = frame.interactionBox();
		
		try {
			robot = new Robot();
		}
		catch(Exception e) {
			System.err.println("failed to create robot");
			return;
		}
		
		for (Gesture gesture: frame.gestures()) {
			for (Hand hand: frame.hands()) {
				if (hand.isLeft()) {
					for (Finger thumb: hand.fingers()) {
						for (Finger index: hand.fingers()) {
							for (Finger middle: hand.fingers()) {
								for (Finger pinky: hand.fingers()) {
									for (Finger ring: hand.fingers()) {
										if (thumb.type() == Finger.Type.TYPE_THUMB &&
												index.type() == Finger.Type.TYPE_INDEX &&
											middle.type() == Finger.Type.TYPE_INDEX &&
											pinky.type() == Finger.Type.TYPE_PINKY &&
											ring.type() == Finger.Type.TYPE_RING) {
											System.out.println("frame: "+ frame.id() + ", left gesture: " + 
													thumb.isExtended() + " " +
													index.isExtended() + " " +
													middle.isExtended() + " " +
													pinky.isExtended() + " " + 
													ring.isExtended() + " " + 
													gesture.type().toString());
											if (!thumb.isExtended() && 
													index.isExtended() && 
													// !middle.isExtended() &&
													!pinky.isExtended() &&
													!ring.isExtended()) {
										
												if (gesture.type() == Type.TYPE_KEY_TAP || gesture.type() == Type.TYPE_SCREEN_TAP) {
													System.out.println("frame: " + frame.id() + ", mouse left click");
													if (!debug) {
														robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
														robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
													}
													return;
												}
												if (gesture.type() == Type.TYPE_CIRCLE) {
													CircleGesture circle = new CircleGesture(gesture);
													if (circle.pointable().direction().angleTo(circle.normal()) <= Math.PI / 3) {
														System.out.println("frame: " + frame.id() + ", mouse wheel up");
														if (!debug) {
															robot.mouseWheel(1);
														}
														return;
													} else {
														System.out.println("frame: " + frame.id() + ", mouse wheel down");
														if (!debug) {
															robot.mouseWheel(-1);
														}
														return;
													}
												}
											}
										}
									}	
								}
							}
						}
					}
				}
				if (hand.isRight()) {
					for (Finger thumb: hand.fingers()) {
						for (Finger index: hand.fingers()) {
							for (Finger middle: hand.fingers()) {
								for (Finger pinky: hand.fingers()) {
									for (Finger ring: hand.fingers()) {
										if (thumb.type() == Finger.Type.TYPE_THUMB &&
												index.type() == Finger.Type.TYPE_INDEX &&
											middle.type() == Finger.Type.TYPE_INDEX &&
											pinky.type() == Finger.Type.TYPE_PINKY &&
											ring.type() == Finger.Type.TYPE_RING) {
											System.out.println("frame: "+ frame.id() + ", right gesture: " + 
													thumb.isExtended() + " " +
													index.isExtended() + " " +
													middle.isExtended() + " " +
													pinky.isExtended() + " " + 
													ring.isExtended() + " " + 
													gesture.type().toString());
											if (!thumb.isExtended() && 
													index.isExtended() && 
													// !middle.isExtended() &&
													!pinky.isExtended() &&
													!ring.isExtended()) {
										
												if (gesture.type() == Type.TYPE_KEY_TAP || gesture.type() == Type.TYPE_SCREEN_TAP) {
													System.out.println("frame: " + frame.id() + ", mouse right click");
													if (!debug) {
														robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
														robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
													}
													return;
												}
											}
										}
									}	
								}
							}
						}
					}
				}
			}
		}
		
		for (Hand hand: frame.hands()) {
			Vector handpos = hand.stabilizedPalmPosition();		
			Vector boxHandpos = box.normalizePoint(handpos);															
			Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
			float x = screen.width * boxHandpos.getX();
			float y = screen.height - boxHandpos.getY() * screen.height;
			
			if (hand.isLeft()) {
				// clean right state
				if (state == State.RIGHTDOWN) {
					state = State.LEFTDOWN;
				}
				rightdown_buffer = 0;
				rightup_buffer = 0;
				//System.out.println("find left hand with grab: " + hand.grabStrength());
				if (hand.grabStrength() > 0.5) {
					if (state == State.NORMAL) {
						if (leftdown_buffer < MAX_BUFFER) {
							leftdown_buffer++;
						} else {
							leftdown_buffer = 0;
							state = State.LEFTDOWN;
							System.out.println("frame: " + frame.id() + ", mouse left down");
							if (!debug) {
								robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
							}
							return;
						}
					}
				} else {
					if (state == State.LEFTDOWN) {
						if (leftup_buffer < MAX_BUFFER) {
							leftup_buffer++;
						} else {
							leftup_buffer = 0;
							state = State.NORMAL;
							System.out.println("frame: " + frame.id() + ", mouse left up");
							if (!debug) {
								robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
							}
							return;
						}
					}
				}
				
				// move
				if (!debug) {
					robot.mouseMove((int)x, (int)y);
				}
			} else if (hand.isRight()) {
				// clean left state
				if (state == State.LEFTDOWN) {
					state = State.NORMAL;
				}
				leftdown_buffer = 0;
				leftup_buffer = 0;
				//System.out.println("find right hand with grab: " + hand.grabStrength());
				if (hand.grabStrength() > 0.5) {
					if (state == State.NORMAL) {
						if (rightdown_buffer < MAX_BUFFER) {
							rightdown_buffer++;
						} else {
							rightdown_buffer = 0;
							state = State.RIGHTDOWN;
							System.out.println("frame: " + frame.id() + ", mouse right down");
							if (!debug) {
								robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
							}
							return;
						}
					} 
				} else {
					if (state == State.RIGHTDOWN) {
						if (rightup_buffer < MAX_BUFFER) {
							rightup_buffer++;
						} else {
							rightup_buffer = 0;
							state = State.NORMAL;
							System.out.println("frame: " + frame.id() + ", mouse right up");
							if (!debug) {
								robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
							}
							return;
						}
					}
				}
				
				// move
				if (!debug) {
					robot.mouseMove((int)x, (int)y);
				}
			}
		}
		
	}
}
