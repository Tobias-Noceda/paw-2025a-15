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
    file_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    uploader_id BIGINT NOT NULL,
    upload_date TIMESTAMP NOT NULL,
    study_type INT NOT NULL DEFAULT 0,
    study_date DATE NOT NULL,

    FOREIGN KEY (file_id) REFERENCES files(file_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (uploader_id) REFERENCES users(user_id)
);

CREATE TABLE patient_coverages (--TODO-check que el userId sea de un patient y insurance existente
    patient_id BIGINT PRIMARY KEY,
    insurance_id BIGINT NOT NULL,

    FOREIGN KEY (patient_id) REFERENCES users(user_id),
    FOREIGN KEY (insurance_id) REFERENCES insurances(insurance_id)
);

CREATE TABLE doctor_coverages (--TODO-check que el userId sea de un doctor y insurance existente
    doctor_id BIGINT NOT NULL,
    insurance_id BIGINT NOT NULL,

    PRIMARY KEY(doctor_id, insurance_id),
    FOREIGN KEY (doctor_id) REFERENCES users(user_id),
    FOREIGN KEY (insurance_id) REFERENCES insurances(insurance_id)
);

CREATE TABLE doctor_shifts (--TODO-check que el userId sea de un doctor
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

CREATE TABLE appointments (--TODO-check que el userId sea de un patient
    shift_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    appointment_date DATE NOT NULL,

    PRIMARY KEY(shift_id, appointment_date),
    FOREIGN KEY (shift_id) REFERENCES doctor_shifts(shift_id),
    FOREIGN KEY (patient_id) REFERENCES users(user_id)
);

CREATE TABLE doctor_details (--TODO-check que el userId sea de un doctor 
    doctor_id BIGINT NOT NULL,
    doctor_licence VARCHAR(50) NOT NULL,
    doctor_specialty INT NOT NULL,

    PRIMARY KEY(doctor_id, doctor_specialty),--TODO si no vamos a permitir mas de una specialty eliminar (ya lo asumimos al tener getDetailByDoctorId no mas y otras cosas)
    FOREIGN KEY (doctor_id) REFERENCES users(user_id)
);

CREATE TABLE patient_details (--TODO-check que el userId sea de un patient
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

    PRIMARY KEY(patient_id),
    FOREIGN KEY(patient_id) REFERENCES users(user_id)
);

CREATE TABLE auth_doctors (--TODO-check que el userId sea de un patient y el de doctor de doctor
    doctor_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    access_level INT NOT NULL DEFAULT 0,

    PRIMARY KEY(doctor_id, patient_id, access_level),
    FOREIGN KEY (doctor_id) REFERENCES users(user_id),
    FOREIGN KEY (patient_id) REFERENCES users(user_id)
);

CREATE TABLE auth_studies (--TODO-check que el userId sea de undoctor y que el study exista
    doctor_id BIGINT NOT NULL,
    study_id BIGINT NOT NULL,

    PRIMARY KEY(doctor_id, study_id),
    FOREIGN KEY (doctor_id) REFERENCES users(user_id),
    FOREIGN KEY (study_id) REFERENCES studies(study_id)
);