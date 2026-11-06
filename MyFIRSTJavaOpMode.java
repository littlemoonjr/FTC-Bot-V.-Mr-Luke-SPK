package org.firstinspires.ftc.teamcode; 
import com.qualcomm.robotcore.hardware.DcMotor; 
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode; 
import com.qualcomm.robotcore.eventloop.opmode.Autonomous; 
@Autonomous(name="another 87 test", group="Autonomous") 
public class MyFIRSTJavaOpMode extends LinearOpMode { 
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
        move(-0.5, 0.5, 1250);
        move(-0.1, 1, 2300);
        move(-1, 0.1, 4150);
        move(-0.1, 1, 1500);
        } 
        
    } 
    
    public void move(double mL, double mR, int ms){
        left_front.setPower(mL);
        left_back.setPower(mL);
        right_front.setPower(mR);
        right_back.setPower(mR);
        sleep(ms);
        left_back.setPower(0);
        left_front.setPower(0);
        right_front.setPower(0);
        right_back.setPower(0);
        sleep(300);
    }
}  
