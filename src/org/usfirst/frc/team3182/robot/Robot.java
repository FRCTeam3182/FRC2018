/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team3182.robot;

import com.ctre.phoenix.*;
import com.ctre.*;
import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.*;



/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	//Defines the variables as members of our Robot class

    Joystick joy = new Joystick(0);
    int[] joyButtons = {0,1,2,3,4};
    /*
    	0: intake in
    	1: intake left
    	2: intake right
    	3: intake out
    	4: climb up
    */
    private static final String kDefaultAuto = "Default";
	private static final String kLeftStartAuto = "LeftPos";
	private static final String kRightStartAuto = "RightPos";
	private String m_autoSelected;
	private SendableChooser<String> m_chooser = new SendableChooser<>();
    Timer m_timer;
    Timer a_timer;
    Spark winch = new Spark(1);
    Spark rightIntake = new Spark(3);
    Spark leftIntake = new Spark(2);
    Spark arm = new Spark(4);
    
    WPI_TalonSRX frontLeft = new WPI_TalonSRX(3);
    WPI_TalonSRX frontRight = new WPI_TalonSRX(2);
    WPI_TalonSRX backLeft = new WPI_TalonSRX(1);
	WPI_TalonSRX backRight = new WPI_TalonSRX(0);

    DifferentialDrive driveTrain = new DifferentialDrive(frontLeft, frontRight);
    
    //public static int winch = 0;
	//public static Spark winchSpark = new Spark(winch);
    //Winch winch;
    
  //Initializes the variables in the robotInit method, this method is called when the robot is initializing
    public void robotInit() {
         m_timer = new Timer();
         a_timer = new Timer();
         backLeft.follow(frontLeft);
         backRight.follow(frontRight);
         
         m_chooser.addDefault("Default Auto" , kDefaultAuto);
         m_chooser.addObject("LeftStartAuto", kLeftStartAuto);
         m_chooser.addObject("RightStartAuto", kRightStartAuto);
         SmartDashboard.putData("Auto choices", m_chooser);
         
         frontLeft.setInverted(false);
         backLeft.setInverted(false);
         frontRight.setInverted(false);
         backLeft.setInverted(false);
    }    

	/**
	 * This function is run once each time the robot enters autonomous mode.
	 */
	@Override
	public void autonomousInit() {
		m_timer.reset();
		a_timer.reset();
		m_timer.start();
		a_timer.start();
		
		m_autoSelected = m_chooser.getSelected();
		System.out.println("Auto selected:" + m_autoSelected);
		
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		double speed = 0.62;
		switch (m_autoSelected) { 
			case kDefaultAuto:
			default:
				while(m_timer.get()<3.0) {
					driveTrain.arcadeDrive(speed, -0.05);
				}
				driveTrain.stopMotor();
				break;
			case kLeftStartAuto:
				while(m_timer.get()<3.0) {
					driveTrain.arcadeDrive(speed, -0.05);
				}
				driveTrain.stopMotor();
				break;
			case kRightStartAuto:
				while(m_timer.get()<3.0) {
					driveTrain.arcadeDrive(speed, -0.05);
				}
				driveTrain.stopMotor();
				break;
			}
		
	}
			
	

	/**
	 * This function is called once each time the robot enters teleoperated mode.
	 */
	@Override
	public void teleopInit() {
	}

	/**
	 * This function is called periodically during teleoperated mode.
	 */
	@Override
	public void teleopPeriodic() {
		drive();
		climb();
		moveArm();
		
	}

	
	@Override
	public void testPeriodic() 
	{
		climb();
	}
	
	public void drive(){
		
		double forward = -1.0 * joy.getY(); 
		double turn = +1.0 * joy.getX();
		if (Math.abs(forward) < 0.10) { //10% deadband
			forward = 0;
		}
		if (Math.abs(turn) < 0.10) {
			turn = 0;
		}
		//System.out.println("JoyY:" + forward + "  turn:" + turn );
		driveTrain.arcadeDrive(forward, turn);
	}
	
	public void climb(){
		if (joy.getRawButton(3)){
			winch.set(1);
		} else if (joy.getRawButton(10)) {
			winch.set(-1);;
		} else {
			winch.stopMotor();
		}
	} 
	
	public void moveArm(){
		if (joy.getRawButton(5)) {
			arm.set(1);
		}
		if (joy.getRawButton(3)) {
			arm.set(-1);
		}
		if (!joy.getRawButton(5) && !joy.getRawButton(3)) {
			arm.stopMotor();
		}
	}
	/*
	public void intake(){
		if ()
	}

	
	public void cubeIntake()
	{
		//In both
		if (Robot.m_stick.getRawButton(6)) {
			intakeR(1);
			intakeL(-1);
		}
		//Out both
		if (Robot.m_stick.getRawButton(4)) {
			intakeR(-1);
			intakeL(1);
		}
		//In right
		if (Robot.m_stick.getRawButton(11)) {
			intakeR(1);
		}
		//In left
		if (Robot.m_stick.getRawButton(12)) {
			intakeL(-1);
		}
	}
	
	
	public void grabCube()
	{
		
		
	}

	public void releaseCube()
	{
		
		
	}


		//Stops climbing
		public void climbStop() {
			winchSpark.set(0);
		}
	}
	*/
}