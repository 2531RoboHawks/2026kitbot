package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.RunCommand;

import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

public class RobotContainer {
  private final DriveSubsystem drive = new DriveSubsystem();
  private final ShooterSubsystem shooter = new ShooterSubsystem();

  private final XboxController driver = new XboxController(Constants.DRIVER_CONTROLLER_PORT);

  public RobotContainer() {
    // Drive: LEFT stick does everything
    drive.setDefaultCommand(
        new RunCommand(
            () -> drive.singleStickDrive(-driver.getLeftY(), driver.getLeftX()),
            drive
        )
    );

    // Shooter controls (priority: LB reverse > RB full > RT variable > stop)
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
                  shooter.set(rt); // variable speed
                } else {
                  shooter.stop();
                }
              }
            },
            shooter
        )
    );
  }
}
