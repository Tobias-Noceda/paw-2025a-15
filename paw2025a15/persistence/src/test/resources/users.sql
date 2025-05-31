INSERT INTO users(user_id, user_email, user_password, user_name, picture_id, user_role, user_telephone, locale, create_date) 
    VALUES(1, 'donna@example.com', 'supersecret123', 'donna', 1, 0, '1144445555', 0, '2025-04-09');
INSERT INTO patient_details(patient_id, patient_birthdate, patient_height, patient_weight) VALUES(1, '2000-01-01', 1.75, 89.00);
INSERT INTO users(user_id, user_email, user_password, user_name, picture_id, user_role, user_telephone, locale, create_date) 
    VALUES(2, 'membrillo@example.com', 'supersecret123', 'membrillo', 1, 1, '1144445555', 0, '2025-04-09');
INSERT INTO doctor_details(doctor_id, doctor_licence, doctor_specialty) VALUES(2, 'med-licence', 2);