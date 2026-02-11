package frc.robot;

public final class Constants {

  public static final int DRIVER_CONTROLLER_PORT = 0;

  public static final class Drive {
    // Your CAN IDs
    public static final int LEFT_FRONT_ID  = 4; //4
    public static final int LEFT_REAR_ID   = 3; //3
    public static final int RIGHT_FRONT_ID = 1; //1
    public static final int RIGHT_REAR_ID  = 2; //2

    // If it spins when pushing forward, flip this
    public static final boolean INVERT_RIGHT_SIDE = true;

    public static final int CURRENT_LIMIT_AMPS = 50;
    public static final double DEADBAND = 0.05;
    // Scale down turning responsiveness
    public static final double TURN_SCALE = 0.8;
    // Slew-rate limits (units per second)
    public static final double FWD_SLEW_RATE = 3.0;
    public static final double TURN_SLEW_RATE = 3.0;
    // Speed-limit mode scaling
    public static final double SPEED_LIMIT_SCALE = 0.5;
    public static final double SPEED_LIMIT_RATE_SCALE = 0.5;
  }

  public static final class Shooter {
  public static final int LEFT_SHOOTER_ID  = 5;
  public static final int RIGHT_SHOOTER_ID = 6;

  // Set true if the wheels fight each other / spin opposite
  public static final boolean INVERT_RIGHT_SHOOTER = true;

  public static final int CURRENT_LIMIT_AMPS = 40;

  public static final double FULL_SPEED = 1.0;       // RB
  public static final double REVERSE_SPEED = -1.0;  // LB
  public static final double RT_SPEED_MULTIPLIER = 1.35;
  public static final double TRIGGER_DEADBAND = 0.05;
  // Delay to spin up one motor first when RT is pressed
  public static final double RT_DELAY_SECONDS = 0.5;
  // Swap which motor is delayed: false = delay right, true = delay left
  public static final boolean RT_DELAY_SWAP = false;
}

  public static final class Feeder {
    // Shared feeder/shooter motors:
    // CAN 6 = right motor, CAN 5 = left motor.
    public static final int RIGHT_MOTOR_ID = 6;
    public static final int LEFT_MOTOR_ID = 5;

    // Right motor was running opposite; invert to match left
    public static final boolean RIGHT_INVERT = true;
    // Keep default direction; specific actions flip the left motor in code.
    public static final boolean LEFT_INVERT = false;
    public static final int CURRENT_LIMIT_AMPS = 30;

    // Speeds to load balls when LT is held.
    // Right runs forward, left runs reversed and slower.
    public static final double LOAD_RIGHT_SPEED = 0.6;
    public static final double LOAD_LEFT_SPEED = -0.5;
    public static final double MANUAL_TRIGGER_DEADBAND = 0.05;
  }

  public static final class Vision {
    // Default Limelight stream URL (update if your Limelight has a custom hostname)
    public static final String LIMELIGHT_STREAM_URL = "http://limelight.local:5800/stream.mjpg";
  }

  private Constants() {}
}
