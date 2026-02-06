package frc.robot.subsystems;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructPublisher;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.Constants;
import frc.robot.DebugTuning;

public class LimelightSubsystem extends SubsystemBase {
  private Pose3d cameraPose;
  private Transform3d robotToCamera;
  private final StructPublisher<Pose3d> mountPosePub;

  public LimelightSubsystem() {
    DebugTuning.seedNumber("Limelight/Mount/X_m", Constants.Limelight.MOUNT_X_M);
    DebugTuning.seedNumber("Limelight/Mount/Y_m", Constants.Limelight.MOUNT_Y_M);
    DebugTuning.seedNumber("Limelight/Mount/Z_m", Constants.Limelight.MOUNT_Z_M);
    DebugTuning.seedNumber("Limelight/Mount/Roll_deg", Constants.Limelight.MOUNT_ROLL_DEG);
    DebugTuning.seedNumber("Limelight/Mount/Pitch_deg", Constants.Limelight.MOUNT_PITCH_DEG);
    DebugTuning.seedNumber("Limelight/Mount/Yaw_deg", Constants.Limelight.MOUNT_YAW_DEG);

    updatePoseFromDashboard();

    mountPosePub =
        NetworkTableInstance.getDefault()
            .getStructTopic("Limelight/MountPose", Pose3d.struct)
            .publish();
  }

  @Override
  public void periodic() {
    updatePoseFromDashboard();
    mountPosePub.set(cameraPose);
  }

  private void updatePoseFromDashboard() {
    double x = DebugTuning.getNumber("Limelight/Mount/X_m", Constants.Limelight.MOUNT_X_M);
    double y = DebugTuning.getNumber("Limelight/Mount/Y_m", Constants.Limelight.MOUNT_Y_M);
    double z = DebugTuning.getNumber("Limelight/Mount/Z_m", Constants.Limelight.MOUNT_Z_M);
    double rollDeg =
        DebugTuning.getNumber("Limelight/Mount/Roll_deg", Constants.Limelight.MOUNT_ROLL_DEG);
    double pitchDeg =
        DebugTuning.getNumber("Limelight/Mount/Pitch_deg", Constants.Limelight.MOUNT_PITCH_DEG);
    double yawDeg =
        DebugTuning.getNumber("Limelight/Mount/Yaw_deg", Constants.Limelight.MOUNT_YAW_DEG);

    Translation3d translation = new Translation3d(x, y, z);
    Rotation3d rotation =
        new Rotation3d(
            Units.degreesToRadians(rollDeg),
            Units.degreesToRadians(pitchDeg),
            Units.degreesToRadians(yawDeg));

    cameraPose = new Pose3d(translation, rotation);
    robotToCamera = new Transform3d(translation, rotation);
  }

  public Pose3d getCameraPose() {
    return cameraPose;
  }

  public Transform3d getRobotToCamera() {
    return robotToCamera;
  }
}
