package com.smartparking;

import com.smartparking.model.Bike;
import com.smartparking.model.Car;
import com.smartparking.model.ParkingRecord;
import com.smartparking.model.Truck;
import com.smartparking.model.Vehicle;
import com.smartparking.model.VehicleFactory;
import com.smartparking.model.VehicleType;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests verifying vehicle tariff calculations, ceiling rounding, and minimum charge logic.
 */
public class FeeCalculationTest {

    @Test
    public void testBikeFeeCalculation() {
        Vehicle bike = new Bike("KA-01-AB-1234", "John Doe", "9876543210");
        assertEquals(40.0, bike.getHourlyRate(), 0.001);

        // Minimum 1 hour charge
        assertEquals(40.0, bike.calculateFee(0), 0.001);
        assertEquals(40.0, bike.calculateFee(1), 0.001);

        // Multi-hour charge
        assertEquals(80.0, bike.calculateFee(2), 0.001);
        assertEquals(120.0, bike.calculateFee(3), 0.001);
    }

    @Test
    public void testCarFeeCalculation() {
        Vehicle car = new Car("DL-03-XY-9876", "Alice Smith", "9812345678");
        assertEquals(100.0, car.getHourlyRate(), 0.001);

        // Minimum 1 hour charge
        assertEquals(100.0, car.calculateFee(0), 0.001);
        assertEquals(100.0, car.calculateFee(1), 0.001);

        // 3 hours charge
        assertEquals(300.0, car.calculateFee(3), 0.001);
        assertEquals(500.0, car.calculateFee(5), 0.001);
    }

    @Test
    public void testTruckFeeCalculation() {
        Vehicle truck = new Truck("HR-26-EE-4321", "Bob Johnson", "9834567890");
        assertEquals(150.0, truck.getHourlyRate(), 0.001);

        // Minimum 1 hour charge
        assertEquals(150.0, truck.calculateFee(0), 0.001);
        assertEquals(150.0, truck.calculateFee(1), 0.001);

        // 4 hours charge
        assertEquals(600.0, truck.calculateFee(4), 0.001);
    }

    @Test
    public void testPolymorphicVehicleFactory() {
        Vehicle v1 = VehicleFactory.createVehicle(VehicleType.BIKE, "MH-01-A-1111", "Ramesh", "9999999999");
        Vehicle v2 = VehicleFactory.createVehicle(VehicleType.CAR, "MH-01-B-2222", "Suresh", "9999999998");
        Vehicle v3 = VehicleFactory.createVehicle(VehicleType.TRUCK, "MH-01-C-3333", "Ganesh", "9999999997");

        assertEquals(VehicleType.BIKE, v1.getVehicleType());
        assertEquals(VehicleType.CAR, v2.getVehicleType());
        assertEquals(VehicleType.TRUCK, v3.getVehicleType());

        assertEquals(80.0, v1.calculateFee(2), 0.001);
        assertEquals(200.0, v2.calculateFee(2), 0.001);
        assertEquals(300.0, v3.calculateFee(2), 0.001);
    }

    @Test
    public void testBillableHoursCeilingLogic() {
        LocalDateTime entryTime = LocalDateTime.of(2026, 11, 10, 10, 0);

        ParkingRecord record = new ParkingRecord();
        record.setEntryTime(entryTime);

        // 10 minutes parked -> 1 hour charge
        LocalDateTime exit10Min = LocalDateTime.of(2026, 11, 10, 10, 10);
        assertEquals(1, record.calculateBillableHours(exit10Min));

        // Exactly 60 minutes parked -> 1 hour charge
        LocalDateTime exit60Min = LocalDateTime.of(2026, 11, 10, 11, 0);
        assertEquals(1, record.calculateBillableHours(exit60Min));

        // 61 minutes parked (1 hr 1 min) -> 2 hours charge (started hour ceiling)
        LocalDateTime exit61Min = LocalDateTime.of(2026, 11, 10, 11, 1);
        assertEquals(2, record.calculateBillableHours(exit61Min));

        // 2 hours 45 mins -> 3 hours charge
        LocalDateTime exit165Min = LocalDateTime.of(2026, 11, 10, 12, 45);
        assertEquals(3, record.calculateBillableHours(exit165Min));
    }
}
