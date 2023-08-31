-- Database: seoul_genai

-- DROP DATABASE IF EXISTS seoul_genai;

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
    cover_img_url TEXT,
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
CREATE TABLE keyword (
    id INT NOT NULL DEFAULT nextval('keyword_id_seq' :: regclass),
    session_id INT,
    type TEXT,
    keyword TEXT,
    story_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
CREATE SEQUENCE temp_keyword_id_seq INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1;
CREATE TABLE temp_keyword (
    id INT NOT NULL DEFAULT nextval('keyword_id_seq' :: regclass),
    session_id INT,
    type TEXT,
    keyword TEXT,
    story_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);