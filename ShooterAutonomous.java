package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;
import org.firstinspires.ftc.vision.apriltag.AprilTagMetadata;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

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

    // Declare webcam & vision variables
    private WebcamName webcam = null;
    private VisionPortal visionPortal;
   
    AprilTagLibrary.Builder aprilTaglib = null;
   
    String CurrentTarget = "Object A";

    // Dynamic power tuning constants (adjust based on robot testing)
    private static final double BASE_SHOOTER_POWER = 0.40;
    private static final double RANGE_POWER_SCALER = 0.015; // Power addition per inch of distance
    private static final double HEIGHT_POWER_SCALER = 0.020; // Power addition per inch of elevation

    @Override
    public void runOpMode() {
        aprilTaglib = new AprilTagLibrary.Builder();
       
        setupLibrary();
           
        AprilTagLibrary customLibraryAprilTag = aprilTaglib.build();
        AprilTagProcessor aprilTag = new AprilTagProcessor.Builder()
            .setTagLibrary(customLibraryAprilTag)
            .build();
       
        webcam = hardwareMap.get(WebcamName.class, "mr luki");

        // Link the processor to the VisionPortal using webcam variable
        visionPortal = new VisionPortal.Builder()
                .setCamera(webcam)
                .addProcessor(aprilTag)
                .build();

        // Initialize drive motors
        leftFront  = hardwareMap.get(DcMotor.class, "left_front");
        rightFront = hardwareMap.get(DcMotor.class, "right_front");
        leftBack   = hardwareMap.get(DcMotor.class, "left_back");
        rightBack  = hardwareMap.get(DcMotor.class, "right_back");

        // Initialize intake and shooter motors
        intake  = hardwareMap.get(DcMotor.class, "intake");
        shooter = hardwareMap.get(DcMotor.class, "shooter");
       
        // Safely initialize CR servos if present in config
        try {
            servo0 = hardwareMap.get(CRServo.class, "servo0");
            servo1 = hardwareMap.get(CRServo.class, "servo1");
        } catch (Exception e) {
            telemetry.addData("Warning", "CR Servos not found in hardware map");
        }
       
        telemetry.addData("Status", "Initialized");
        telemetry.update();
       
        waitForStart();
       
        boolean hasFired = false;

        while (opModeIsActive()) {
            boolean targetFound = false;
            AprilTagDetection desiredTag = null;

            // Get a snapshot list of currently visible AprilTags
            List<AprilTagDetection> currentDetections = aprilTag.getDetections();
            int amount = currentDetections.size();
           
            // Search detections for CurrentTarget
            for (AprilTagDetection detection : currentDetections) {
                if (detection.ftcPose != null && detection.metadata != null) {
                    if (CurrentTarget.equals(detection.metadata.name)) {
                        targetFound = true;
                        desiredTag = detection;
                        break;
                    }
                }
            }
           
            if (targetFound && desiredTag != null && !hasFired) {
                double range = desiredTag.ftcPose.range;        // Distance to tag in inches
                double elevation = desiredTag.ftcPose.z;      // Target height offset relative to camera
                double headingError = desiredTag.ftcPose.bearing; // Horizontal bearing in degrees

                telemetry.addData("Target Found", desiredTag.metadata.name);
                telemetry.addData("Range (in)", Math.round(range * 100.0) / 100.0);
                telemetry.addData("Elevation (z)", Math.round(elevation * 100.0) / 100.0);
                telemetry.addData("Heading Error (deg)", Math.round(headingError * 100.0) / 100.0);

                // Calculate turning steering power proportional to heading error
                double steering = Range.clip(headingError * 0.02, -0.3, 0.3);

                if (Math.abs(headingError) > 2.0) {
                    // Turn robot towards the tag center
                    setDrivePower(-steering, steering);
                    telemetry.addData("Status", "Aiming...");
                } else {
                    // Target centered! Stop drive motors.
                    setDrivePower(0, 0);

                    // Dynamically calculate power based on target height elevation and distance range
                    double calculatedPower = BASE_SHOOTER_POWER
                                           + (range * RANGE_POWER_SCALER)
                                           + (elevation * HEIGHT_POWER_SCALER);

                    // Restrict calculated power strictly within 0.0 and 1.0 motor limits
                    calculatedPower = Range.clip(calculatedPower, 0.0, 1.0);

                    telemetry.addData("Status", "Target Aimed!");
                    telemetry.addData("Calculated Power", calculatedPower);
                    telemetry.update();

                    // Start spinning shooter flywheel at dynamic speed
                    shooter.setPower(calculatedPower);

                    // Wait 5 seconds while holding position and spinning up shooter
                    sleep(5000);

                    // Feed game piece into shooter using servos
                    if (servo0 != null) servo0.setPower(1.0);
                    if (servo1 != null) servo1.setPower(1.0);
                   
                    sleep(1500); // Allow time to feed the game piece

                    // Stop all mechanisms
                    if (servo0 != null) servo0.setPower(0.0);
                    if (servo1 != null) servo1.setPower(0.0);
                    shooter.setPower(0.0);

                    hasFired = true; // Mark completed to avoid repeated shots
                }
            } else if (!hasFired) {
                // Rotate to scan for target if not visible yet
                setDrivePower(0.2, -0.2);
                telemetry.addData("Status", "Searching for " + CurrentTarget + "...");
            } else {
                // Completed firing sequence
                setDrivePower(0, 0);
                telemetry.addData("Status", "Firing sequence complete.");
            }
           
            telemetry.addData("Currently Detecting", amount);
            telemetry.update();
        }
    }
   
    public void setupLibrary() {
        createLocation(30, "Object A", 3.1, DistanceUnit.INCH);
        createLocation(31, "Object B", 3.1, DistanceUnit.INCH);
        createLocation(32, "Object C", 3.1, DistanceUnit.INCH);
        createLocation(33, "Object D", 3.1, DistanceUnit.INCH);

        createLocation(50, "Hive", 3.1, DistanceUnit.INCH);
    }

    public void createLocation(int ID, String Name, double AprilTagSize, DistanceUnit Measurement) {
        aprilTaglib.addTag(new AprilTagMetadata(
            ID,
            Name,
            AprilTagSize,
            Measurement
        ));
    }
   
    public void setDrivePower(double left, double right) {
        leftFront.setPower(left);
        leftBack.setPower(left);
       
        rightFront.setPower(right);
        rightBack.setPower(right);
    }
}
