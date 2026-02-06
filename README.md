kitbot code for 2026

now having hopper auto fill

controls: 

Left stick: movement

RT: fire/shoot 

LT: unjam hopper/shoot

B: Load in to hopper 

**Limelight Mount**
Set the camera mount in `Constants.Limelight` using meters for X/Y/Z and degrees for roll/pitch/yaw.
Robot frame is +X forward, +Y left, +Z up with origin at the robot center on the floor.
For debug tuning, set `Constants.DEBUG = true` and edit SmartDashboard keys:
`Limelight/Mount/X_m (debug)`, `Limelight/Mount/Y_m (debug)`, `Limelight/Mount/Z_m (debug)`,
`Limelight/Mount/Roll_deg (debug)`, `Limelight/Mount/Pitch_deg (debug)`,
`Limelight/Mount/Yaw_deg (debug)`.

**Global Debug Save**
Any dashboard key that ends with ` (debug)` is included in the global save file.
Toggle `Tuning/Save (debug)` to write `/home/lvuser/deploy/tuning.json` and print values
to the console for easy copy/paste. When `Constants.DEBUG = true`, the file is loaded
on boot and used to seed `(debug)` keys.

