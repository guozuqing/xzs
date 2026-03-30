-- 天空领域-培训发展部考试系统 PostgreSQL 初始化脚本
-- 根据 MyBatis mapper 文件反推表结构

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. 用户表
CREATE TABLE IF NOT EXISTS t_user (
    id SERIAL PRIMARY KEY,
    user_uuid UUID,
    user_name VARCHAR(255),
    password VARCHAR(500),
    real_name VARCHAR(255),
    age INTEGER,
    sex INTEGER,
    birth_day TIMESTAMP,
    user_level INTEGER,
    phone VARCHAR(255),
    role INTEGER,
    status INTEGER,
    image_path VARCHAR(500),
    create_time TIMESTAMP,
    modify_time TIMESTAMP,
    last_active_time TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    wx_open_id VARCHAR(255)
);

-- 2. 学科表
CREATE TABLE IF NOT EXISTS t_subject (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    level INTEGER,
    level_name VARCHAR(255),
    item_order INTEGER,
    deleted BOOLEAN DEFAULT FALSE
);

-- 3. 文本内容表
CREATE TABLE IF NOT EXISTS t_text_content (
    id SERIAL PRIMARY KEY,
    content TEXT,
    create_time TIMESTAMP
);

-- 4. 题目表
CREATE TABLE IF NOT EXISTS t_question (
    id SERIAL PRIMARY KEY,
    question_type INTEGER,
    subject_id INTEGER,
    score INTEGER,
    grade_level INTEGER,
    difficult INTEGER,
    correct VARCHAR(255),
    info_text_content_id INTEGER,
    create_user INTEGER,
    status INTEGER,
    create_time TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 5. 试卷表
CREATE TABLE IF NOT EXISTS t_exam_paper (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    subject_id INTEGER,
    paper_type INTEGER,
    grade_level INTEGER,
    score INTEGER,
    question_count INTEGER,
    suggest_time INTEGER,
    limit_start_time TIMESTAMP,
    limit_end_time TIMESTAMP,
    frame_text_content_id INTEGER,
    create_user INTEGER,
    create_time TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    task_exam_id INTEGER
);

-- 6. 试卷答案表
CREATE TABLE IF NOT EXISTS t_exam_paper_answer (
    id SERIAL PRIMARY KEY,
    exam_paper_id INTEGER,
    paper_name VARCHAR(255),
    paper_type INTEGER,
    subject_id INTEGER,
    system_score INTEGER,
    user_score INTEGER,
    paper_score INTEGER,
    question_correct INTEGER,
    question_count INTEGER,
    do_time INTEGER,
    status INTEGER,
    create_user INTEGER,
    create_time TIMESTAMP,
    task_exam_id INTEGER
);

-- 7. 试卷题目客户答案表
CREATE TABLE IF NOT EXISTS t_exam_paper_question_customer_answer (
    id SERIAL PRIMARY KEY,
    question_id INTEGER,
    exam_paper_id INTEGER,
    exam_paper_answer_id INTEGER,
    question_type INTEGER,
    subject_id INTEGER,
    customer_score INTEGER,
    question_score INTEGER,
    question_text_content_id INTEGER,
    answer VARCHAR(255),
    text_content_id INTEGER,
    do_right BOOLEAN DEFAULT FALSE,
    create_user INTEGER,
    create_time TIMESTAMP,
    item_order INTEGER
);

-- 8. 消息表
CREATE TABLE IF NOT EXISTS t_message (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255),
    content TEXT,
    create_time TIMESTAMP,
    send_user_id INTEGER,
    send_user_name VARCHAR(255),
    send_real_name VARCHAR(255),
    receive_user_count INTEGER,
    read_count INTEGER
);

-- 9. 消息用户表
CREATE TABLE IF NOT EXISTS t_message_user (
    id SERIAL PRIMARY KEY,
    message_id INTEGER,
    receive_user_id INTEGER,
    receive_user_name VARCHAR(255),
    receive_real_name VARCHAR(255),
    readed BOOLEAN DEFAULT FALSE,
    create_time TIMESTAMP,
    read_time TIMESTAMP
);

-- 10. 任务试卷表
CREATE TABLE IF NOT EXISTS t_task_exam (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255),
    grade_level INTEGER,
    frame_text_content_id INTEGER,
    create_user INTEGER,
    create_time TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    create_user_name VARCHAR(255)
);

-- 11. 任务试卷客户答案表
CREATE TABLE IF NOT EXISTS t_task_exam_customer_answer (
    id SERIAL PRIMARY KEY,
    task_exam_id INTEGER,
    create_user INTEGER,
    create_time TIMESTAMP,
    text_content_id INTEGER
);

-- 12. 用户事件日志表
CREATE TABLE IF NOT EXISTS t_user_event_log (
    id SERIAL PRIMARY KEY,
    user_id INTEGER,
    user_name VARCHAR(255),
    real_name VARCHAR(255),
    content TEXT,
    create_time TIMESTAMP
);

-- 13. 用户Token表
CREATE TABLE IF NOT EXISTS t_user_token (
    id SERIAL PRIMARY KEY,
    token UUID,
    user_id INTEGER,
    wx_open_id VARCHAR(255),
    create_time TIMESTAMP,
    end_time TIMESTAMP,
    user_name VARCHAR(255)
);

-- 14. 视频表
CREATE TABLE IF NOT EXISTS t_video (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    subject_id INTEGER,
    video_url VARCHAR(1000),
    cover_url VARCHAR(1000),
    description TEXT,
    grade_level INTEGER,
    item_order INTEGER DEFAULT 0,
    create_user INTEGER,
    create_time TIMESTAMP DEFAULT NOW(),
    deleted BOOLEAN DEFAULT FALSE
);

-- 15. 视频章节表
CREATE TABLE IF NOT EXISTS t_video_chapter (
    id SERIAL PRIMARY KEY,
    video_id INTEGER NOT NULL,
    title VARCHAR(255) NOT NULL,
    video_url VARCHAR(1000),
    duration INTEGER DEFAULT 0,
    item_order INTEGER DEFAULT 0,
    create_time TIMESTAMP DEFAULT NOW(),
    deleted BOOLEAN DEFAULT FALSE
);
