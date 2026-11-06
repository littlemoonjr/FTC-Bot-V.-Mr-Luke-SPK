package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;
import org.firstinspires.ftc.vision.apriltag.AprilTagMetadata;

import java.util.List;

@TeleOp(name="ShooterAutonomous", group="Linear Opmode")
public class ShooterAutonomous extends LinearOpMode {

    // Declare hardware variables for 4 drive motors
    private DcMotor leftFront = null;
    private DcMotor rightFront = null;
    private DcMotor leftBack = null;
    private DcMotor rightBack = null;

    // Declare hardware variables for additional Expansion Hub motors
    private DcMotor intake = null;
    private DcMotor shooter = null;

    // Declare hardware variables for the 2 Continuous Rotation servos
    private CRServo servo0 = null;
    private CRServo servo1 = null;

    // Declare webcam variable
    private WebcamName webcam = null;
    private VisionPortal visionPortal;

    @Override
    public void runOpMode() {
        AprilTagLibrary.Builder aprilTaglib = new AprilTagLibrary.Builder();
        
        aprilTaglib.addTag(new AprilTagMetadata(
            33,
            "A Shoot Station",
            3.1,
            DistanceUnit.INCH
            ));
            
        aprilTaglib.addTag(new AprilTagMetadata(
            32,
            "A Shoot Station",
            3.1,
            DistanceUnit.INCH
            ));
            
        aprilTaglib.addTag(new AprilTagMetadata(
            31,
            "A Shoot Station",
            3.1,
            DistanceUnit.INCH
            ));
            
        aprilTaglib.addTag(new AprilTagMetadata(
            30,
            "Home",
            3.1,
            DistanceUnit.INCH
            ));
            
        AprilTagLibrary customLibraryAprilTag = aprilTaglib.build();
        AprilTagProcessor aprilTag = new AprilTagProcessor.Builder()
            .setTagLibrary(customLibraryAprilTag)
            .build();
        
        
        
        webcam = hardwareMap.get(WebcamName.class, "mr luki");


        // Link the processor to the VisionPortal using your webcam variable
        visionPortal = new VisionPortal.Builder()
                .setCamera(webcam)
                .addProcessor(aprilTag)
                .build();

        leftFront  = hardwareMap.get(DcMotor.class, "left_front");
        rightFront = hardwareMap.get(DcMotor.class, "right_front");
        leftBack   = hardwareMap.get(DcMotor.class, "left_back");
        rightBack  = hardwareMap.get(DcMotor.class, "right_back");

        // Initialize intake and shooter motors (configured on Expansion Hub)
        intake  = hardwareMap.get(DcMotor.class, "intake");
        shooter = hardwareMap.get(DcMotor.class, "shooter");
        
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        
        waitForStart();
        
        while (opModeIsActive()) {
            boolean targetFound = false;
            AprilTagDetection desiredTag = null;

            // Get a snapshot list of currently visible AprilTags
            List<AprilTagDetection> currentDetections = aprilTag.getDetections();
            int amount = 0;
            
            // Loop through detections and grab the very first valid detection found
            for (AprilTagDetection detection : currentDetections) {
                // Check if the detection contains valid location data (ftcPose)
                
                amount += 1;
                
                if (detection.ftcPose != null && detection.metadata != null) {
                    double x = detection.ftcPose.x;
                    double y = detection.ftcPose.y;
                    telemetry.addData("Status", "Target: (" + x + "," + y + ")" + " | " + detection.metadata.name);
                    
                    //telemetry.addData("Proposed Power Needed: ", detection.centerY * 0.05);
                }
                
                //if (detection.ftcPose != null) {
                    //telemetry.addData("Status", detection);
                //}
            }
            
            telemetry.addData("Currently Detecting", amount);
            telemetry.update();
        }
        
        //if (opModeIsActive()); { 
        //   intake.setPower(1);
        //   shooter.setPower(0.6);
           
         //  sleep(5000);
           
        //   setDrivePower(0.5, -0.4);
           
        //   sleep(600);
          
       //    shooter.setPower(0.5);
        //   sleep(5000);
        //   setDrivePower(0.5,-0.4);
       //    sleep(600);
           
        //}
        
    }
    
    public void setDrivePower(double left, double right) {
        leftFront.setPower(left);
        leftBack.setPower(left);
        
        rightFront.setPower(right);
        rightBack.setPower(right);
    }
}
