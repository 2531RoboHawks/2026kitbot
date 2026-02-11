package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.HttpCamera;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.RunCommand;

import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

public class RobotContainer {
  private final DriveSubsystem drive = new DriveSubsystem();
  private final ShooterSubsystem shooter = new ShooterSubsystem();
  private final HttpCamera limelightCamera =
      new HttpCamera("limelight", Constants.Vision.LIMELIGHT_STREAM_URL);

  private final XboxController driver = new XboxController(Constants.DRIVER_CONTROLLER_PORT);
  private final Timer rtDelayTimer = new Timer();
  private boolean rtWasActive = false;

  private final SlewRateLimiter normalFwdLimiter =
      new SlewRateLimiter(Constants.Drive.FWD_SLEW_RATE);
  private final SlewRateLimiter normalTurnLimiter =
      new SlewRateLimiter(Constants.Drive.TURN_SLEW_RATE);
  private final SlewRateLimiter slowFwdLimiter =
      new SlewRateLimiter(Constants.Drive.FWD_SLEW_RATE * Constants.Drive.SPEED_LIMIT_RATE_SCALE);
  private final SlewRateLimiter slowTurnLimiter =
      new SlewRateLimiter(Constants.Drive.TURN_SLEW_RATE * Constants.Drive.SPEED_LIMIT_RATE_SCALE);
  private boolean speedLimitActive = false;
  private double lastFwd = 0.0;
  private double lastTurn = 0.0;

  public RobotContainer() {
    // Seed debug tunables so they show up on the dashboard
    SmartDashboard.putNumber("Shooter/LoadRight (debug)", Constants.Feeder.LOAD_RIGHT_SPEED);
    SmartDashboard.putNumber("Shooter/LoadLeft (debug)", Constants.Feeder.LOAD_LEFT_SPEED);
    SmartDashboard.putBoolean("Drive/SpeedLimit", false);
    SmartDashboard.putString("Vision/LimelightStream", Constants.Vision.LIMELIGHT_STREAM_URL);

    CameraServer.startAutomaticCapture(limelightCamera);

    // Drive: LEFT stick does everything
    drive.setDefaultCommand(
        new RunCommand(
            () -> {
              boolean limit = SmartDashboard.getBoolean("Drive/SpeedLimit", false);

              if (limit != speedLimitActive) {
                speedLimitActive = limit;
                if (limit) {
                  slowFwdLimiter.reset(lastFwd);
                  slowTurnLimiter.reset(lastTurn);
                } else {
                  normalFwdLimiter.reset(lastFwd);
                  normalTurnLimiter.reset(lastTurn);
                }
              }

              double scale = limit ? Constants.Drive.SPEED_LIMIT_SCALE : 1.0;
              double fwd = -driver.getLeftY() * scale;
              double turn = -driver.getRightX() * Constants.Drive.TURN_SCALE * scale;

              double outFwd = (limit ? slowFwdLimiter : normalFwdLimiter).calculate(fwd);
              double outTurn = (limit ? slowTurnLimiter : normalTurnLimiter).calculate(turn);

              lastFwd = outFwd;
              lastTurn = outTurn;

              drive.singleStickDrive(outFwd, outTurn);
            },
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
