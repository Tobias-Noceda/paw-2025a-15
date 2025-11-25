CREATE TABLE IF NOT EXISTS files(
    file_id SERIAL PRIMARY KEY,
    file_content BYTEA NOT NULL,
    file_type VARCHAR(20) NOT NULL
);





--CHANGES FOR FILES TABLE
--ALTER TABLE files
--ALTER COLUMN file_type TYPE INT
--USING (
--CASE
--    WHEN file_type = 'image/png' THEN 0
--    WHEN file_type = 'image/jpeg' THEN 1
--    WHEN file_type = 'application/pdf' THEN 2
--END
--);

CREATE TABLE IF NOT EXISTS insurances(
    insurance_id SERIAL PRIMARY KEY,
    insurance_name VARCHAR(100) NOT NULL UNIQUE,
    picture_id BIGINT NOT NULL DEFAULT 1,

    FOREIGN KEY (picture_id) REFERENCES files(file_id)
);

CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    user_email VARCHAR(100) NOT NULL UNIQUE,
    user_password VARCHAR(100) NOT NULL,
    user_name VARCHAR (100) NOT NULL,
    picture_id BIGINT NOT NULL DEFAULT 1,

    FOREIGN KEY (picture_id) REFERENCES files(file_id)
);

-- Add roles
-- -- Step 1: Add the column with a default value
-- ALTER TABLE users
-- ADD COLUMN user_role INT;

-- -- Step 2: Update the values based on the condition
-- UPDATE users
-- SET user_role = CASE
--     WHEN user_id IN (SELECT doctor_id FROM doctor_details) THEN 1
--     ELSE 0
-- END;

-- -- Step 3: Enable the NULL constraint on the new column
-- ALTER TABLE users
-- ALTER COLUMN user_role SET NOT NULL;

-- Add telephone
-- ALTER TABLE users
-- ADD COLUMN user_telephone VARCHAR(20) NOT NULL DEFAULT '1112345678';

-- ALTER TABLE users
-- ADD COLUMN locale INT NOT NULL DEFAULT 0;

-- ALTER TABLE users
-- ADD COLUMN create_date DATE;
-- -- la logica de esto es que esten dispersos similar
-- UPDATE users
-- SET create_date = DATEADD(DAY, (userid * @totalDays / @maxId), @startDate);
-- ALTER TABLE users
-- ALTER COLUMN create_date SET NOT NULL;
--
-- UPDATE users
-- SET create_date = DATE '2025-04-09';
-- --
-- ALTER TABLE users
-- ALTER COLUMN create_date SET NOT NULL;

CREATE TABLE IF NOT EXISTS studies (
    study_id SERIAL PRIMARY KEY,
    study_type VARCHAR(100) NOT NULL,
    file_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    uploader_id BIGINT NOT NULL,
    upload_date TIMESTAMP NOT NULL,

    FOREIGN KEY (file_id) REFERENCES files(file_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (uploader_id) REFERENCES users(user_id)
);

--CHANGES FOR STUDIES TABLE
--ALTER TABLE studies
--RENAME COLUMN study_type TO study_comment;
--ALTER TABLE studies
--ALTER COLUMN study_comment DROP NOT NULL;
--ALTER TABLE studies
--ADD COLUMN study_type INT NOT NULL DEFAULT 0;
--UPDATE studies
--SET study_type = 0 WHERE study_type IS NULL;
--ALTER TABLE studies
--ADD COLUMN study_date DATE;
--UPDATE studies
--SET study_date = upload_date::DATE;
--ALTER TABLE studies
--ALTER COLUMN study_date SET NOT NULL;

CREATE TABLE IF NOT EXISTS patient_coverages (
    patient_id BIGINT PRIMARY KEY,
    insurance_id BIGINT NOT NULL,

    FOREIGN KEY (patient_id) REFERENCES users(user_id),
    FOREIGN KEY (insurance_id) REFERENCES insurances(insurance_id)
);

CREATE TABLE IF NOT EXISTS doctor_coverages (
    doctor_id BIGINT NOT NULL,
    insurance_id BIGINT NOT NULL,

    PRIMARY KEY(doctor_id, insurance_id),
    FOREIGN KEY (doctor_id) REFERENCES users(user_id),
    FOREIGN KEY (insurance_id) REFERENCES insurances(insurance_id)
);

CREATE TABLE IF NOT EXISTS doctor_shifts (
    shift_id SERIAL PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    shift_weekday INT NOT NULL,
    shift_address VARCHAR(50) NOT NULL,
    shift_start_time TIME NOT NULL,
    shift_end_time TIME NOT NULL,

    UNIQUE (doctor_id, shift_weekday, shift_start_time),
    UNIQUE (doctor_id, shift_weekday, shift_end_time),
    FOREIGN KEY (doctor_id) REFERENCES users(user_id),

    CONSTRAINT valid_time_range CHECK (shift_start_time < shift_end_time)
);

CREATE TABLE IF NOT EXISTS appointments (
    shift_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    appointment_date DATE NOT NULL,

    PRIMARY KEY(shift_id, appointment_date),
    FOREIGN KEY (shift_id) REFERENCES doctor_shifts(shift_id),
    FOREIGN KEY (patient_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS doctor_details (
    doctor_id BIGINT NOT NULL,
    doctor_licence VARCHAR(50) NOT NULL,
    doctor_specialty INT NOT NULL,

    PRIMARY KEY(doctor_id, doctor_specialty),
    FOREIGN KEY (doctor_id) REFERENCES users(user_id)
);

--ALTER TABLE doctor_details DROP CONSTRAINT doctor_details_pkey;
--ALTER TABLE doctor_details ADD PRIMARY KEY (doctor_id);
--UPDATE doctor_details SET doctor_licence = CONCAT(doctor_licence, '_', doctor_id) WHERE doctor_licence = 'med-licence';
--ALTER TABLE doctor_details ADD CONSTRAINT unique_doctor_licence UNIQUE (doctor_licence);

CREATE TABLE IF NOT EXISTS patient_details (
    patient_id BIGINT NOT NULL,
    patient_age INT,
    patient_blood_type INT,
    patient_height NUMERIC,
    patient_weight NUMERIC,
    patient_smokes BOOLEAN,
    patient_drinks BOOLEAN,
    patient_meds VARCHAR(250),
    patient_conditions VARCHAR(250),
    patient_allergies VARCHAR(250),
    patient_diet VARCHAR(100),
    patient_hobbies VARCHAR(100),
    patient_job VARCHAR(50),

    PRIMARY KEY(patient_id),
    FOREIGN KEY(patient_id) REFERENCES users(user_id)
);

-- INSERT INTO patient_details (patient_id)
-- SELECT user_id
-- FROM users
-- WHERE user_role = 0 --patient role
--  AND user_id NOT IN (SELECT patient_id FROM patient_details);

--ALTER TABLE patient_details
--ADD COLUMN patient_birthdate DATE;

--UPDATE patient_details
--SET patient_birthdate = make_date(EXTRACT(YEAR FROM CURRENT_DATE)::int - patient_age, 1, 1)
--WHERE patient_age IS NOT NULL;

--ALTER TABLE patient_details DROP COLUMN patient_age;

CREATE TABLE IF NOT EXISTS auth_doctors (
    doctor_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,

    PRIMARY KEY(doctor_id, patient_id),
    FOREIGN KEY (doctor_id) REFERENCES users(user_id),
    FOREIGN KEY (patient_id) REFERENCES users(user_id)
);

-- ALTER TABLE auth_doctors
-- ADD COLUMN allowed INT NOT NULL DEFAULT 0;
-- ALTER TABLE auth_doctors
-- RENAME COLUMN allowed TO access_level;
-- ALTER TABLE auth_doctors
-- DROP CONSTRAINT auth_doctors_pkey;
-- ALTER TABLE auth_doctors
-- ADD PRIMARY KEY (doctor_id, patient_id, access_level);

CREATE TABLE IF NOT EXISTS auth_studies (
    doctor_id BIGINT NOT NULL,
    study_id BIGINT NOT NULL,

    PRIMARY KEY(doctor_id, study_id),
    FOREIGN KEY (doctor_id) REFERENCES users(user_id),
    FOREIGN KEY (study_id) REFERENCES studies(study_id)
);

---------------------------------------------------------
--CAMBIOS SEGUNDA ENTREGA---



-- -- update to new table structure
-- INSERT INTO study_files (study_id, file_id)
-- SELECT study_id, file_id FROM studies;

-- -- remove old column
-- ALTER TABLE studies DROP COLUMN file_id;


CREATE TABLE IF NOT EXISTS patient_coverages (
    patient_id BIGINT PRIMARY KEY,
    insurance_id BIGINT NOT NULL,

    FOREIGN KEY (patient_id) REFERENCES users(user_id),
    FOREIGN KEY (insurance_id) REFERENCES insurances(insurance_id)
);

-- -- remove patient_coverages table
-- DROP TABLE IF EXISTS patient_coverage;


-- CREATE TABLE IF NOT EXISTS doctor_shifts (
--     shift_id SERIAL PRIMARY KEY,
--     doctor_id BIGINT NOT NULL,
--     shift_weekday INT NOT NULL,
--     shift_address VARCHAR(50) NOT NULL,
--     shift_start_time TIME NOT NULL,
--     shift_end_time TIME NOT NULL,

--     UNIQUE (doctor_id, shift_weekday, shift_start_time),
--     UNIQUE (doctor_id, shift_weekday, shift_end_time),
--     FOREIGN KEY (doctor_id) REFERENCES users(user_id),

--     CONSTRAINT valid_time_range CHECK (shift_start_time < shift_end_time)
-- );




-- CREATE TABLE IF NOT EXISTS appointments (
--     shift_id BIGINT NOT NULL,
--     patient_id BIGINT NOT NULL,
--     appointment_date DATE NOT NULL,

--     PRIMARY KEY(shift_id, appointment_date),
--     FOREIGN KEY (shift_id) REFERENCES doctor_shifts(shift_id),
--     FOREIGN KEY (patient_id) REFERENCES users(user_id)
-- );



-- -- add is_active column to doctor_single_shifts
-- ALTER TABLE doctor_single_shifts
-- ADD COLUMN IF NOT EXISTS shift_is_active BOOLEAN NOT NULL DEFAULT TRUE;




-- -- add patient insurance columns to patient_details
-- ALTER TABLE patient_details
-- ADD COLUMN patient_insurance_id BIGINT,
-- ADD FOREIGN KEY (patient_insurance_id) REFERENCES insurances(insurance_id),
-- ADD COLUMN patient_insurance_number VARCHAR(30);

