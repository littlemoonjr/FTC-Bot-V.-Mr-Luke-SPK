// package org.firstinspires.ftc.teamcode;
// 
// import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
// import com.qualcomm.robotcore.hardware.Blinker;
// import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
// import com.qualcomm.robotcore.hardware.DcMotor;
// import com.qualcomm.robotcore.hardware.IMU;
// 
// @TeleOp(name = "Arctic Dual Stick Drive")
// public class Arctic extends LinearOpMode {
//     private Blinker control_Hub;
//     private DcMotor leftmotor;
//     private DcMotor rightmotor;
//     private IMU imu;
// 
//     @Override
//     public void runOpMode() {
//         // **Hardware Mapping**
//         control_Hub = hardwareMap.get(Blinker.class, "Control Hub");
//         leftmotor = hardwareMap.get(DcMotor.class, "leftmotor");
//         rightmotor = hardwareMap.get(DcMotor.class, "rightmotor");
// 
//         // **Reverse one motor for proper movement**
//         leftmotor.setDirection(DcMotor.Direction.REVERSE);
// 
//         telemetry.addData("Status", "Initialized");
//         telemetry.update();
// 
//         // **Wait for PLAY button**
//         waitForStart();
// 
//         while (opModeIsActive()) {
//             // **Left Stick controls Left Motor, Right Stick controls Right Motor**
//             double leftPower = gamepad1.left_stick_y;  // Up = Forward, Down = Backward
//             double rightPower = gamepad1.right_stick_y;
// 
//             double speedFactor = 0.5;  // Adjust this to make the robot slower (0.5 = 50% speed)
//             leftmotor.setPower(leftPower * speedFactor);
//             rightmotor.setPower(rightPower * speedFactor);
// 
//             // **Telemetry Debug Info**
//             telemetry.addData("Left Motor Power", leftPower);
//             telemetry.addData("Right Motor Power", rightPower);
//             telemetry.update();
//         }
//     }
// }
// 