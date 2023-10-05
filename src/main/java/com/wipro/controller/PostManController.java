package com.wipro.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wipro.entity.CreditCard;
import com.wipro.entity.Driver;
import com.wipro.entity.InsuranceProvider;
import com.wipro.entity.Quote;
import com.wipro.entity.UserProfile;
import com.wipro.entity.Vehicle;
import com.wipro.repository.CreditCardRepository;
import com.wipro.repository.DriverRepository;
import com.wipro.repository.InsuranceProviderRepository;
import com.wipro.repository.QuoteRepository;
import com.wipro.repository.UserProfileRepository;
import com.wipro.repository.VehicleRepository;

@RestController
@RequestMapping("public/postman")
public class PostManController {
	
	@Autowired
	private UserProfileRepository userRepo;
	
	@Autowired
	private InsuranceProviderRepository insuranceRepo;
	
	@Autowired
	private DriverRepository driverRepo;
	
	@Autowired
	private VehicleRepository vehicleRepo;
	
	@Autowired
	private QuoteRepository quoteRepo;
	
	@Autowired
	private CreditCardRepository creditRepo;
	

	@GetMapping("helloworld")
	public String helloWorld() {
		return "Hello World!";
	}
	
	
	
	/********************************** UserProfile Management **********************************/
	
	@GetMapping("userProfiles")
	public List<UserProfile> getAllUserProfiles(){
		return userRepo.findAll();
	}
	
	@GetMapping("usersResponse")
	public ResponseEntity<List<UserProfile>> getAllUsersWithResponse(){
		return new ResponseEntity<List<UserProfile>>(userRepo.findAll(), HttpStatus.FOUND);
	}
	
	@GetMapping("admin/userProfiles/{userId}")
	public UserProfile getUserById(@PathVariable Integer userId){
		return userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User is Not Found with the User Id: " + userId));
	}
	
	@PostMapping({"registration", "admin/addUser"})
	public ResponseEntity<UserProfile> addUserProfile(@RequestBody UserProfile user) {
		user.setRole("ROLE_USER");
		return new ResponseEntity<UserProfile>(userRepo.save(user), HttpStatus.CREATED);
	}
	
	@DeleteMapping("admin/userProfiles/{userId}")
	public ResponseEntity<Object> deleteUserByAdmin(@PathVariable Integer userId) {
		Optional<UserProfile> user = userRepo.findById(userId);
		if(user.isEmpty()) return new ResponseEntity<>("User is not Found", HttpStatus.NOT_FOUND);
		if(user.get().getRole().equals("ROLE_ADMIN")) return new ResponseEntity<>("Can't Delete The Admin", HttpStatus.BAD_REQUEST);
		
		Predicate<? super Driver> predicate = driver -> driver.getUserId().equals(user.get());
		Optional<Driver> driver = driverRepo.findAll().stream().filter(predicate).findFirst();		//Can User JPA implementation - findByDriverName from the DriverRepository
		if(driver.isPresent()) {
			Integer licenseNumber = driver.get().getLicenseNumber();
			driverRepo.deleteById(licenseNumber);
		}
		
		List<CreditCard> creditCards = creditRepo.findAllByUserId(user.get());
		creditCards.forEach(cred -> {
			List<Quote> quotes = quoteRepo.findAllByCreditCard(cred);
			quotes.forEach(q -> q.setCreditCard(null));
//			Iterable<Quote> q = quotes; 
			quoteRepo.saveAll(quotes);

			/*
			quotes.forEach(q -> {
				q.setCreditCard(null);
				quoteRepo.save(q);
				});
			 */
		});
		
		creditRepo.deleteAllByUserId(user.get());
		userRepo.deleteById(userId);
		return new ResponseEntity<>("User is Deleted", HttpStatus.GONE);
	}
	
	@PostMapping("userProfiles/forgotPassword")
	public ResponseEntity<Object> forgotPassword(@RequestBody UserProfile user){
		UserProfile userFetched = userRepo.findByEmailIdAndUserName(user.getEmailId(), user.getUserName()).orElseThrow(() -> new RuntimeException("User Doesn't Exist"));
		if(!userFetched.getDOB().equals(user.getDOB()) || !userFetched.getContactNumber().equals(user.getContactNumber())) 
			return new ResponseEntity<Object>("Re-enter correct user details to reset the password", HttpStatus.BAD_REQUEST);
		userFetched.setPassword(user.getPassword());
		userRepo.save(userFetched);
		return new ResponseEntity<Object>("Password Reset Succesful", HttpStatus.ACCEPTED);
	}
	
	
	/********************************** Quote Management **********************************/
	
	@GetMapping("quotes/{quoteId}")
	public Quote findQuoteById(@PathVariable Integer quoteId) {
		return quoteRepo.findById(quoteId).orElseThrow(() -> new RuntimeException("Quote Not Found"));
	}
	
	@GetMapping("quotes")
	public List<Quote> getAllQuotes(){
		return quoteRepo.findAll();
	}
	
	@DeleteMapping("quotes/{quoteId}")
	public void deleteByQuoteId(@PathVariable Integer quoteId) {
		Vehicle vehicle = findQuoteById(quoteId).getVehicleId();
		vehicle.setQuote(null);
		vehicleRepo.save(vehicle);
		quoteRepo.deleteById(quoteId);		
	}
	
	@Autowired
	private JdbcTemplate jdbc;
	
	@PostMapping("quotes/{vehicleId}/{insuranceProviderName}/{creditCardNumber}")
	public ResponseEntity<Object> addQuote(@RequestBody Quote quote, @PathVariable Integer vehicleId, @PathVariable String insuranceProviderName, @PathVariable Long creditCardNumber) throws SQLException {
		quote.setInsuranceProvider(getInsuranceProviderById(insuranceProviderName));
		CreditCard card = getCreditCardByNumber(creditCardNumber);
		quote.setCreditCard(card);
		
		List<Driver> users = getAllByVehicleId(vehicleId); 
		users.removeIf(driver -> !driver.getUserId().equals(card.getUserId()));
		if(users.size() == 0) return new ResponseEntity<Object>("Enter a Correct Credit Card Number That Belongs to the User: "+card.getUserId().getUserName(), HttpStatus.BAD_REQUEST);
		
		Vehicle vehicle = getVehicleById(vehicleId);
		if(vehicle.getQuote() != null) return new ResponseEntity<>("Quote is Already Exists for this Vehicle: "+vehicle.getVehicleName()+", "+vehicleId, HttpStatus.BAD_REQUEST);
		Quote savedQuote = quoteRepo.save(quote);
		
		Connection conn = jdbc.getDataSource().getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement("update quote set vehicle_id = ? where quote_id = ?");
			ps.setInt(1, vehicleId);
			ps.setInt(2, savedQuote.getQuoteId());
			ps.execute();
		}catch(SQLException e) {
			conn.prepareStatement("delete from quote where quote_id = "+savedQuote.getQuoteId()).execute();
			return new ResponseEntity<Object>("Error While Updating the vehicleId: "+vehicleId+" for purchased Quote, quoteId: "+savedQuote.getQuoteId()+"\n Re-Check the Vehicle Id & CreditCardNumber", HttpStatus.BAD_REQUEST);
		}
		Quote newQuote = findQuoteById(savedQuote.getQuoteId());
		vehicle.setQuote(newQuote);
		saveVehicle(vehicle);
		return new ResponseEntity<Object>(newQuote, HttpStatus.CREATED);
	}
	
	
	/********************************** Driver Management **********************************/
	
	@GetMapping("drivers")
	public List<Driver> getAllDrivers(){
		return driverRepo.findAll();
	}
	
	@GetMapping("drivers/byVehicleId/{vehicleId}")
	public List<Driver> getAllByVehicleId(@PathVariable Integer vehicleId){
		return driverRepo.findAllByVehicleId(getVehicleById(vehicleId));
	}
	
	@GetMapping("drivers/{licenseNumber}")
	public Driver getDriverById(@PathVariable Integer licenseNumber){
		return driverRepo.findById(licenseNumber).orElseThrow(() -> new RuntimeException("Driver Doesn't Exists for License Number: "+licenseNumber));
	}
	
	@DeleteMapping("drivers/{licenseNumber}")
	public ResponseEntity<Object> deleteDriverById(@PathVariable Integer licenseNumber){
		driverRepo.deleteById(licenseNumber);
		return new ResponseEntity<Object>("Driver Deleted for License Number: "+licenseNumber, HttpStatus.GONE);
	}
	
	@PostMapping("drivers/setVehicle/{licenseNumber}")
	public ResponseEntity<Object> setVehicle(@PathVariable Integer licenseNumber, @RequestBody Map<String, Object> map){
		Driver driver = getDriverById(licenseNumber);
		Vehicle vehicle = getVehicleById((Integer) map.get("vehicleId"));
		driver.setVehicleId(vehicle);
		return new ResponseEntity<Object>(driverRepo.save(driver), HttpStatus.ACCEPTED);
	}

	@PostMapping("drivers")
	public ResponseEntity<Object> saveDriver(@RequestBody Map<String, Object> map) {
		Driver driver = new Driver();
		driver.setLicenseNumber((int)map.get("licenseNumber"));
		UserProfile user = getUserById((int)map.get("userId"));
		driver.setUserId(user);
		
		String driverName = (String)map.get("driverName");
		if(!driverName.equals(user.getUserName())) return new ResponseEntity<Object>("User Id: "+user.getUserId()+" doesn't matches with User Name: "+driverName, HttpStatus.BAD_REQUEST);
		driver.setDriverName(user);
		
		Integer vehicleId = (Integer) map.get("vehicleId");
		if(vehicleId != null) {
			Vehicle vehicle = getVehicleById(vehicleId);
			driver.setVehicleId(vehicle);
		}
		return new ResponseEntity<Object>(driverRepo.save(driver), HttpStatus.CREATED);
	}
	
	@PostMapping("drivers/{licenseNumber}/setVehicle/{vehicleId}")
	public ResponseEntity<Object> setVehicle(@PathVariable Integer licenseNumber, @PathVariable Integer vehicleId){
		Vehicle vehicle = getVehicleById(vehicleId);
		Driver driver = getDriverById(licenseNumber);
		driver.setVehicleId(vehicle);
		return new ResponseEntity<Object>(driverRepo.save(driver), HttpStatus.ACCEPTED);
	}
	
	//Usually we don't change userId for a driver license number(User for a Driver), but we can
	@PostMapping("drivers/setUser/{licenseNumber}")
	public ResponseEntity<Object> setUser(@PathVariable Integer licenseNumber, @RequestBody Map<String, Object> map) {
		Driver driver = getDriverById(licenseNumber);
		UserProfile user = getUserById((Integer) map.get("userId"));
		
		driver.setUserId(user);
		driver.setDriverName(user);
		return new ResponseEntity<Object>(driverRepo.save(driver), HttpStatus.ACCEPTED);
	}
	
	/********************************** Vehicle Management **********************************/
	
	@GetMapping("vehicles")
	public List<Vehicle> getAllVehicles(){
		return vehicleRepo.findAll();
	}
	
	@GetMapping("vehicles/{vehicleId}")
	public Vehicle getVehicleById(@PathVariable Integer vehicleId) {
		return vehicleRepo.findById(vehicleId).orElseThrow(() -> new RuntimeException("Vehicle is Not Found for the Vehicle Id: "+vehicleId));
	}
	
	@PostMapping("vehicles")
	public Vehicle saveVehicle(@RequestBody Vehicle vehicle) {
		return vehicleRepo.save(vehicle);
	}
	
	@DeleteMapping("vehicles/{vehicleId}")
	public void deleteVehicleById(@PathVariable Integer vehicleId) {
		Vehicle vehicle = getVehicleById(vehicleId);
		Optional<Driver> driver = driverRepo.findByVehicleId(vehicle);
		if(driver.isPresent()) {
			driver.get().setVehicleId(null);
			driverRepo.save(driver.get());
		}
		vehicleRepo.deleteById(vehicleId);
	}
	
	
	/********************************** InsuranceProvider Management **********************************/
	
	@GetMapping("insuranceProviders/{insuranceProviderName}")
	public InsuranceProvider getInsuranceProviderById(@PathVariable String insuranceProviderName) {
		return insuranceRepo.findById(insuranceProviderName).orElseThrow(() -> new RuntimeException(insuranceProviderName + " insurance provider is not found"));
	}
	
	@GetMapping("insuranceProviders")
	public List<InsuranceProvider> getAllInsuranceProviders(){
		return insuranceRepo.findAll();
	}
	
	/********************************** CreditCard Management **********************************/
	
	@GetMapping("creditCards/{creditCardNumber}")
	public CreditCard getCreditCardByNumber(@PathVariable Long creditCardNumber) {
		return creditRepo.findById(creditCardNumber).orElseThrow(() -> new RuntimeException("Credit Card is Not Found with the number: "+creditCardNumber));
	}
	
	@GetMapping("{userId}/creditCards")
	public List<CreditCard> getCreditCardsOfUser(@PathVariable Integer userId){
		UserProfile user = getUserById(userId);
		Predicate<? super CreditCard> predicate = card -> card.getUserId().equals(user);
		return creditRepo.findAll().stream().filter(predicate ).toList();
	}
	
	@GetMapping("admin/creditCards")
	public List<CreditCard> getAllCreditCards(){
		return creditRepo.findAll();
	}
	
	@PostMapping("{userId}/creditCards")
	public ResponseEntity<Object> addCreditCard(@RequestBody CreditCard card, @PathVariable Integer userId) {
		if(card.getCreditCardNumber()<1000000000) return new ResponseEntity<>("Incorrect Credit Card Number: "+card.getCreditCardNumber(), HttpStatus.NOT_FOUND);
		if(creditRepo.findById(card.getCreditCardNumber()).isPresent()) return new ResponseEntity<Object>("Please select another Credit Card, Credit card: "+card.getCreditCardNumber()+" is already exists", HttpStatus.BAD_REQUEST);
		UserProfile user = getUserById(userId);
		card.setUserId(user);
		return new ResponseEntity<Object>(creditRepo.save(card), HttpStatus.CREATED);
	}
	
	@DeleteMapping("creditCards/{cardNumber}")
	public void deleteCreditCard(@PathVariable Long cardNumber) {
		CreditCard card = getCreditCardByNumber(cardNumber);
		Optional<Quote> quote = quoteRepo.findByCreditCard(card);
		if(quote.isPresent()) {
			Quote q = quote.get();
			q.setCreditCard(null);
			quoteRepo.save(q);
		}
		creditRepo.deleteById(cardNumber);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
