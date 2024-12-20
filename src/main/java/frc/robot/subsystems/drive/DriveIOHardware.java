package frc.robot.subsystems.drive;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import com.ctre.phoenix6.swerve.SwerveDrivetrain;
import com.ctre.phoenix6.swerve.SwerveDrivetrainConstants;
import com.ctre.phoenix6.swerve.SwerveModuleConstants;
import com.ctre.phoenix6.swerve.SwerveRequest;
import edu.wpi.first.wpilibj.Timer;

import edu.wpi.first.math.kinematics.ChassisSpeeds;

public class DriveIOHardware extends SwerveDrivetrain implements DriveIO {

    ArrayBlockingQueue<SwerveDriveState> telemetryQueue = new ArrayBlockingQueue<>(20);
    ArrayBlockingQueue<Double> timestampQueue = new ArrayBlockingQueue<>(20);
    static ReentrantLock telemetryLock = new ReentrantLock();

    Consumer<SwerveDriveState> telemetryConsumer = state -> {
        DriveIOHardware.telemetryLock.lock();
        try {
            double timestamp = Timer.getFPGATimestamp()/1e6;
            timestampQueue.offer(timestamp);
            telemetryQueue.offer(this.getStateCopy());
        } finally {
            DriveIOHardware.telemetryLock.unlock();
        }
    };

    public DriveIOHardware(SwerveDrivetrainConstants drivetrainConstants, SwerveModuleConstants moduleConstants){
        super(drivetrainConstants,moduleConstants);

        registerTelemetry(telemetryConsumer);
    }

    @Override
    public void setControl(SwerveRequest request){
        this.setControl(request);
    }

    @Override
    public void updateInputs(DriveIOInputs inputs){
        inputs.fromSwerveDriveState(this.getStateCopy());
        DriveIOHardware.telemetryLock.lock();
        try {
            inputs.telemetryTimestamps = timestampQueue.stream().mapToDouble((val) -> val).toArray();
            inputs.telemetryStates = telemetryQueue.stream().toArray(SwerveDriveState[]::new);
        } finally {
            DriveIOHardware.telemetryLock.unlock();
        }
    }

}
