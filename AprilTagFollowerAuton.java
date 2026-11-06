package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@Autonomous(name="Autonomous Universal AprilTag Follower", group="Linear Opmode")
public class AprilTagFollowerAuton extends LinearOpMode {

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

    // Declare webcam and vision variables
    private WebcamName webcam = null;
    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;

    // Tuning constants for the tracking behavior
    final double DESIRED_DISTANCE = 12.0; // How close the robot should get to the tag (inches)
    
    // Gain constants to control tracking aggressiveness (Tune these for your specific robot weight/gearing)
    final double SPEED_GAIN  =  0.025;   // Forward/Backward tuning
    final double STRAFE_GAIN =  0.015;   // Left/Right strafe tuning
    final double TURN_GAIN   =  0.015;   // Rotation tuning

    // Maximum limits to ensure the robot doesn't move dangerously fast
    final double MAX_AUTO_SPEED  = 0.50;   
    final double MAX_AUTO_STRAFE = 0.50;   
    final double MAX_AUTO_TURN   = 0.30;   

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initializing Vision...");
        telemetry.update();

        // Initialize drive motor hardware variables
        leftFront  = hardwareMap.get(DcMotor.class, "left_front");
        rightFront = hardwareMap.get(DcMotor.class, "right_front");
        leftBack   = hardwareMap.get(DcMotor.class, "left_back");
        rightBack  = hardwareMap.get(DcMotor.class, "right_back");

        // Initialize intake and shooter motors (keeping consistent with template configuration)
        intake  = hardwareMap.get(DcMotor.class, "intake");
        shooter = hardwareMap.get(DcMotor.class, "shooter");

        // Initialize CR servo hardware variables
        servo0 = hardwareMap.get(CRServo.class, "servo0");
        servo1 = hardwareMap.get(CRServo.class, "servo1");

        // Initialize webcam hardware variable
        webcam = hardwareMap.get(WebcamName.class, "Webcam 1");

        // Set directions matching your hardware configuration
        leftFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.REVERSE);

        intake.setDirection(DcMotor.Direction.REVERSE);
        shooter.setDirection(DcMotor.Direction.REVERSE);

        // Initialize the AprilTag processor
        aprilTag = new AprilTagProcessor.Builder().build();

        // Link the processor to the VisionPortal using your webcam variable
        visionPortal = new VisionPortal.Builder()
                .setCamera(webcam)
                .addProcessor(aprilTag)
                .build();

        telemetry.addData("Status", "Initialized & Ready");
        telemetry.update();

        // Wait for the driver to press PLAY
        waitForStart();

        while (opModeIsActive()) {
            boolean targetFound = false;
            AprilTagDetection desiredTag = null;

            // Get a snapshot list of currently visible AprilTags
            List<AprilTagDetection> currentDetections = aprilTag.getDetections();
            
            // Loop through detections and grab the very first valid detection found
            for (AprilTagDetection detection : currentDetections) {
                // Check if the detection contains valid location data (ftcPose)
                if (detection.ftcPose != null) {
                    targetFound = true;
                    desiredTag = detection;
                    break;  // Target the first valid tag seen in this frame
                }
            }

            double drive  = 0;
            double strafe = 0;
            double turn   = 0;

            if (targetFound) {
                // Determine errors relative to our target placement
                double rangeError  = (desiredTag.ftcPose.range - DESIRED_DISTANCE);
                double headingError = desiredTag.ftcPose.bearing;
                double yawError     = desiredTag.ftcPose.yaw;

                // Use the proportional gains to calculate required motor power variations
                drive  = Range.clip(rangeError * SPEED_GAIN, -MAX_AUTO_SPEED, MAX_AUTO_SPEED);
                turn   = Range.clip(headingError * TURN_GAIN, -MAX_AUTO_TURN, MAX_AUTO_TURN);
                strafe = Range.clip(yawError * STRAFE_GAIN, -MAX_AUTO_STRAFE, MAX_AUTO_STRAFE);

                // If the metadata isn't known to the SDK, it will gracefully show "Unknown Tag Name"
                String tagName = (desiredTag.metadata != null) ? desiredTag.metadata.name : "Unknown Tag Name";
                telemetry.addData("Target Found", "ID %d (%s)", desiredTag.id, tagName);
                telemetry.addData("Range Error (Inches)", "%.2f", rangeError);
                telemetry.addData("Heading Error (Deg)", "%.2f", headingError);
            } else {
                // If no tag is spotted, sit completely still
                telemetry.addData("Target Status", "Searching for ANY AprilTag...");
            }

            // Calculate power for each mecanum wheel using calculated vision inputs
            double lfPower = drive + strafe + turn;
            double rfPower = drive - strafe - turn;
            double lbPower = drive - strafe + turn;
            double rbPower = drive + strafe - turn;

            // Normalize values to prevent clipping anomalies
            double max = Math.max(Math.abs(lfPower), Math.abs(rfPower));
            max = Math.max(max, Math.abs(lbPower));
            max = Math.max(max, Math.abs(rbPower));

            if (max > 1.0) {
                lfPower /= max;
                rfPower /= max;
                lbPower /= max;
                rbPower /= max;
            }

            // Send power commands to the drivetrains
            leftFront.setPower(lfPower);
            rightFront.setPower(rfPower);
            leftBack.setPower(lbPower);
            rightBack.setPower(rbPower);

            // Telemetry diagnostics updates
            telemetry.addData("Drive Power Output", "%.2f", drive);
            telemetry.addData("Strafe Power Output", "%.2f", strafe);
            telemetry.addData("Turn Power Output", "%.2f", turn);
            telemetry.update();
            
            // Minor sleep to relieve processor tension cycles
            sleep(10);
        }
        
        // Clean up and close vision portal safely when opmode ceases
        visionPortal.close();
    }
}
