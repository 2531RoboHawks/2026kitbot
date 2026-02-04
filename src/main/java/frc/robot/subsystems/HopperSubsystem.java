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

public class HopperSubsystem extends SubsystemBase {
  private final SparkMax hopperMotor = new SparkMax(Constants.Hopper.MOTOR_ID, MotorType.kBrushed);

  public HopperSubsystem() {
    SparkMaxConfig cfg = new SparkMaxConfig();
    cfg.smartCurrentLimit(Constants.Hopper.CURRENT_LIMIT_AMPS);
    cfg.idleMode(IdleMode.kBrake);
    cfg.inverted(Constants.Hopper.INVERT);

    configOrPrint(hopperMotor, cfg);
  }

  private void configOrPrint(SparkMax spark, SparkMaxConfig cfg) {
    REVLibError err = spark.configure(cfg, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    if (err != REVLibError.kOk) {
      System.out.println("Hopper SPARK configure failed (CAN " + spark.getDeviceId() + "): " + err);
    }
  }

  public void load() {
    set(Constants.Hopper.LOAD_SPEED);
  }

  /** speed in [-1, 1] */
  public void set(double speed) {
    hopperMotor.set(MathUtil.clamp(speed, -1.0, 1.0));
  }

  public void stop() {
    hopperMotor.stopMotor();
  }
}
