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
  }

  public static final class Shooter {
  public static final int LEFT_SHOOTER_ID  = 5;
  public static final int RIGHT_SHOOTER_ID = 6;

  // Set true if the wheels fight each other / spin opposite
  public static final boolean INVERT_RIGHT_SHOOTER = true;

  public static final int CURRENT_LIMIT_AMPS = 40;

  public static final double FULL_SPEED = 1.0;       // RB
  public static final double REVERSE_SPEED = -1.0;  // LB
  public static final double TRIGGER_DEADBAND = 0.05;
}

  public static final class Hopper {
    // FRC 2026 kitbot hopper:
    // CAN 6 = upper/right hopper motor, CAN 5 = lower/floor intake motor.
    public static final int UPPER_MOTOR_ID = 6;
    public static final int LOWER_MOTOR_ID = 5;

    public static final boolean UPPER_INVERT = false;
    public static final boolean LOWER_INVERT = false;
    public static final int CURRENT_LIMIT_AMPS = 30;

    // Speed to load balls into the hopper when B is held.
    public static final double LOAD_SPEED = 0.6;
    public static final double MANUAL_TRIGGER_DEADBAND = 0.05;
  }

  private Constants() {}
}
