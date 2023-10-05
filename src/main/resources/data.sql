-- to save tokens & login details
create table if not exists persistent_logins (
    username varchar(100) not null,
    series varchar(64) primary key,
    token varchar(64) not null,
    last_used timestamp not null
);

-- Insertion of Default Insurance Providers
insert into insurance_provider (insurance_provider_name, discount, insurance_declared_value) values ('ABC', 30, 95),
    ('DEF', 25, 95), ('GHI', 26, 95), ('JKL', 20, 95), ('MNO', 26, 95), ('PQR', 22, 95), ('STV', 20, 95), ('UVW', 25, 95), ('XYZ', 30, 95);


-- User Profile Default Insertions

insert into user_profile (user_id, user_name, role, dob, profession, contact_number, email_id, gender, password, permanent_address, present_address) values
    (10001, 'Admin', 'ROLE_ADMIN', '1999-01-01', 'Maintaining', 9999999999, 'admin@gmail.com', 'male', 'admin', null, null),
    (10002, 'User', 'ROLE_USER', '2001-02-15', 'Browsing', 8888888888, 'user@gmail.com', 'male', 'user', 'NoWhere', 'EveryWhere'),
    (10003, 'Farmer', 'ROLE_USER', '2002-12-11', 'Farming', 7777777777, 'farmer@gmail.com', 'male', 'farmer', 'Bellampalli, Mancherial', 'Bellampalli, Mancherial'),
    (10004, 'ShopKeeper', 'ROLE_USER', '1993-04-30', 'ShopKeeping', 6666666666, 'keeper@gmail.com', 'male', 'keeper', 'Not here', 'Don`t know');


-- Credit Card Default Insertions

insert into credit_card values (
	select user_id from user_profile where user_id = 10003, curdate(), curdate()+100, 50000, 1000000001, 'ICICI');


-- Vehicle Default Insertions

insert into vehicle values (current_date(), null, 5, 500000, 450000, 101, 11111, 2023, 2023, 'Hyderabad', 'Petrol', 'Telangana', 'Harley Davidson', 'Forty Eight', 'Harley Davidson 48');

-- Driver Default Insertions

insert into driver values (12345, select user_id from user_profile where user_id = 10003, 
	select vehicle_id from vehicle where vehicle_id = 101, select user_name from user_profile where user_id = 10003);


-- Quote Default Insertions

insert into quote values (12, curdate(), curdate()-1, 1001, curdate()+365, select vehicle_id from vehicle where vehicle_id=101, 
	select credit_card_number from credit_card where credit_card_number = 1000000001, 
		select insurance_provider_name from insurance_provider where insurance_provider_name = 'ABC', 'New', 'Valid');

		
-- Updating Vehicle quote_id from null to it's assigned quote

update vehicle set quote_id = select quote_id from quote where vehicle_id = 101;