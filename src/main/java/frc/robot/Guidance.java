package frc.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.networktables.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.VisionThread;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

public class Guidance {
	NetworkTableEntry distanceEntry, targetEntry, currentEntry;
	private static final double diameter = 9.25, radius = diameter / 2, circumference = diameter * Math.PI;
	private static double distance = 0, target = 0, current = 0, targetOffset, realCurrent, targetRatio, maxPower,
			powerRatio, error, realPower;

	private static final int IMG_WIDTH = 160;
	private static final int IMG_HEIGHT = 120;

	private static VisionThread visionThread;
	private UsbCamera camera;
	private FRC5572Controller driverController;
	private double centerX = 0.0;
	private double centerY = 0.0;
	private double targetArea = 0.0;

	private final Object imgLock = new Object();

	public void init() {
		// Initializing the network table itself as well as its entries
		NetworkTableInstance inst = NetworkTableInstance.getDefault();
		NetworkTable table = inst.getTable("datatable");
		distanceEntry = table.getEntry("Distance");
		targetEntry = table.getEntry("Target Position");
		currentEntry = table.getEntry("Current Position");

		driverController = new FRC5572Controller(0);

		camera = CameraServer.getInstance().startAutomaticCapture();
		camera.setResolution(IMG_WIDTH, IMG_HEIGHT);
		camera.setWhiteBalanceManual(25);
		camera.setFPS(15);
		camera.setBrightness(0);
		camera.setExposureManual(0);

		visionThread = new VisionThread(camera, new GripPipeline(), pipeline -> {
			if (!pipeline.filterContoursOutput().isEmpty()) {
				Rect r = Imgproc.boundingRect(pipeline.filterContoursOutput().get(0));
				synchronized (imgLock) {
					centerX = 2 * r.x + r.width - (IMG_WIDTH / 2);
					centerY = 2 * r.y + r.height - (IMG_HEIGHT / 2);
					targetArea = r.area();
				}
			}
		});
		visionThread.start();
	}

	public double periodic() {
		distanceEntry.setDouble(distance);
		targetEntry.setDouble(target);
		currentEntry.setDouble(current);

		targetOffset = distance;
		maxPower = 1;
		realCurrent = 1;

		// TODO change to run this code on button press
		if (target == target) {
			camera.setExposureManual(0);
			camera.setBrightness(0);

			synchronized (imgLock) {
				SmartDashboard.putNumber("Center X", centerX);
				SmartDashboard.putNumber("Center Y", centerY);
				SmartDashboard.putNumber("Area", targetArea);

				if (Math.abs(centerX) > 3) {
					if (centerX > 0) {
						// Put drive code here (left)
					} else {
						// Put drive code here (right)
					}
				}
			}

		} else {
			camera.setExposureManual(25);
			camera.setBrightness(25);
		}

		// Calculations for determining power output to motors
		targetRatio = current / target;
		powerRatio = targetRatio * maxPower;
		error = (current - realCurrent) / target;
		realPower = (targetRatio - error) * maxPower;

		distance += 1;
		current += 1;
		target += 1;

		return realPower;
	}
}