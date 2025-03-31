CREATE TABLE IF NOT EXISTS files(
    file_id SERIAL PRIMARY KEY,
    file_content BYTEA NOT NULL,
    file_type VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS patients (
    patient_id SERIAL PRIMARY KEY,
    patient_email VARCHAR(100) NOT NULL UNIQUE,
    patient_password VARCHAR(100) NOT NULL,
    patient_name VARCHAR (100) NOT NULL,
    picture_id BIGINT NOT NULL DEFAULT 1,

    FOREIGN KEY (picture_id) REFERENCES files(file_id)
);

CREATE TABLE IF NOT EXISTS studies (
    study_id SERIAL PRIMARY KEY,
    study_type VARCHAR(100) NOT NULL,
    file_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,

    FOREIGN KEY (file_id) REFERENCES files(file_id),
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id)
);

CREATE TABLE IF NOT EXISTS physicians (
    physician_id SERIAL PRIMARY KEY,
    physician_email VARCHAR(100) NOT NULL UNIQUE,
    physician_password VARCHAR(100) NOT NULL,
    physician_name VARCHAR (100) NOT NULL,
    picture_id BIGINT NOT NULL DEFAULT 1,

    FOREIGN KEY (picture_id) REFERENCES files(file_id)
);