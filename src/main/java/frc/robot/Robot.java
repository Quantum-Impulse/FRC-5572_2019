package frc.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Relay.Direction;
import edu.wpi.cscore.UsbCamera;

public class Robot extends TimedRobot {
  // Override default robot period
  private static final double kRealPeriod = 0.05;

  public Robot() {
    super(kRealPeriod);
  }

  // PWM ports
  private static final int leftMotorPort1 = 6, leftMotorPort2 = 8, rightMotorPort1 = 4, rightMotorPort2 = 5, 
    intakePort = 3, beltPort = 7, hatchPort = 2;

  // PCM ports
  private static final int HRPort1 = 1, HRPort2 = 6, IAPort1 = 2, IAPort2 = 5,
    FCS1 = 5, FCS2 = 2, BCS1 = 4, BCS2 = 3,  HI1 = 6, HI2 = 1;

  // PCM device IDs
  private static final int PCM1 = 0, PCM2 = 1;

  // Motors and solenoids
  private static SpeedController belt, hatch;
  private static DoubleSolenoid hatchRelease, hatchIntake, frontClimbStand, backClimbStand;
  private static Arm<PWMVictorSPX> intakeArm;

  // Init all other devices/variables
  private static DifferentialDrive m_myRobot;
  private static FRC5572Controller driverController1, driverController2;
  private static CameraServer cServer;
  private static UsbCamera rearCamera;
  private static Compressor compressor;

  // Sets the power the motors will be scaled by
  private static final double intakeSpeed = 0.5, hatchSpeed = 1;
  private static double driveScale, beltSpeed;

  @Override
  public void robotInit() {
    // All motors are run through Victors except the window motor which is run through a Spark
    // Init of individual motors
    belt = new PWMVictorSPX(beltPort);
    hatch = new Spark(hatchPort);

    // Init of pneumatics
    hatchRelease = new DoubleSolenoid(PCM2, HRPort1, HRPort2);
    hatchIntake = new DoubleSolenoid(PCM1, HI1, HI2);
    frontClimbStand = new DoubleSolenoid(PCM2, FCS1, FCS2);
    backClimbStand = new DoubleSolenoid(PCM2, BCS1, BCS2);

    // Init of the intake
    intakeArm = new Arm<PWMVictorSPX>(IAPort1, IAPort2, new PWMVictorSPX(intakePort), PCM1);

    // Init of drive motor groups
    SpeedControllerGroup group_left = new SpeedControllerGroup(new PWMVictorSPX(leftMotorPort1),
        new PWMVictorSPX(leftMotorPort2));
    SpeedControllerGroup group_right = new SpeedControllerGroup(new PWMVictorSPX(rightMotorPort1),
        new PWMVictorSPX(rightMotorPort2));
    m_myRobot = new DifferentialDrive(group_left, group_right);

    // Init of all other devices
    compressor = new Compressor();
    cServer = CameraServer.getInstance();
    UsbCamera rearCamera = cServer.startAutomaticCapture(0);
    rearCamera.setFPS(15);
    
    // Init of the controllers
    driverController1 = new FRC5572Controller(0);
    driverController2 = new FRC5572Controller(1);

    // Init of the drive scale
    driveScale = 0.9;

    //Create value to smart dashboard
    //SmartDashboard.getBoolean("InverseEnabled", false);
    SmartDashboard.putBoolean("InverseEnabled", false);
    // Starting the compressor
    compressor.start();
  }

  // Boolean for the drive control flip system (MUST BE SET TO FALSE TO WORK)
  private static boolean driveInverted = false, isDriveInverted = false;

  // Boolean for disabling back camera
  // private static boolean disableCamera = true, isCameraDisabled = false;

  // Naming variables for button assignments
  private static double RY1, LY1, LY2, RT1, LT1, LT2;
  private static boolean B2, X2, Y2, A2, RB1, LB1, A1, B1, LB2, RB2;
 
  @Override
  public void robotPeriodic() {
    // This program utilizes try/catch statements throughout for less complicated error handling
    // Renaming of the buttons for easier reading of the code and computation, all button usages are defined here
    // Driver controls
  
    RY1 = driverController1.RY();  // Used for right motor input to the drive train.
    LY1 = driverController1.LY();  // Used for left motor input to the drive train.
    LB1 = driverController1.LB(); // Slow button
    RB1 = driverController1.RB(); // Boost button
    A1 = driverController1.A(); // Flips the drivetrain controls
    B1 = driverController1.B(); // Disables back camera
    RT1 = driverController1.RT(); // Runs the front climb stand pneumatics
    LT1 = driverController1.LT(); // Runs the back climb stand pneumatics
    // Operator controls
    LY2 = driverController2.LY(); // Used to run the belt
    A2 = driverController2.A(); // Close hatch intake (GRAB HATCH)
    B2 = driverController2.B(); // Close hatch intake, push hatch release pistons (PLACE HATCH)
    LB2 = driverController2.LB(); // Runs the hatch motor down
    RB2 = driverController2.RB(); // Runs the hatch motor up
    X2 = driverController2.X(); // Puts the arm up and stops the intake motor
    Y2 = driverController2.Y(); // Puts the arm down and starts the intake motor
    LT2 = driverController2.LT(); // Boosts the speed of the belt to shoot over other robots

    try {
      // Controlling the wheels using the left and right sticks of the drive controller
      // Due to wiring, both sides must be reversed
      m_myRobot.tankDrive(driveScale * (driveScale > 0 ? -LY1 : -RY1), driveScale * (driveScale > 0 ? -RY1 : -LY1));
      // Returns the percentage of power being put to the wheels to the Smart Dashboard
      SmartDashboard.putNumber("Left Wheel Power", driveScale * -LY1);
      SmartDashboard.putNumber("Right Wheel Power", driveScale * -RY1);
    } catch (Exception driveError) {
      // Outputs an error if there is an error within the drivetrain
      System.out.println(driveError);
    }

    // Allows the driver to press the right bumper to boost the speed of the robot
    // or left bumper to slow it down
    try {
      // LB sets power to 60% of input (slow), RB sets power to 100% of input (boosted), 90% is default
      driveScale = LB1 || RB1 ? (LB1 ? (driveScale > 0 ? 0.6 : -0.6) : (driveScale > 0 ? 1 : -1)) : (driveScale > 0 ? 0.9 : -0.9);
    } catch (Exception boostError) {
      // Outputs an error if there is an error within the boost function
      System.out.println(boostError);
    }

    // Reverses the drivetrain controls using the A button of the driver controller
    try {
      if(SmartDashboard.getBoolean("InverseEnabled", false))
      {
      if (A1 && !driveInverted) {
        driveInverted = true;
        driveScale = -driveScale;
      } if (!A1) {
        // Must be FALSE for button to function correctly
        driveInverted = false;
      }
      // Changing the boolean solely used for output to the Smart Dashboard
      isDriveInverted = driveScale < 0 ? true : false;
      // Returns boolean state to Smart Dashboard
      SmartDashboard.putBoolean("isDriveInverted", isDriveInverted);
    }
    } catch (Exception driveInvertError) {
      // Outputs an error if there is an error while flipping the drivetrain controls
      System.out.println(driveInvertError);
    }

    // Controls the front climb stand pneumatics using the left bumper of the driver controller
    try {
      // Pressing RT activates the front climb stand
      frontClimbStand.set(RT1 > 0.2 ? Value.kReverse : Value.kForward);
    } catch (Exception frontClimbError) {
      // Outputs an error if there is an error within the climb mechanism
      System.out.println(frontClimbError);
    }

    // Controls the back climb stand pneumatics using the right bumper of the driver controller
    try {
      // Pressing LT activates the back climb stand
      backClimbStand.set(LT1 > 0.2 ? Value.kReverse : Value.kForward);
    } catch (Exception backClimbError) {
      // Outputs an error if there is an error within the climb mechanism
      System.out.println(backClimbError);
    }

    // Pressing B on the driver controller disables the back camera (to save bandwidth)
    // try {
    //   rearCamera.setFPS(20);
    //   if (B1 && disableCamera) {
    //     disableCamera = false;
    //     rearCamera.setFPS(1);
    //   } if (!B1) {
    //     disableCamera = true;
    //   }
    //   // Using a seperate variable to control the output to the Smart Dashboard
    //   if (!disableCamera) {
    //     isCameraDisabled = true;
    //   }
    //   SmartDashboard.putBoolean("isCameraDisabled", isCameraDisabled);
    // } catch (Exception cameraError) {
    //   System.out.println(cameraError);
    // }

    // Controlling the belt using the triggers of the second controller
    try {
      belt.set(-LY2 * beltSpeed);
      intakeArm.runMotor(intakeSpeed, Math.abs(LY2) > 0.1);
      // Returns the speed of the belt to the smart dashboard
      SmartDashboard.putNumber("Belt Speed", belt.get());
    } catch (Exception beltError) {
      // Outputs an error if there is an error within the belt
      System.out.println(beltError);
    }

    // Allows the operator to boost the speed of the belt by pressing LT
    try {
      // Normal belt speed is 65%, while boosted belt speed is 100%
      beltSpeed = (LT2 > 0.2 ? 1 : 0.65);
    } catch (Exception beltBoostError) {
      // Outputs an error if an error occurs whilst boosting the belt
      System.out.println(beltBoostError);
    }

    // When A is pressed, the intake closes to pick up a hatch 
    // When B is pressed, the intake closes and the hatch release pneumatics pop out to place a hatch
    try {
      hatchIntake.set(A2 || B2 ? Value.kReverse : Value.kForward);
      hatchRelease.set(B2 ? Value.kReverse : Value.kForward);
    } catch (Exception hatchError) {
      // Outputs an error if there is an error within the hatch pneumatics
      System.out.println(hatchError);
    }

    // Controls the hatch mechanism using LB and RB to run the seat motor
    // try {
    //   hatch.set(LB2 || RB2 ? (RB2 ? -hatchSpeed : hatchSpeed) : 0);
    // } catch (Exception windowError) {
    //   // Outputs an error if there is an error within the hatch motor
    //   System.out.println(windowError);
    // }

    // Running the intake arm and intake at the same time using X & Y buttons on the second controller
    // Runs through the seperate arm class
    try {
     intakeArm.set( X2 ^ Y2 ? (X2 ? Arm.Direction.aUp : Arm.Direction.aDown) : Arm.Direction.aOff, intakeSpeed);
     } catch (Exception intakeArmError) {
       // Outputs an error if there is an error within the intake
      System.out.println(intakeArmError);
    }
  }
}