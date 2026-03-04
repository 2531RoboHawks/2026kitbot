# 2026 Kitbot

Kitbot code for 2026, featuring hopper auto fill and CANdle LED control.

## Controls (Xbox Controller — Driver)

### Drive
| Input | Action |
|---|---|
| Left Stick Y | Forward / Backward |
| Right Stick X | Turn |
| SmartDashboard: `Drive/SpeedLimit` | Toggle slow/precision mode |

### Shooter & Feeder
Controls are prioritized in this order (highest priority first):

| Input | Action |
|---|---|
| **LB** (Left Bumper) | Reverse shooter |
| **RB** (Right Bumper) | Full speed shoot |
| **RT** (Right Trigger) | Variable speed shoot (speed scales with trigger, one motor delayed briefly at start) |
| **LT** (Left Trigger) | Load into hopper/feeder |
| **B** | Manual full speed shoot |
| *(no input)* | Stop shooter |

### Notes
- RT shoot has a brief delay before the second motor spins up (controlled by `Constants.Shooter.RT_DELAY_SECONDS`)
- LT load speeds are tunable live via SmartDashboard: `Shooter/LoadRight (debug)` and `Shooter/LoadLeft (debug)`
- Speed limit mode reduces drive speed and slew rate — toggle via SmartDashboard
