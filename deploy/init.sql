-- Database: seoul_genai

-- DROP DATABASE IF EXISTS seoul_genai;

CREATE DATABASE seoul_genai
    WITH
    OWNER = "default"
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;

CREATE SEQUENCE member_id_seq INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1;
CREATE TABLE member (
    id INT NOT NULL DEFAULT nextval('member_id_seq' :: regclass),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    password VARCHAR(255),
    phone VARCHAR(12),
    enroll_lecture INT,
    finsh_lecture INT,
    level TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
CREATE SEQUENCE lecture_id_seq INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1;
CREATE TABLE LECTURE (
    id INT NOT NULL DEFAULT nextval('lecture_id_seq' :: regclass),
    category TEXT NOT NULL,
    level TEXT NOT NULL,
    title TEXT NOT NULL,
    detail TEXT,
    total_time INT, --- hour
    thumenail_link TEXT,
    video_link TEXT,
    is_active boolean,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,    
    PRIMARY KEY (id)
);
CREATE SEQUENCE lecture_detail_id_seq INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1;
CREATE TABLE LECTURE_DETAIL (
    id INT NOT NULL DEFAULT nextval('lecture_detail_id_seq' :: regclass),
    lecture_seq INT NOT NULL,
    title TEXT NOT NULL,
    show_title TEXT,
    parent_lecture_id TEXT,
    video_link TEXT,
    stt_text TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,    
    PRIMARY KEY (id)
);
CREATE TABLE ENROLL_LECTURE_USER (
    user_id INT NOT NULL,
    lecture_id INT NOT NULL,
    running_time INT,
    enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_study_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE CHAT_HIST (
    session INT NOT NULL,
    user_id INT NOT NULL,
    msg_type INT NOT NULL,
    msg_num INT NOT NULL,
    msg TEXT NOT NULL,
    status TEXT,
    img_url TEXT,
    lecture_url TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE SEQUENCE session_id_seq INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1;
CREATE TABLE SESSION (
    session INT NOT NULL DEFAULT nextval('session_id_seq' :: regclass),
    user_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (session)
);