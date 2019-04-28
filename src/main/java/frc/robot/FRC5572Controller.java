package frc.robot;

import edu.wpi.first.wpilibj.*;

public class FRC5572Controller {
    // These valus are the controller input values from the driver station
    public final int leftZ = 2;
    public final int leftX = 0;
    public final int leftY = 1;

    public final int rightZ = 3;
    public final int rightX = 4;
    public final int rightY = 5;

    public final int leftButton = 5;
    public final int rightButton = 6;

    public final int xButton = 3;
    public final int yButton = 4;
    public final int aButton = 1;
    public final int bButton = 2;

    public final int startButton = 8;
    public final int backButton = 7;

    public final int leftStickButton = 9;
    public final int rightStickButton = 10;

    private XboxController pad;
    /**Logitech Game Controller 
     * @param x port of controller
    */
    public FRC5572Controller(int x) {
        pad = new XboxController(x);
    }
    /**Returns the value of the left trigger
     * The value returned will be between 0 and 1, with 0 being fully depressed and 1 being fully pressed */
    public double LT() {
        return pad.getRawAxis(leftZ);
    }
    /**Returns the value of the left bumper
     * The value returned will be true if the button is pressed, and false otherwise */
    public boolean LB() {
        return pad.getRawButton(leftButton);
    }
    /**Returns the value of the right trigger
     * The value returned will be between 0 and 1, with 0 being fully depressed and 1 being fully pressed
     */
    public double RT() {
        return pad.getRawAxis(rightZ);
    }
    /**Returns the value of the right bumper
     * The value returned will be true if the button is pressed, and false otherwise
     */
    public boolean RB() {
        return pad.getRawButton(rightButton);
    }
    /**Returns the value of the blue X button
     * The value returned will be true if the button is pressed, and false otherwise
     */
    public boolean X() {
        return pad.getRawButton(xButton);
    }
    /**Returns the value of the yellow Y button
     * The value returned will be true if the button is pressed, and false otherwise
     */
    public boolean Y() {
        return pad.getRawButton(yButton);
    }
    /**Returns the value of the green A button
     * The value returned will be true if the button is pressed, and false otherwise
     */
    public boolean A() {
        return pad.getRawButton(aButton);
    }
    /**Returns the value of the red B button
     * The value returned will be true if the button is pressed, and false otherwise
     */
    public boolean B() {
        return pad.getRawButton(bButton);
    }
    /**Returns the values from the left joystick
     * The value returned is a pair, with the first value being the x-coordinate of the joystick and the second value being the y-coordinate.
     * The coordinates can be any value from -1 to 1, with 1 being fully up/right, -1 being fully down/left, and 0 being untouched.
     */
    public double LX() {
        return pad.getRawAxis(leftX);
    }
    /**Returns the values from the left joystick
     * The value returned is a pair, with the first value being the x-coordinate of the joystick and the second value being the y-coordinate.
     * The coordinates can be any value from -1 to 1, with 1 being fully up/right, -1 being fully down/left, and 0 being untouched.
     */
    public double LY() {
        return pad.getRawAxis(leftY);
    }
    /**Returns the values from the right joystick
     * The value returned is a pair, with the first value being the x-coordinate of the joystick and the second value being the y-coordinate. 
     * The coordinates can be any value from -1 to 1, with 1 being fully up/right, -1 being fully down/left, and 0 being untouched. */
    public double RX() {
        return pad.getRawAxis(rightX);
    }
    /**Returns the values from the right joystick
     * The value returned is a pair, with the first value being the x-coordinate of the joystick and the second value being the y-coordinate. 
     * The coordinates can be any value from -1 to 1, with 1 being fully up/right, -1 being fully down/left, and 0 being untouched. */
    public double RY() {
        return pad.getRawAxis(rightY);
    }
    /**Returns the values from the D Pad.
     * The value returned is an integer value which describes the location being pressed on the D Pad, with 0 being the upwards direction,
     * and each other value may be taken as the degrees (from 0 to 360). If none of the buttons are being pressed, -1 is returned instead. 
     */
    public int POV() {
        return pad.getPOV(0);
    }
    /**Returns the value of the start button
     * The value returned will be true if the button is pressed, and false otherwise
     */
    public boolean start() {
        return pad.getRawButton(startButton);
    }
    /**Returns the value of the back button
     * The value returned will be true if the button is pressed, and false otherwise
     */
    public boolean back() {
        return pad.getRawButton(backButton);
    }
    /**Returns the value of the left button
     * The value returned will be true if the button is pressed, and false otherwise
     */
    public boolean Lbutton() {
        return pad.getRawButton(leftStickButton);
    }
    /**Returns the value of the right button
     * The value returned will be true if the button is pressed, and false otherwise
     */
    public boolean Rbutton() {
        return pad.getRawButton(rightStickButton);
    }
}