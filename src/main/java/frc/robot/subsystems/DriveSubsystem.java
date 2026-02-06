package frc.robot.subsystems;

import com.revrobotics.PersistMode;
import com.revrobotics.REVLibError;
import com.revrobotics.ResetMode;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.Constants;

public class DriveSubsystem extends SubsystemBase {
  // If you are NOT using NEOs, change kBrushless -> kBrushed
  private final SparkMax leftFront  = new SparkMax(Constants.Drive.LEFT_FRONT_ID,  MotorType.kBrushless);
  private final SparkMax leftRear   = new SparkMax(Constants.Drive.LEFT_REAR_ID,   MotorType.kBrushless);
  private final SparkMax rightFront = new SparkMax(Constants.Drive.RIGHT_FRONT_ID, MotorType.kBrushless);
  private final SparkMax rightRear  = new SparkMax(Constants.Drive.RIGHT_REAR_ID,  MotorType.kBrushless);

  private final DifferentialDrive drive = new DifferentialDrive(leftFront, rightFront);

  public DriveSubsystem() {
    SparkMaxConfig leftCfg = new SparkMaxConfig();
    leftCfg.smartCurrentLimit(Constants.Drive.CURRENT_LIMIT_AMPS);
    leftCfg.idleMode(IdleMode.kBrake);
    leftCfg.inverted(false);

    SparkMaxConfig rightCfg = new SparkMaxConfig();
    rightCfg.smartCurrentLimit(Constants.Drive.CURRENT_LIMIT_AMPS);
    rightCfg.idleMode(IdleMode.kBrake);
    rightCfg.inverted(Constants.Drive.INVERT_RIGHT_SIDE);

    configOrPrint(leftFront, leftCfg);
    configOrPrint(leftRear, leftCfg);
    configOrPrint(rightFront, rightCfg);
    configOrPrint(rightRear, rightCfg);

    // Use REV follow mode instead of MotorControllerGroup (deprecated in WPILib 2024).
    followOrPrint(leftRear, leftFront, false);
    followOrPrint(rightRear, rightFront, false);

    drive.setDeadband(Constants.Drive.DEADBAND);
  }

  private void configOrPrint(SparkMax spark, SparkMaxConfig cfg) {
    REVLibError err = spark.configure(cfg, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    if (err != REVLibError.kOk) {
      System.out.println("Drive SPARK configure failed (CAN " + spark.getDeviceId() + "): " + err);
    }
  }

  private void followOrPrint(SparkMax follower, SparkMax leader, boolean invert) {
    SparkMaxConfig followCfg = new SparkMaxConfig();
    followCfg.follow(leader, invert);

    REVLibError err = follower.configure(followCfg, ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);
    if (err != REVLibError.kOk) {
      System.out.println("Drive SPARK follow configure failed (CAN " + follower.getDeviceId() + "): " + err);
    }
  }

  /** Left stick controls whole drive (arcade mixing). */
  public void singleStickDrive(double forward, double turn) {
    forward = MathUtil.clamp(forward, -1.0, 1.0);
    turn = MathUtil.clamp(turn, -1.0, 1.0);

    drive.arcadeDrive(forward, turn, true);
  }

  public void stop() {
    drive.stopMotor();
  }
}
