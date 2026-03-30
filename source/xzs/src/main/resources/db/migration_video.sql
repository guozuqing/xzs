-- 视频教学功能 - 数据库迁移脚本

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
