package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class Robot extends TimedRobot {
  @SuppressWarnings("unused")
  private RobotContainer robotContainer;

  // Deadband for direction indicators so they don't flicker
  private static final double DIR_DEADBAND = 0.15;

  @Override
  public void robotInit() {
    robotContainer = new RobotContainer();
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();

    if (robotContainer == null) {
      return;
    }

    var driver = robotContainer.getDriver();

    // Raw axes
    double rightx = driver.getRightX();
    double leftY = driver.getLeftY();
    double rt = driver.getRightTriggerAxis();
    double lt = driver.getLeftTriggerAxis();

    // Match your drive sign convention (RobotContainer uses -getLeftY())
    double forwardCmd = MathUtil.applyDeadband(-leftY, DIR_DEADBAND);
    double turnCmd = MathUtil.applyDeadband(rightx, DIR_DEADBAND);

    // Direction booleans (Up = stick forward -> leftY is negative)
    boolean up = (-leftY) > DIR_DEADBAND;
    boolean down = (-leftY) < -DIR_DEADBAND;
    boolean right = rightx > DIR_DEADBAND;
    boolean left = rightx < -DIR_DEADBAND;

    // Publish for Elastic
    SmartDashboard.putNumber("Controller/rightx", rightx);
    SmartDashboard.putNumber("Controller/LeftY", leftY);

    SmartDashboard.putNumber("Controller/ForwardCmd", forwardCmd);
    SmartDashboard.putNumber("Controller/TurnCmd", turnCmd);

    SmartDashboard.putNumber("Controller/LT_percent", lt * 100.0);
    SmartDashboard.putNumber("Controller/RT_percent", rt * 100.0);

    SmartDashboard.putBoolean("Controller/Dir/Up", up);
    SmartDashboard.putBoolean("Controller/Dir/Down", down);
    SmartDashboard.putBoolean("Controller/Dir/Left", left);
    SmartDashboard.putBoolean("Controller/Dir/Right", right);

    // Optional (handy to show shooter priority inputs)
    SmartDashboard.putBoolean("Controller/LB", driver.getLeftBumper());
    SmartDashboard.putBoolean("Controller/RB", driver.getRightBumper());
  }
}
