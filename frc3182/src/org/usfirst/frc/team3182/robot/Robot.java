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
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.*;
import edu.wpi.first.wpilibj.Relay;



/**
 * IMPORTANT FIELD NOTES
 * 		LEFT is 0, RIGHT is 1
 */
public class Robot extends IterativeRobot {

	//Defines the variables as members of our Robot class

    Joystick joy = new Joystick(0);
    Joystick pow = new Joystick(1);
    int[] joyButtons = {0,1,2,3,4, 5};
    /*
    	0: intake left
    	1: intake right
    	2: intake in
    	3: intake out
    	4: climb up
    	5: climb down
		
    */
    int[] powButtons = {0,1,2,3};
    /*
     *  0: arm down
     *  1: arm up
     *  2: cube grab
     *  3: cube release
     */
    
    private static final String kDefaultAuto = "Default";
	private static final String leftStartSwitch = "LeftStartSwitch";
	private static final String rightStartSwitch = "RightStartSwitch";
	private SendableChooser<String> autoChoose = new SendableChooser<>();
	private String autoPlan;
	
	String gameData;
	int[] sides = new int[2]; //closest element to far

	private static final double intakeSpeed = 0.5;  
	
    Timer m_timer;
    Timer a_timer;
    
    Spark winch = new Spark(1);
    Spark rightIntake = new Spark(3);
    Spark leftIntake = new Spark(2);
    Spark arm = new Spark(4);
    
    Relay bothSolenoid = new Relay(2);
    
    WPI_TalonSRX frontLeft = new WPI_TalonSRX(3);
    WPI_TalonSRX frontRight = new WPI_TalonSRX(2);
    WPI_TalonSRX backLeft = new WPI_TalonSRX(1);
	WPI_TalonSRX backRight = new WPI_TalonSRX(0);

    DifferentialDrive driveTrain = new DifferentialDrive(frontLeft, frontRight);

    
    public void robotInit() {
         m_timer = new Timer();
         a_timer = new Timer();
         backLeft.follow(frontLeft);
         backRight.follow(frontRight);
         autoChoose.addDefault("Drive Forward" , kDefaultAuto);
         autoChoose.addObject("Left Start, Switch", leftStartSwitch);
         autoChoose.addObject("Right Starting, Switch", rightStartSwitch);
         SmartDashboard.putData("Auto Choices", autoChoose);
         
         frontLeft.setInverted(false);
         backLeft.setInverted(false);
         frontRight.setInverted(false);
         backLeft.setInverted(false);
    }    
    
    // MAIN CODE
	@Override
	public void autonomousInit() {
		m_timer.reset();
		a_timer.reset();
		m_timer.start();
		a_timer.start();
		
		autoPlan = autoChoose.getSelected();
		System.out.println("Auto selected:" + autoChoose);
		
		getSides();
		
	}

	@Override
	public void autonomousPeriodic() {
		switch (autoPlan) { 
			case leftStartSwitch:
				autoLeftStartSwitch();
				break;
			case rightStartSwitch:
				autoRightStartSwitch();
				break;
			case kDefaultAuto:
			default:
				autoDriveForward();
				break;
			}
		
	}
			
	@Override
	public void teleopInit() {
	}

	@Override
	public void teleopPeriodic() {
		drive();
		climb();
		moveArm();
		intake();
		cubeAction();
	}

	@Override
	public void testPeriodic() 
	{

	}
	
	
	// AUTO METHODS
	public void getSides()
	{
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		if(gameData.length() > 0)
		{
			for(int a=0; a<3; a++)
			{
				if(gameData.charAt(a) == 'L')
				{
					sides[a] = 0;
				}
				else
				{
					sides[a] = 1;
				}
					
			}
		}
	}
	
	public void autoDriveForward()
	{
		while(m_timer.get()<3.0) {
			driveTrain.arcadeDrive(0.5, -0.05);
		}
		driveTrain.stopMotor();
	}
	
	public void autoLeftStartSwitch()
	{
		if(sides[0] == 'L') //LEFT TO LEFT
		{
			
		}
		else //LEFT TO RIGHT
		{
			
		}
	}
	
	public void autoRightStartSwitch()
	{
		if(sides[0] == 'L') //RIGHT TO LEFT
		{
			
		}
		else //RIGHT TO RIGHT
		{
			
		}
	}
	
	//TELEOP METHODS
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
		if (joy.getRawButton(powButtons[4])){ //CLIMB UP
			winch.set(1);
		} else if (joy.getRawButton(powButtons[5])) { //CLIMB DOWN
			winch.set(-1);;
		} else {
			winch.stopMotor();
		}
	} 
	
	public void moveArm(){
		if (pow.getRawButton(powButtons[0])) { // ARM DOWN
			arm.set(1);
		}
		if (pow.getRawButton(powButtons[1])) { // ARM UP
			arm.set(-1);
		}
		if (!pow.getRawButton(powButtons[0]) && !pow.getRawButton(powButtons[1])) {
			arm.stopMotor();
		}
	}
	
	public void intake()
	{
		if(pow.getRawButton(powButtons[2])) //INTAKE BOTH IN
		{
			rightIntake.set(intakeSpeed);
			leftIntake.set(-intakeSpeed);
		}
		else if(pow.getRawButton(powButtons[3]))//INTAKE BOTH OUT
		{
			rightIntake.set(-intakeSpeed);
			leftIntake.set(intakeSpeed);
		}
		else if(pow.getRawButton(powButtons[0])) //INTAKE LEFT IN
		{
			rightIntake.stopMotor();
			leftIntake.set(-intakeSpeed);
		}
		else if(pow.getRawButton(powButtons[1])) //INTAKE RIGHT IN
		{
			rightIntake.set(intakeSpeed);
			leftIntake.stopMotor();
		}
		else
		{
			rightIntake.stopMotor();
			leftIntake.stopMotor();
		}
	}
	
	public void cubeAction()
	{
		if(pow.getRawButton(powButtons[2]))
		{
			bothSolenoid.set(Relay.Value.kOn); //GRAB
		}
		else if(pow.getRawButton(powButtons[3])) 
		{
			bothSolenoid.set(Relay.Value.kOff); //RELEASE
		}
	}
	
}