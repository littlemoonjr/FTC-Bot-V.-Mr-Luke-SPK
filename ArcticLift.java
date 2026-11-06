// package org.firstinspires.ftc.teamcode;
// 
// import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
// import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
// import com.qualcomm.robotcore.hardware.DcMotor;
// import com.qualcomm.robotcore.hardware.Servo;
// 
// @TeleOp(name = "Arctic Dual Stick Drive")
// public class ArcticLift extends LinearOpMode {
// 
//     private DcMotor leftmotor;
//     private DcMotor rightmotor;
//     private DcMotor liftmotor;
//     private Servo clawservoright;
//     private Servo clawservoleft;
// 
//     // === Adjustable power multipliers ===
//     private double leftDriveMultiplier = 1.0; // Left Motor Power
//     private double rightDriveMultiplier = 1.0; // Right Motor Power
//     private double liftPowerMultiplier = 1.0; // Lift Motor Power
// 
//     // === Claw servo positions ===
//     private final double CLAW_OPEN_RIGHT = 0.6;
//     private final double CLAW_OPEN_LEFT = 0.5;
//     private final double CLAW_CLOSED_RIGHT = 0.2;
//     private final double CLAW_CLOSED_LEFT = 0.9;
// 
//     @Override
//     public void runOpMode() {
//         // Hardware Mapping
//         leftmotor = hardwareMap.get(DcMotor.class, "leftmotor");
//         rightmotor = hardwareMap.get(DcMotor.class, "rightmotor");
//         liftmotor = hardwareMap.get(DcMotor.class, "liftmotor");
//         clawservoright = hardwareMap.get(Servo.class, "clawservoright");
//         clawservoleft = hardwareMap.get(Servo.class, "clawservoleft");
// 
//         // Reverse one motor to ensure forward is forward
//         rightmotor.setDirection(DcMotor.Direction.REVERSE);
// 
//         telemetry.addData("Status", "Initialized");
//         telemetry.update();
// 
//         waitForStart();
// 
//         while (opModeIsActive()) {
//             // Drive control
//             double leftPower = -gamepad1.left_stick_y * leftDriveMultiplier;
//             double rightPower = -gamepad1.right_stick_y * rightDriveMultiplier;
// 
//             // Lift control
//             double liftUp = gamepad1.left_trigger;
//             double liftDown = gamepad1.right_trigger;
//             double liftPower = (liftUp - liftDown) * liftPowerMultiplier;
// 
//             leftmotor.setPower(leftPower);
//             rightmotor.setPower(rightPower);
//             liftmotor.setPower(liftPower);
// 
//             // Claw control: B to open, X to close
//             if (gamepad1.b) { // Open claw
//                 clawservoright.setPosition(CLAW_OPEN_RIGHT);
//                 clawservoleft.setPosition(CLAW_OPEN_LEFT);
//             } else if (gamepad1.x) { // Close claw
//                 clawservoright.setPosition(CLAW_CLOSED_RIGHT);
//                 clawservoleft.setPosition(CLAW_CLOSED_LEFT);
//             }
// 
//             // Debugging / telemetry
//             telemetry.addData("Left Motor Power", leftPower);
//             telemetry.addData("Right Motor Power", rightPower);
//             telemetry.addData("Lift Motor Power", liftPower);
//             telemetry.addData("Right Servo Position", clawservoright.getPosition());
//             telemetry.addData("Left Servo Position", clawservoleft.getPosition());
//             telemetry.update();
//         }
//     }
// }
// 