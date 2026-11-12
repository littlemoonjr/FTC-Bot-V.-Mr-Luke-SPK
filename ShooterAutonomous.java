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

// Coded by Ethan A & updated to use webcam vision for distance tracking

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

    // Declare hardware variables for Continuous Rotation servos
    private CRServo servo0 = null;
    private CRServo servo1 = null;
    private CRServo bringservo = null;

    // Declare webcam & vision variables
    private WebcamName webcam = null;
    private VisionPortal visionPortal;

    AprilTagLibrary.Builder aprilTaglib = null;

    String CurrentTarget = "Object A";

    // Dynamic power tuning constants
    private static final double BASE_SHOOTER_POWER = 0.40;
    private static final double RANGE_POWER_SCALER = 0.015;
    private static final double HEIGHT_POWER_SCALER = 0.020;

    private enum State {
        SEARCHING_AND_COLLECTING,
        try {
            servo0 = hardwareMap.get(CRServo.class, "servo0");
            servo1 = hardwareMap.get(CRServo.class, "servo1");
            bringservo = hardwareMap.get(CRServo.class, "bring");
        } catch (Exception e) {
            telemetry.addData("Warning", "CR Servos not found in hardware map");
        }

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        State currentState = State.SEARCHING_AND_COLLECTING;
        boolean hasFired = false;

        while (opModeIsActive()) {
            boolean targetFound = false;
            AprilTagDetection desiredTag = null;
            double webcamDistance = 999.0; // Default large distance if no tag seen

            // Get a snapshot list of currently visible AprilTags from the webcam
            List<AprilTagDetection> currentDetections = aprilTag.getDetections();
           
            for (AprilTagDetection detection : currentDetections) {
                if (detection.ftcPose != null && detection.metadata != null) {
                    // Check if this is our specific shooting target
                    if (CurrentTarget.equals(detection.metadata.name)) {
                        targetFound = true;
                        desiredTag = detection;
                    }
                    // Use webcam distance measurement from any visible tag for proximity checks
                    webcamDistance = detection.ftcPose.range;
                }
            }

            // State Machine Transition based on Webcam Vision
            if (targetFound && !hasFired) {
                currentState = State.AIMING_AND_FIRING;
            }

            switch (currentState) {
                case SEARCHING_AND_COLLECTING:
                    // Keep intake running continuously to scoop up balls
                    intake.setPower(-0.5);

                    // If the webcam detects we are too close to a wall/tag (< 12 inches), back up or turn to avoid it
                    if (webcamDistance < 12.0) {
                        setDrivePower(-0.3, 0.3); // Turn away from obstacle
                        telemetry.addData("Status", "Webcam Obstacle Avoidance: Turning...");
                    } else {
                        // Roam around searching for balls while intake is active
                        setDrivePower(0.3, 0.25);
                        telemetry.addData("Status", "Searching for balls & collecting...");
                    }
                    break;

                case AIMING_AND_FIRING:
                    intake.setPower(0.0); // Stop intake during firing sequence

                    if (desiredTag != null) {
                        double range = desiredTag.ftcPose.range;
                        double elevation = desiredTag.ftcPose.z;
                        double headingError = desiredTag.ftcPose.bearing;

                        telemetry.addData("Target Found", desiredTag.metadata.name);
                        telemetry.addData("Webcam Range (in)", Math.round(range * 100.0) / 100.0);

                        double steering = Range.clip(headingError * 0.02, -0.3, 0.3);

                        if (Math.abs(headingError) > 2.0) {
                            // Align horizontally using webcam bearing error
                            setDrivePower(-steering, steering);
                            telemetry.addData("Status", "Aiming at AprilTag via Webcam...");
                        } else {
                            // Centered perfectly! Stop moving.
                            setDrivePower(0, 0);

                            // Calculate dynamic shooter power using webcam range and elevation data
                            double calculatedPower = BASE_SHOOTER_POWER
                                                   + (range * RANGE_POWER_SCALER)
                                                   + (elevation * HEIGHT_POWER_SCALER);
                            calculatedPower = Range.clip(calculatedPower, 0.0, 1.0);

                            shooter.setPower(calculatedPower);
                            telemetry.addData("Status", "Spinning up shooter...");
                            telemetry.update();

                            // Spin up delay
                            sleep(5000);

                            // Feed ball into ramp using bringservo and indexing servos
                            if (bringservo != null) bringservo.setPower(1.0);
                            if (servo0 != null) servo0.setPower(1.0);
                            if (servo1 != null) servo1.setPower(1.0);

                            sleep(1500); // Feeding duration

                            // Stop mechanisms
                            if (bringservo != null) bringservo.setPower(0.0);
                            if (servo0 != null) servo0.setPower(0.0);
                            if (servo1 != null) servo1.setPower(0.0);
                            shooter.setPower(0.0);

                            hasFired = true;
                            currentState = State.SEARCHING_AND_COLLECTING;
                        }
                    }
                    break;
            }

            telemetry.addData("Current State", currentState);
            telemetry.addData("Webcam Distance to Tag", webcamDistance);

            telemetry.update();
        }
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

    public void setupLibrary() {
        createLocation(30, "Object A", 3.1, DistanceUnit.INCH);
        createLocation(31, "Object B", 3.1, DistanceUnit.INCH);
        createLocation(32, "Object C", 3.1, DistanceUnit.INCH);
        createLocation(33, "Object D", 3.1, DistanceUnit.INCH);
        createLocation(50, "Hive", 3.1, DistanceUnit.INCH);
    }
}
