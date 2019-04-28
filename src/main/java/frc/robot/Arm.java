package frc.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SpeedController;

public class Arm<T> {
    public static enum Direction{
        aUp,
        aDown,
        aOff
    };
    
    private Direction position;
    private DoubleSolenoid intakeArm;
    private SpeedController intake;
    /**
     * Construct the arm class with the Arm solenoid, and motor intake
     * @param forwardChannel forwardChannel for the arm solenoid
     * @param reverseChannel reverseChannel for the arm solenoid
     * @param intakePort PWM Motor port
     */
    public Arm(int forwardChannel, int reverseChannel, T intakeVal, int PCMID){
        if(intakeVal instanceof SpeedController)
            this.intake = (SpeedController)intakeVal;
        else
            throw new Error("Instance of non-SpeedController class given to Arm");
        this.position = Direction.aOff;
        this.intakeArm = new DoubleSolenoid(PCMID, forwardChannel, reverseChannel);
        intakeArm.set(Value.kOff);
    }
    
    /**Attempts to put the arm in the up state
     * @return Boolean: true if successful, false if failed
     */
    private boolean Up(){
        // try{
            if(this.position != Direction.aUp)
                intakeArm.set(Value.kForward);
            this.position = Direction.aUp;
            return true;
        // }catch(Exception e){
        //     return false;
        // }
    }
    /**Attempts to put the arm in the down state
     * @return Boolean: true if successful, false if failed.
     */
    private boolean Down(){
        // try{
            if(this.position != Direction.aDown)
                intakeArm.set(Value.kReverse);
            this.position = Direction.aDown;
            return true;
        // }catch(Exception e){
        //     return false;
        // }
    }
    /**Attempts to put the arm in the off state.
     * @return Bool: true if successful, false if failed.
     */
    private boolean Off(){
        // try{
            if(this.position != Direction.aOff)
                intakeArm.set(Value.kOff);
            //this.position = Direction.aOff;
            return true;
        // }catch(Exception e){
        //     return false;
        // }
    }

    public Direction getState(){
        return this.position;
    }

    /**
     * Runs the motor based off the current setting
     * @param intakeSpeed Maximum intake speed to set the motor to when arm is down
     * @return Bool: based off if the function succeeded or not. **NOT USED**
     */
    public boolean runMotor(double intakeSpeed, boolean force){
        // try{
            switch(this.position){
                case aUp:
                    intake.set(0);
                    //System.out.println("Position is up");
                    break;
                case aDown:
                    intake.set(intakeSpeed);
                    //System.out.println("Position is down");
                    break;
                case aOff:

                    intake.set(0);
                    //System.out.println("Position is off");
                    break;
            }
            
            //intake.set((this.position == Direction.aDown ) ? intakeSpeed : 0);
            return true;
        // }catch(Exception e){
        //     return false;
        // }
    }
    /**
     * Easy control of Arm class
     * @param dir Direction enum with the desired operation.
     * @return Bool based on success of the program
     */
    public boolean set(Arm.Direction dir, double intakeMotorSpeed){
        boolean result = this.runMotor(intakeMotorSpeed, false);
        switch(dir){
            case aDown:
                result = this.Down();
                break;
            case aUp:
                result = this.Up();
                break;
            case aOff:
                result = this.Off();
                break;
            default:
                throw new Error("Non-Direction enum given to Arm.set()");
        }
        if(!result)
            throw new Error("Error occured in the Arm class while trying to set the Arm state.");
        result = result;
        
        return result;
    }
}