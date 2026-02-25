package frc.robot.subsystems;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.configs.CANdleConfiguration;
import com.ctre.phoenix6.controls.SolidColor;
import com.ctre.phoenix6.hardware.CANdle;
import com.ctre.phoenix6.signals.Enable5VRailValue;
import com.ctre.phoenix6.signals.RGBWColor;
import com.ctre.phoenix6.signals.StripTypeValue;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.Constants;

public class CandleSubsystem extends SubsystemBase {
  private static final double MIN_COLOR = 0.0;
  private static final double MAX_COLOR = 255.0;

  private final CANdle candle =
      new CANdle(Constants.CANdle.CAN_ID, new CANBus(Constants.CANdle.CAN_BUS));
  private final SolidColor solidColor =
      new SolidColor(Constants.CANdle.LED_START, Constants.CANdle.LED_END);

  private int lastR = -1;
  private int lastG = -1;
  private int lastB = -1;

  public CandleSubsystem() {
    CANdleConfiguration config = new CANdleConfiguration();
    config.LED.StripType = resolveStripType(Constants.CANdle.STRIP_TYPE);
    config.LED.BrightnessScalar = 1.0;
    config.CANdleFeatures.Enable5VRail = Enable5VRailValue.Enabled;

    StatusCode status = candle.getConfigurator().apply(config);
    SmartDashboard.putString(Constants.CANdle.KEY_CONFIG_STATUS, status.toString());
  }

  @Override
  public void periodic() {
    boolean off = SmartDashboard.getBoolean(Constants.CANdle.KEY_OFF, true);
    boolean purpleGold = SmartDashboard.getBoolean(Constants.CANdle.KEY_PURPLE_GOLD, false);

    if (off) {
      if (purpleGold) {
        SmartDashboard.putBoolean(Constants.CANdle.KEY_PURPLE_GOLD, false);
      }
      applyColor(0, 0, 0);
      return;
    }

    if (purpleGold) {
      double blend = autoBlend();
      applyColor(
          lerp(Constants.CANdle.PURPLE_R, Constants.CANdle.GOLD_R, blend),
          lerp(Constants.CANdle.PURPLE_G, Constants.CANdle.GOLD_G, blend),
          lerp(Constants.CANdle.PURPLE_B, Constants.CANdle.GOLD_B, blend)
      );
      return;
    }

    applyColor(
        readColor(Constants.CANdle.KEY_R),
        readColor(Constants.CANdle.KEY_G),
        readColor(Constants.CANdle.KEY_B)
    );
  }

  private int readColor(String key) {
    double value = SmartDashboard.getNumber(key, 0.0);
    return (int) Math.round(MathUtil.clamp(value, MIN_COLOR, MAX_COLOR));
  }

  private void applyColor(int r, int g, int b) {
    if (r == lastR && g == lastG && b == lastB) {
      return;
    }

    lastR = r;
    lastG = g;
    lastB = b;

    StatusCode status = candle.setControl(solidColor.withColor(new RGBWColor(r, g, b)));
    SmartDashboard.putString(Constants.CANdle.KEY_STATUS, status.toString());
    SmartDashboard.putNumber(Constants.CANdle.KEY_APPLIED_R, r);
    SmartDashboard.putNumber(Constants.CANdle.KEY_APPLIED_G, g);
    SmartDashboard.putNumber(Constants.CANdle.KEY_APPLIED_B, b);
    setDashboardColor(r, g, b);
  }

  private void setDashboardColor(int r, int g, int b) {
    if (SmartDashboard.getNumber(Constants.CANdle.KEY_R, -1) != r) {
      SmartDashboard.putNumber(Constants.CANdle.KEY_R, r);
    }
    if (SmartDashboard.getNumber(Constants.CANdle.KEY_G, -1) != g) {
      SmartDashboard.putNumber(Constants.CANdle.KEY_G, g);
    }
    if (SmartDashboard.getNumber(Constants.CANdle.KEY_B, -1) != b) {
      SmartDashboard.putNumber(Constants.CANdle.KEY_B, b);
    }
  }

  private double autoBlend() {
    double period = Constants.CANdle.AUTO_CYCLE_SECONDS;
    if (period <= 0.0) {
      period = 4.0;
    }
    double time = Timer.getFPGATimestamp();
    double phase = (time % period) / period;
    return 0.5 - 0.5 * Math.cos(2.0 * Math.PI * phase);
  }

  private int lerp(int from, int to, double alpha) {
    double value = from + (to - from) * alpha;
    return (int) Math.round(MathUtil.clamp(value, MIN_COLOR, MAX_COLOR));
  }

  private StripTypeValue resolveStripType(String stripType) {
    if (stripType == null) {
      return StripTypeValue.GRB;
    }
    String normalized = stripType.trim().toUpperCase();
    if ("RGB".equals(normalized)) {
      return StripTypeValue.RGB;
    }
    return StripTypeValue.GRB;
  }
}
