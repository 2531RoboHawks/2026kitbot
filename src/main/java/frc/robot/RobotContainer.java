package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.RunCommand;

import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

public class RobotContainer {
  private final DriveSubsystem drive = new DriveSubsystem();
  private final ShooterSubsystem shooter = new ShooterSubsystem();

  private final XboxController driver = new XboxController(Constants.DRIVER_CONTROLLER_PORT);
  private final Timer rtDelayTimer = new Timer();
  private boolean rtWasActive = false;

  public RobotContainer() {
    // Seed debug tunables so they show up on the dashboard
    SmartDashboard.putNumber("Shooter/LoadRight (debug)", Constants.Feeder.LOAD_RIGHT_SPEED);
    SmartDashboard.putNumber("Shooter/LoadLeft (debug)", Constants.Feeder.LOAD_LEFT_SPEED);

    // Drive: LEFT stick does everything
    drive.setDefaultCommand(
        new RunCommand(
            () -> drive.singleStickDrive(
                -driver.getLeftY(),
                -driver.getRightX() * Constants.Drive.TURN_SCALE
            ),
            drive
        )
    );

    // Shared shooter/feeder motors control:
    // Priority: LB reverse > RB full > RT variable > LT load > B manual > stop
    shooter.setDefaultCommand(
        new RunCommand(
            () -> {
              if (driver.getLeftBumper()) {
                resetRtDelay();
                shooter.set(Constants.Shooter.REVERSE_SPEED);
              } else if (driver.getRightBumper()) {
                resetRtDelay();
                shooter.set(Constants.Shooter.FULL_SPEED);
              } else {
                double rt = driver.getRightTriggerAxis();
                if (rt > Constants.Shooter.TRIGGER_DEADBAND) {
                  runRtShooter(rt);
                  return;
                }
                resetRtDelay();
              }

              double lt = driver.getLeftTriggerAxis();
              if (lt > Constants.Feeder.MANUAL_TRIGGER_DEADBAND) {
                double right = SmartDashboard.getNumber(
                    "Shooter/LoadRight (debug)",
                    Constants.Feeder.LOAD_RIGHT_SPEED
                );
                double left = SmartDashboard.getNumber(
                    "Shooter/LoadLeft (debug)",
                    Constants.Feeder.LOAD_LEFT_SPEED
                );
                // Both motors same direction for LT-load
                shooter.set(right, left);
              } else if (driver.getBButton()) {
                shooter.set(Constants.Shooter.FULL_SPEED);
              } else {
                shooter.stop();
              }
            },
            shooter
        )
    );
  }

  private void resetRtDelay() {
    rtDelayTimer.stop();
    rtDelayTimer.reset();
    rtWasActive = false;
  }

  private void runRtShooter(double rt) {
    if (!rtWasActive) {
      rtDelayTimer.reset();
      rtDelayTimer.start();
      rtWasActive = true;
    }

    // Invert RT only and scale it up for faster shooting.
    double boostedRt = Math.min(rt * Constants.Shooter.RT_SPEED_MULTIPLIER, 1.0);
    double right = -boostedRt;
    double left = -boostedRt;

    if (!rtDelayTimer.hasElapsed(Constants.Shooter.RT_DELAY_SECONDS)) {
      if (Constants.Shooter.RT_DELAY_SWAP) {
        left = 0.0;
      } else {
        right = 0.0;
      }
    }

    shooter.set(right, left);
  }

  public XboxController getDriver() {
    return driver;
  }
}
