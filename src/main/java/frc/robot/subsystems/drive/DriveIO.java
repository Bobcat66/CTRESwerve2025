package frc.robot.subsystems.drive;

import org.littletonrobotics.junction.AutoLog;

import com.ctre.phoenix6.swerve.SwerveDrivetrain;
import com.ctre.phoenix6.swerve.SwerveRequest;
import com.ctre.phoenix6.swerve.SwerveDrivetrain.SwerveDriveState;

import edu.wpi.first.math.geometry.Pose2d;

public interface DriveIO {

    @AutoLog
    class DriveIOInputs extends SwerveDrivetrain.SwerveDriveState {

        double[] telemetryTimestamps = new double[] {};
        SwerveDriveState[] telemetryStates = new SwerveDriveState[] {};

        DriveIOInputs() {
            this.Pose = new Pose2d();
        }

        public void fromSwerveDriveState(SwerveDrivetrain.SwerveDriveState stateIn) {
            this.Pose = stateIn.Pose;
            this.SuccessfulDaqs = stateIn.SuccessfulDaqs;
            this.FailedDaqs = stateIn.FailedDaqs;
            this.ModuleStates = stateIn.ModuleStates;
            this.ModuleTargets = stateIn.ModuleTargets;
            this.Speeds = stateIn.Speeds;
            this.OdometryPeriod = stateIn.OdometryPeriod;
        }
    }

    public default void updateInputs(DriveIOInputs inputs) {}

    public default void setControl(SwerveRequest request) {}
}
