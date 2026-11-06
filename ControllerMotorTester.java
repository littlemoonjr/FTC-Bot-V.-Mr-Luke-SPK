package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

@TeleOp(name="4-Motor Drive with Servos, Intake, Shooter & Webcam", group="Linear Opmode")
public class ControllerMotorTester extends LinearOpMode {

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

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        
        double power = 0.25;

        // Initialize drive motor hardware variables
        leftFront  = hardwareMap.get(DcMotor.class, "left_front");
        rightFront = hardwareMap.get(DcMotor.class, "right_front");
        leftBack   = hardwareMap.get(DcMotor.class, "left_back");
        rightBack  = hardwareMap.get(DcMotor.class, "right_back");

        // Initialize intake and shooter motors (configured on Expansion Hub)
        intake  = hardwareMap.get(DcMotor.class, "intake");
        shooter = hardwareMap.get(DcMotor.class, "shooter");

        // Initialize CR servo hardware variables
        servo0 = hardwareMap.get(CRServo.class, "servo0");
        servo1 = hardwareMap.get(CRServo.class, "servo1");

        // Initialize webcam hardware variable
        webcam = hardwareMap.get(WebcamName.class, "mr luki");

        // Reverse left side drive motors because they face the opposite direction
        leftFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.REVERSE);

        // Set direction for intake and shooter motors (adjust REVERSE if needed for physical mounting)
        intake.setDirection(DcMotor.Direction.REVERSE);
        shooter.setDirection(DcMotor.Direction.REVERSE);

        // Wait for the driver to press PLAY
        waitForStart();

        while (opModeIsActive()) {

            // Mecanum Drive Controls:
            // Left Stick Y: Forward/Backward
            // Left Stick X: Strafe Sideways
            // Right Stick X: Turn Left/Right
            double drive  = -gamepad1.left_stick_y;
            double strafe =  gamepad1.left_stick_x;
            double turn   =  gamepad1.right_stick_x;

            // Define default speed multiplier (60% power for control)
            double speedMultiplier = 0.6;

            // BOOST FEATURE: If Right Bumper is pressed, increase drive speed to 100%
            if (gamepad1.right_bumper) {
                speedMultiplier = 1.0;
            }

            // CR SERVO FEATURE: If B button is pressed, spin servo0 (clockwise) and servo1 (counterclockwise). Otherwise, stop.
            if (gamepad1.b) {
                servo0.setPower(1.0);   // Clockwise
                servo1.setPower(-1.0);  // Counterclockwise
            } else {
                servo0.setPower(0.0);   // Stop
                servo1.setPower(0.0);   // Stop
            }

            // INTAKE FEATURE:
            // Left Bumper = Intake forward (100% power)
            // Left Trigger = Reverse intake / eject (proportional to trigger press)
            if (gamepad1.left_bumper) {
                intake.setPower(1.0);
            } else if (gamepad1.left_trigger > 0.1) {
                intake.setPower(-gamepad1.left_trigger);
            } else {
                intake.setPower(0.0);
            }

            // SHOOTER FEATURE: Controlled by Right Trigger (variable velocity up to 100%)
            
            
            if (gamepad1.dpad_down) {
                power -= 0.001;
            }
            if (gamepad1.dpad_up) {
                power += 0.001;
            }
            
            shooter.setPower(gamepad1.right_trigger * power);
            
            
            

            // Calculate power for each mecanum wheel
            double lfPower = drive + strafe + turn;
            double rfPower = drive - strafe - turn;
            double lbPower = drive - strafe + turn;
            double rbPower = drive + strafe - turn;

            // Normalize values so no motor power exceeds 1.0, keeping movements proportional
            double max = Math.max(Math.abs(lfPower), Math.abs(rfPower));
            max = Math.max(max, Math.abs(lbPower));
            max = Math.max(max, Math.abs(rbPower));

            if (max > 1.0) {
                lfPower /= max;
                rfPower /= max;
                lbPower /= max;
                rbPower /= max;
            }

            // Apply the speed multiplier and send power to motors
            leftFront.setPower(lfPower * speedMultiplier);
            rightFront.setPower(rfPower * speedMultiplier);
            leftBack.setPower(lbPower * speedMultiplier);
            rightBack.setPower(rbPower * speedMultiplier);

            // Telemetry updates
            telemetry.addData("Power of Shooter", power);
            telemetry.addData("Status", "Running");
            telemetry.addData("Boost Active", gamepad1.right_bumper ? "YES (100%)" : "NO (60%)");
            telemetry.addData("Servo Status", gamepad1.b ? "SPINNING" : "STOPPED");
            telemetry.addData("Intake Power", "%.2f", intake.getPower());
            telemetry.addData("Shooter Power", "%.2f", shooter.getPower());
            telemetry.addData("Webcam Initialized", webcam != null ? "YES (" + webcam.getDeviceName() + ")" : "NO");
            telemetry.addData("Front Motors", "LF (%.2f), RF (%.2f)", lfPower * speedMultiplier, rfPower * speedMultiplier);
            telemetry.addData("Back Motors", "LB (%.2f), RB (%.2f)", lbPower * speedMultiplier, rbPower * speedMultiplier);
            telemetry.update();
        }
    }
}
