package org.firstinspires.ftc.teamcode; 
import com.qualcomm.robotcore.hardware.DcMotor; 
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode; 
import com.qualcomm.robotcore.eventloop.opmode.Autonomous; 
@Autonomous(name="FIGURE8REALROBOT", group="Autonomous") 
public class Figure8RealRobot extends LinearOpMode { 
    // 4 Motor Declarations matching your configuration names 
    DcMotor left_back; 
    DcMotor right_back; 
    DcMotor left_front; 
    DcMotor right_front; 
    @Override 
    public void runOpMode() { 
        // --- 1. HARDWARE MAPPING --- 
        
        left_back = hardwareMap.get(DcMotor.class, "left_back"); 
        right_back = hardwareMap.get(DcMotor.class, "right_back"); 
        left_front = hardwareMap.get(DcMotor.class, "left_front"); 
        right_front = hardwareMap.get(DcMotor.class, "right_front"); 
        
        // Set motor directions (Reverse left side so positive power moves forward) 
        
        left_back.setDirection(DcMotor.Direction.FORWARD); 
        left_front.setDirection(DcMotor.Direction.FORWARD); 
        right_back.setDirection(DcMotor.Direction.REVERSE); 
        right_front.setDirection(DcMotor.Direction.REVERSE); 
        telemetry.addData("Status", "Initialized. Ready to Run."); 
        telemetry.update(); 
        
        // Wait for the Driver Station play button to be pressed 
        waitForStart(); 
        // --- 2. RUN SEQUENCE (Your exact timing logic) --- 
        if (opModeIsActive()) { 
            setDrivePower(0.3, 0.7); 
            telemetry.addData("Status", 1);
            telemetry.update();
            sleep(1100);
            
            telemetry.addData("Status", 2);
            telemetry.update();
            setDrivePower(0, 0.5); 
            sleep(800);
            
            telemetry.addData("Status", 3);
            telemetry.update();
            setDrivePower(0.5, 0.5); 
            sleep(800); 
            
            telemetry.addData("Status", 4);
            telemetry.update();
            setDrivePower(0.4, 0); 
            sleep(800); 
            
            telemetry.addData("Status", 5);
            telemetry.update();
            setDrivePower(0.6, 0.2); 
            sleep(700);
            
            telemetry.addData("Status", 6);
            telemetry.update();
            setDrivePower(0.5, 0.5); 
            sleep(800);
            
            telemetry.addData("Status", 7);
            telemetry.update();
            setDrivePower(0.5, 0); 
            sleep(800);
            
            telemetry.addData("Status", 8);
            telemetry.update();
            setDrivePower(0.8, 0); 
            sleep(600);
         
            telemetry.addData("Status", 9);
            telemetry.update();
            setDrivePower(0.5, 0.25);
            sleep(1000);
            
            telemetry.addData("Status", 10);
            telemetry.update();
            setDrivePower(0.25, 0.5);
            sleep(1000);
           
            telemetry.addData("Status", 11);
            telemetry.update();
            setDrivePower(1, -1); 
            sleep(700);
            
            telemetry.addData("Status", "END");
            telemetry.update();
            setDrivePower(0, 0);
        } 
        
    } 
    
    
    // Helper method to control left and right sides across your 4 motors 
    private void setDrivePower(double leftPower, double rightPower) { 
        left_back.setPower(leftPower); 
        left_front.setPower(leftPower); 
        right_back.setPower(rightPower); 
        right_front.setPower(rightPower); 
        
    }
}  
