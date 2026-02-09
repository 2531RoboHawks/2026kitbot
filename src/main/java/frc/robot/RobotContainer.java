package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.RunCommand;

import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

public class RobotContainer {
  private final DriveSubsystem drive = new DriveSubsystem();
  private final ShooterSubsystem shooter = new ShooterSubsystem();

  private final XboxController driver = new XboxController(Constants.DRIVER_CONTROLLER_PORT);

  public RobotContainer() {
    // Seed debug tunables so they show up on the dashboard
    SmartDashboard.putNumber("Shooter/LoadRight (debug)", Constants.Feeder.LOAD_RIGHT_SPEED);
    SmartDashboard.putNumber("Shooter/LoadLeft (debug)", Constants.Feeder.LOAD_LEFT_SPEED);

    // Drive: LEFT stick does everything
    drive.setDefaultCommand(
        new RunCommand(
            () -> drive.singleStickDrive(-driver.getLeftY(), driver.getLeftX()),
            drive
        )
    );

    // Shared shooter/feeder motors control:
    // Priority: LB reverse > RB full > RT variable > B load > LT variable > stop
    shooter.setDefaultCommand(
        new RunCommand(
            () -> {
              if (driver.getLeftBumper()) {
                shooter.set(Constants.Shooter.REVERSE_SPEED);
              } else if (driver.getRightBumper()) {
                shooter.set(Constants.Shooter.FULL_SPEED);
              } else {
                double rt = driver.getRightTriggerAxis();
                if (rt > Constants.Shooter.TRIGGER_DEADBAND) {
                  // Invert RT only and scale it up for faster shooting.
                  double boostedRt = Math.min(rt * Constants.Shooter.RT_SPEED_MULTIPLIER, 1.0);
                  shooter.set(-boostedRt, -boostedRt); // variable shooter speed
                  return;
                }
              }

              if (driver.getBButton()) {
                double right = SmartDashboard.getNumber(
                    "Shooter/LoadRight (debug)",
                    Constants.Feeder.LOAD_RIGHT_SPEED
                );
                double left = SmartDashboard.getNumber(
                    "Shooter/LoadLeft (debug)",
                    Constants.Feeder.LOAD_LEFT_SPEED
                );
                // Both motors same direction for B-load
                shooter.set(right, left);
              } else {
                double lt = driver.getLeftTriggerAxis();
                if (lt > Constants.Feeder.MANUAL_TRIGGER_DEADBAND) {
                  shooter.set(lt);
                } else {
                  shooter.stop();
                }
              }
            },
            shooter
        )
    );
  }

  public XboxController getDriver() {
    return driver;
  }
}
