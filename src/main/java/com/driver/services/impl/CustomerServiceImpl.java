package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.CabRepository;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CabRepository cabRepository;
	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
     customerRepository2.deleteById(customerId);
	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available,
		// throw "No cab available!" exception
		//Avoid using SQL query

	Customer customer = customerRepository2.findById(customerId).get();
		//List<TripBooking> tripBookingList = customerRepository2.findById(customerId).get().getTripBookingList();
	Driver driver = new Driver();
	//	 List<TripBooking> tripBookingList1 = driver.getTripBookingList();
		List<Driver> driverList = driverRepository2.findAll();
 List<TripBooking> tripBookingList = new ArrayList<>();
 TripBooking tripBooking = new TripBooking();
		for(Driver driver1:driverList) {
			if (driver1.getCab().getAvailable() == true) {
				if (driver == null || driver.getDriverId() > driver1.getDriverId()) {
					driver = driver1;
				}
			}

		}
		if(driver==null) {

			throw new Exception("cab is not available");
		}

		driver.getCab().setAvailable(false);

tripBooking.setCustomer(customer);
tripBooking.setToLocation(toLocation);
tripBooking.setFromLocation(fromLocation);
tripBooking.setDriver(driver);
tripBooking.setStatus(TripStatus.CONFIRMED);
		int rate = driver.getCab().getPerKmRate();
		tripBooking.setBill(distanceInKm * rate);
customer.getTripBookingList().add(tripBooking);



cabRepository.save(driver.getCab());
customerRepository2.save(customer);


return tripBooking;

		 }





	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
tripBooking.setStatus(TripStatus.CANCELED);


Driver driver = tripBooking.getDriver();
Cab cab = driver.getCab();
tripBooking.setBill(0);
cab.setAvailable(true);

tripBookingRepository2.save(tripBooking);
	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
		tripBooking.setStatus(TripStatus.COMPLETED);


		int  km = tripBooking.getDistanceInKm();
		 Driver driver = tripBooking.getDriver();
		 Cab cab = driver.getCab();

		 int rate = cab.getPerKmRate();

		 tripBooking.setBill(rate*km);
		 cab.setAvailable(true);
		 tripBookingRepository2.save(tripBooking);








	}
}