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
  private final SparkMax upperMotor = new SparkMax(Constants.Feeder.UPPER_MOTOR_ID, MotorType.kBrushed);
  private final SparkMax lowerMotor = new SparkMax(Constants.Feeder.LOWER_MOTOR_ID, MotorType.kBrushed);

  public ShooterSubsystem() {
    SparkMaxConfig upperCfg = new SparkMaxConfig();
    upperCfg.smartCurrentLimit(Constants.Feeder.CURRENT_LIMIT_AMPS);
    upperCfg.idleMode(IdleMode.kBrake);
    upperCfg.inverted(Constants.Feeder.UPPER_INVERT);

    SparkMaxConfig lowerCfg = new SparkMaxConfig();
    lowerCfg.smartCurrentLimit(Constants.Feeder.CURRENT_LIMIT_AMPS);
    lowerCfg.idleMode(IdleMode.kBrake);
    lowerCfg.inverted(Constants.Feeder.LOWER_INVERT);

    configOrPrint(upperMotor, upperCfg);
    configOrPrint(lowerMotor, lowerCfg);
  }

  private void configOrPrint(SparkMax spark, SparkMaxConfig cfg) {
    REVLibError err = spark.configure(cfg, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    if (err != REVLibError.kOk) {
      System.out.println("Shooter SPARK configure failed (CAN " + spark.getDeviceId() + "): " + err);
    }
  }

  public void load() {
    set(Constants.Feeder.LOAD_SPEED);
  }

  /** speed in [-1, 1] */
  public void set(double speed) {
    double output = MathUtil.clamp(speed, -1.0, 1.0);
    upperMotor.set(output);
    lowerMotor.set(output);
  }

  public void stop() {
    upperMotor.stopMotor();
    lowerMotor.stopMotor();
  }
}
