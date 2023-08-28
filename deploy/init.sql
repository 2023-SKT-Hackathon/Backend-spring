-- Database: seoul_genai

-- DROP DATABASE IF EXISTS seoul_genai;

CREATE DATABASE storytailor
    WITH
    OWNER = "default"
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;

CREATE SEQUENCE session_id_seq INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1;
CREATE TABLE session (
    id INT NOT NULL DEFAULT nextval('session_id_seq' :: regclass),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE SEQUENCE image_id_seq INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1;
CREATE TABLE image (
    id INT NOT NULL DEFAULT nextval('image_id_seq' :: regclass),
    origin_img_path TEXT ,
    expand_img_path TEXT ,
    session_id INT ,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE SEQUENCE chat_id_seq INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1;
CREATE TABLE chat (
    id INT NOT NULL DEFAULT nextval('chat_id_seq' :: regclass),
    session_id INT NOT NULL,
    msg_num INT,
    msg_type TEXT,
    text TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE SEQUENCE story_id_seq INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1;
CREATE TABLE story (
    id INT NOT NULL DEFAULT nextval('story_id_seq' :: regclass),
    session_id INT NOT NULL,
    cover_img_id INT NOT NULL,
    title TEXT,
    creater TEXT,
    lang TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE SEQUENCE page_id_seq INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1;
CREATE TABLE page (
    id INT NOT NULL DEFAULT nextval('page_id_seq' :: regclass),
    page_no INT ,
    page_img_path TEXT,
    page_content TEXT,
    story_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);


CREATE SEQUENCE keyword_id_seq INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1;
CREATE TABLE SESSION (
    id INT NOT NULL DEFAULT nextval('keyword_id_seq' :: regclass),
    type TEXT,
    keyword TEXT,
    story_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);