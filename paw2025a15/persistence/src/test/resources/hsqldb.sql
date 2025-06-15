CREATE TABLE files(
    file_id SERIAL PRIMARY KEY,
    file_content BYTEA NOT NULL,
    file_type INT NOT NULL
);

CREATE TABLE insurances(
    insurance_id SERIAL PRIMARY KEY,
    insurance_name VARCHAR(100) NOT NULL UNIQUE,
    picture_id BIGINT NOT NULL DEFAULT 1,

    FOREIGN KEY (picture_id) REFERENCES files(file_id)
);

CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    user_email VARCHAR(100) NOT NULL UNIQUE,
    user_password VARCHAR(100) NOT NULL,
    user_name VARCHAR (100) NOT NULL,
    picture_id BIGINT NOT NULL DEFAULT 1,
    user_role INT NOT NULL DEFAULT 0,
    user_telephone VARCHAR(20) NOT NULL DEFAULT '1112345678',
    locale INT NOT NULL DEFAULT 0,
    create_date DATE NOT NULL,

    FOREIGN KEY (picture_id) REFERENCES files(file_id)
);

CREATE TABLE studies (
    study_id SERIAL PRIMARY KEY,
    study_comment VARCHAR(100),
    user_id BIGINT NOT NULL,
    uploader_id BIGINT NOT NULL,
    upload_date TIMESTAMP NOT NULL,
    study_type INT NOT NULL DEFAULT 0,
    study_date DATE NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (uploader_id) REFERENCES users(user_id)
);

CREATE TABLE study_files (
    study_id BIGINT NOT NULL,
    file_id BIGINT NOT NULL,

    PRIMARY KEY(study_id, file_id),
    FOREIGN KEY (study_id) REFERENCES studies(study_id),
    FOREIGN KEY (file_id) REFERENCES files(file_id)
);

CREATE TABLE doctor_coverages (
    doctor_id BIGINT NOT NULL,
    insurance_id BIGINT NOT NULL,

    PRIMARY KEY(doctor_id, insurance_id),
    FOREIGN KEY (doctor_id) REFERENCES users(user_id),
    FOREIGN KEY (insurance_id) REFERENCES insurances(insurance_id)
);

CREATE TABLE doctor_single_shifts (
    shift_id SERIAL PRIMARY KEY,
    doctor_id INT NOT NULL,
    shift_weekday INT NOT NULL,
    shift_address VARCHAR(50) NOT NULL,
    shift_start_time TIME NOT NULL,
    shift_end_time TIME NOT NULL,
    shift_duration INT NOT NULL,
    shift_is_active BOOLEAN NOT NULL DEFAULT TRUE,

    FOREIGN KEY (doctor_id) REFERENCES users(user_id)
);

CREATE TABLE appointments_new (
    shift_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_start_time TIME NOT NULL,
    appointment_end_time TIME NOT NULL,
    appointment_detail VARCHAR(500),

    PRIMARY KEY(shift_id, appointment_date, appointment_start_time, appointment_end_time),
    FOREIGN KEY (shift_id) REFERENCES doctor_single_shifts(shift_id),
    FOREIGN KEY (patient_id) REFERENCES users(user_id)
);

CREATE TABLE doctor_details ( 
    doctor_id BIGINT NOT NULL,
    doctor_licence VARCHAR(50) UNIQUE NOT NULL,
    doctor_specialty INT NOT NULL,

    PRIMARY KEY(doctor_id),
    FOREIGN KEY (doctor_id) REFERENCES users(user_id)
);

CREATE TABLE doctor_vacations (
    doctor_id BIGINT NOT NULL,
    vacation_start_date DATE NOT NULL,
    vacation_end_date DATE NOT NULL,

    PRIMARY KEY(doctor_id, vacation_start_date, vacation_end_date),
    FOREIGN KEY (doctor_id) REFERENCES users(user_id)
);

CREATE TABLE patient_details (
    patient_id BIGINT NOT NULL,
    patient_birthdate DATE,
    patient_blood_type INT,
    patient_height NUMERIC(10,2),
    patient_weight NUMERIC(10,2),
    patient_smokes BOOLEAN,
    patient_drinks BOOLEAN,
    patient_meds VARCHAR(250),
    patient_conditions VARCHAR(250),
    patient_allergies VARCHAR(250),
    patient_diet VARCHAR(100),
    patient_hobbies VARCHAR(100),
    patient_job VARCHAR(50),
    patient_insurance_id BIGINT,
    patient_insurance_number VARCHAR(30),

    PRIMARY KEY(patient_id),
    FOREIGN KEY(patient_id) REFERENCES users(user_id),
    FOREIGN KEY (patient_insurance_id) REFERENCES insurances(insurance_id)
);

CREATE TABLE auth_doctors (
    doctor_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    access_level INT NOT NULL DEFAULT 0,

    PRIMARY KEY(doctor_id, patient_id, access_level),
    FOREIGN KEY (doctor_id) REFERENCES users(user_id),
    FOREIGN KEY (patient_id) REFERENCES users(user_id)
);

CREATE TABLE auth_studies (
    doctor_id BIGINT NOT NULL,
    study_id BIGINT NOT NULL,

    PRIMARY KEY(doctor_id, study_id),
    FOREIGN KEY (doctor_id) REFERENCES users(user_id),
    FOREIGN KEY (study_id) REFERENCES studies(study_id)
);

CREATE SEQUENCE files_file_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE insurances_insurance_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE users_user_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE studies_study_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE doctor_shifts_shift_id_seq START WITH 1 INCREMENT BY 1;