// package org.firstinspires.ftc.teamcode;
// 
// import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
// import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
// import com.qualcomm.robotcore.hardware.DcMotor;
// import org.firstinspires.ftc.robotcore.external.JavaUtil;
// 
// @TeleOp(name = "mecanumdrivesample (Blocks to Java)")
// public class mecanumdrivesample extends LinearOpMode {
// 
//   private DcMotor m0;
//   private DcMotor m1;
//   private DcMotor m2;
//   private DcMotor m3;
// 
//   /**
//    * This function is executed when this Op Mode is selected from the Driver Station.
//    */
//   @Override
//   public void runOpMode() {
//     float y;
//     double x;
//     float rx;
//     double denominator;
// 
//     m0 = hardwareMap.get(DcMotor.class, "frontRightMotorAsDcMotor");
//     m1 = hardwareMap.get(DcMotor.class, "backRightMotorAsDcMotor");
//     m2 = hardwareMap.get(DcMotor.class, "frontLeftMotorAsDcMotor");
//     m3 = hardwareMap.get(DcMotor.class, "backLeftMotorAsDcMotor");
// 
//     // Reverse the right side motors.  This may be wrong for your setup.
//     // If your robot moves backwards when commanded to go forwards, reverse the left side instead.
//     frontRightMotor.setDirection(DcMotor.Direction.REVERSE);
//     backRightMotor.setDirection(DcMotor.Direction.REVERSE);
//     waitForStart();
//     while (opModeIsActive()) {
//       // Remember, Y stick value is reversed
//       y = -gamepad1.left_stick_y;
//       // Factor to counteract imperfect strafing
//       x = gamepad1.left_stick_x * 1.1;
//       rx = gamepad1.right_stick_x;
//       // Denominator is the largest motor power (absolute value) or 1.
//       // This ensures all powers maintain the same ratio, but only if one is outside of the range [-1, 1].
//       denominator = JavaUtil.maxOfList(JavaUtil.createListWith(JavaUtil.sumOfList(JavaUtil.createListWith(Math.abs(y), Math.abs(x), Math.abs(rx))), 1));
//       // Make sure your ID's match your configuration
//       m2.setPower((y + x + rx) / denominator);
//       m3.setPower(((y - x) + rx) / denominator);
//       m0.setPower(((y - x) - rx) / denominator);
//       m1.setPower(((y + x) - rx) / denominator);
//     }
//   }
// }
// 