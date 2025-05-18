CREATE SCHEMA IF NOT EXISTS findjob;
USE findjob;

CREATE TABLE User
(
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    user_name NVARCHAR(50),
    account VARCHAR(20) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role INT NOT NULL
);

CREATE TABLE Job
(
    job_id INT PRIMARY KEY AUTO_INCREMENT,
    job_name NVARCHAR(300),
    salary DECIMAL(10,2),
    company_name NVARCHAR(300) NOT NULL,
    description NVARCHAR(1000),
    is_public int DEFAULT 0 NOT NULL,
    requirement NVARCHAR(1000),
    address NVARCHAR(200)
);

CREATE TABLE Category
(
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    category_name NVARCHAR(200) NOT NULL
);

CREATE TABLE CategoryOfJob
(
    category_of_job_id INT PRIMARY KEY AUTO_INCREMENT,
    job_id INT NOT NULL,
    category_id INT NOT NULL,
    FOREIGN KEY (job_id) REFERENCES Job(job_id),
    FOREIGN KEY (category_id) REFERENCES Category(category_id)
);

CREATE TABLE UserApplyJob
(
    user_apply_job_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    job_id INT NOT NULL,
    cv_link VARCHAR(400),
    state_id INT DEFAULT 0 NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (job_id) REFERENCES Job(job_id)
);

CREATE TABLE UserSaveJob
(
    user_save_job_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    job_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (job_id) REFERENCES Job(job_id)
);
