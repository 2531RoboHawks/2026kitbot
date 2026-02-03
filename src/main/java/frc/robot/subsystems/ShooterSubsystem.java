package frc.robot.subsystems;

import com.revrobotics.PersistMode;
import com.revrobotics.REVLibError;
import com.revrobotics.ResetMode;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.Constants;

public class ShooterSubsystem extends SubsystemBase {
  // BRUSHED shooter motors:
  private final SparkMax leftShooter  = new SparkMax(Constants.Shooter.LEFT_SHOOTER_ID, MotorType.kBrushed);
  private final SparkMax rightShooter = new SparkMax(Constants.Shooter.RIGHT_SHOOTER_ID, MotorType.kBrushed);

  public ShooterSubsystem() {
    SparkMaxConfig leftCfg = new SparkMaxConfig();
    leftCfg.smartCurrentLimit(Constants.Shooter.CURRENT_LIMIT_AMPS);
    leftCfg.idleMode(IdleMode.kCoast);
    leftCfg.inverted(false);

    SparkMaxConfig rightCfg = new SparkMaxConfig();
    rightCfg.smartCurrentLimit(Constants.Shooter.CURRENT_LIMIT_AMPS);
    rightCfg.idleMode(IdleMode.kCoast);
    rightCfg.inverted(Constants.Shooter.INVERT_RIGHT_SHOOTER);

    configOrPrint(leftShooter, leftCfg);
    configOrPrint(rightShooter, rightCfg);
  }

  private void configOrPrint(SparkMax spark, SparkMaxConfig cfg) {
    REVLibError err = spark.configure(cfg, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    if (err != REVLibError.kOk) {
      System.out.println("Shooter SPARK configure failed (CAN " + spark.getDeviceId() + "): " + err);
    }
  }

  /** speed in [-1, 1] */
  public void set(double speed) {
    speed = MathUtil.clamp(speed, -1.0, 1.0);
    leftShooter.set(speed);
    rightShooter.set(speed);
  }

  public void stop() {
    leftShooter.stopMotor();
    rightShooter.stopMotor();
  }
}
