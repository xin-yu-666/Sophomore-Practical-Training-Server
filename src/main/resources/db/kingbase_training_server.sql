/*
 Navicat Premium Data Transfer

 Source Server         : training_server (MySQL)
 Target Server         : KingbaseES
 Date: 2025-07-04

*/

-- 删除可能已存在的同名模式，并创建新的模式
DROP SCHEMA IF EXISTS training_server CASCADE;
CREATE SCHEMA training_server;
-- 设置当前会话的搜索路径
SET search_path TO training_server;

-- ----------------------------
-- 触发器函数：用于自动更新 update_time 字段
-- ----------------------------
CREATE OR REPLACE FUNCTION update_timestamp_func()
RETURNS TRIGGER AS '
BEGIN
    NEW.update_time = now();
RETURN NEW;
END;
' LANGUAGE plpgsql;


-- ----------------------------
-- 表结构: course (课程表)
-- ----------------------------
DROP TABLE IF EXISTS "course";
CREATE TABLE "course" (
                          "id"          SERIAL NOT NULL,
                          "name"        varchar(255) NOT NULL,
                          "content"     text NULL,
                          "author"      varchar(100) NOT NULL,
                          "cover"       varchar(500) NULL,
                          "video"       varchar(500) NULL,
                          "create_time" timestamp NULL,
                          "update_time" timestamp NULL,
                          "user_id"     int NOT NULL,
                          "status"      smallint NOT NULL DEFAULT 0,
                          "summary"     varchar(500) NULL,
                          "orderNum"    int NULL
);

-- 添加注释
COMMENT ON TABLE "course" IS '课程表';
COMMENT ON COLUMN "course"."id" IS '课程ID，主键';
COMMENT ON COLUMN "course"."name" IS '课程标题';
COMMENT ON COLUMN "course"."content" IS '课程内容描述';
COMMENT ON COLUMN "course"."author" IS '课程作者';
COMMENT ON COLUMN "course"."cover" IS '课程封面图片URL';
COMMENT ON COLUMN "course"."video" IS '课程视频URL';
COMMENT ON COLUMN "course"."user_id" IS '上传用户的ID';
COMMENT ON COLUMN "course"."status" IS '状态：0-待审核，1-已通过，2-已拒绝';

-- 添加主键
ALTER TABLE "course" ADD CONSTRAINT "course_pkey" PRIMARY KEY ("id");

-- 创建索引
CREATE INDEX "idx_course_created_at" ON "course" USING btree ("create_time" ASC);
CREATE INDEX "idx_course_status" ON "course" USING btree ("status" ASC);
CREATE INDEX "idx_course_user_id" ON "course" USING btree ("user_id" ASC);

-- ----------------------------
-- 插入课程数据
-- ----------------------------
INSERT INTO "course" ("id", "name", "content", "author", "cover", "video", "create_time", "update_time", "user_id", "status", "summary", "orderNum") VALUES
                                                                                                                                                         (3, '1', '1', '1', '/uploads/images/3c3739b883c3427c9b8ca0e174ee061e.jpg', '/uploads/videos/6582eb514b314fbeb531c1404a7146db.mp4', '2025-06-30 14:56:55', NULL, 1, 1, '1', 2),
                                                                                                                                                         (4, '2', '2', '2', '/uploads/images/fdc14811665b403f8116dbcdc1abcb37.jpg', '/uploads/videos/b2e5ce83659d445bbc69c92dd8cbfa65.mp4', '2025-06-30 15:11:35', NULL, 1, 1, '2', 3),
                                                                                                                                                         (5, '4', '4', '4', '/uploads/images/340101da1a084e83aa3507153dc28c74.jpg', '/uploads/videos/a14f5c1cb63e4a7fa014245bf8e60462.mp4', '2025-06-30 15:21:43', NULL, 5, 1, '4', 4),
                                                                                                                                                         (6, '数学', '1+1=2', '人', '/uploads/images/2256c33b36b64e428718905930299fd6.jpg', '/uploads/videos/1da096b4801847a7822aef9c27f34fea.mp4', '2025-06-30 15:30:02', NULL, 5, 1, '一般', 6);
-- 重置自增序列
SELECT setval('course_id_seq', (SELECT MAX(id) FROM course));


-- ----------------------------
-- 表结构: enterprise (企业表)
-- ----------------------------
DROP TABLE IF EXISTS "enterprise";
CREATE TABLE "enterprise" (
                              "id"          BIGSERIAL NOT NULL,
                              "name"        varchar(100) NOT NULL,
                              "contact"     varchar(50) NOT NULL,
                              "phone"       varchar(20) NOT NULL,
                              "email"       varchar(100) NOT NULL,
                              "status"      smallint NULL DEFAULT 1,
                              "create_time" timestamp NULL DEFAULT now(),
                              "update_time" timestamp NULL DEFAULT now()
);

COMMENT ON TABLE "enterprise" IS '企业表';
COMMENT ON COLUMN "enterprise"."name" IS '企业名称';
COMMENT ON COLUMN "enterprise"."contact" IS '联系人';
COMMENT ON COLUMN "enterprise"."phone" IS '联系电话';
COMMENT ON COLUMN "enterprise"."email" IS '电子邮箱';
COMMENT ON COLUMN "enterprise"."status" IS '状态：0-禁用 1-启用';
COMMENT ON COLUMN "enterprise"."create_time" IS '创建时间';
COMMENT ON COLUMN "enterprise"."update_time" IS '更新时间';

ALTER TABLE "enterprise" ADD CONSTRAINT "enterprise_pkey" PRIMARY KEY ("id");

-- 创建触发器以自动更新 update_time
CREATE TRIGGER "enterprise_update_time_trigger"
    BEFORE UPDATE ON "enterprise"
    FOR EACH ROW
    EXECUTE PROCEDURE update_timestamp_func();

-- ----------------------------
-- 插入企业数据
-- ----------------------------
INSERT INTO "enterprise" ("id", "name", "contact", "phone", "email", "status", "create_time", "update_time") VALUES
                                                                                                                 (1, '阿里巴巴', '马云', '13800138001', 'alibaba@example.com', 1, '2025-06-21 20:56:40', '2025-06-21 20:56:40'),
                                                                                                                 (2, '腾讯科技', '马化腾', '13800138002', 'tencent@example.com', 1, '2025-06-21 20:56:40', '2025-06-21 20:56:40'),
                                                                                                                 (3, '百度在线', '李彦宏', '13800138003', 'baidu@example.com', 1, '2025-06-21 20:56:40', '2025-06-21 20:56:40'),
                                                                                                                 (4, '京东集团', '刘强东', '13800138004', 'jd@example.com', 1, '2025-06-21 20:56:40', '2025-06-21 20:56:40'),
                                                                                                                 (5, '小米科技', '雷军', '13800138005', 'xiaomi@example.com', 1, '2025-06-21 20:56:40', '2025-06-21 20:56:40'),
                                                                                                                 (7, '美团', '王兴', '13900139002', 'mt@example.com', 1, '2025-06-29 14:29:13', '2025-06-29 14:29:13'),
                                                                                                                 (8, '饿了么', '张旭豪', '13900139001', 'elm@example.com', 1, '2025-06-29 14:29:59', '2025-06-29 14:29:59');
SELECT setval('enterprise_id_seq', (SELECT MAX(id) FROM enterprise));


-- ----------------------------
-- 表结构: meeting (会议表)
-- ----------------------------
DROP TABLE IF EXISTS "meeting";
CREATE TABLE "meeting" (
                           "id"          BIGSERIAL NOT NULL,
                           "name"        varchar(255) NOT NULL,
                           "start_time"  timestamp NOT NULL,
                           "end_time"    timestamp NOT NULL,
                           "creator"     varchar(100) NOT NULL,
                           "content"     text NULL,
                           "create_time" timestamp NULL DEFAULT now(),
                           "user_id"     bigint NULL,
                           "status"      int NULL DEFAULT 0
);

COMMENT ON TABLE "meeting" IS '会议表';
COMMENT ON COLUMN "meeting"."id" IS '主键ID';
COMMENT ON COLUMN "meeting"."name" IS '会议名称';
COMMENT ON COLUMN "meeting"."start_time" IS '会议开始时间';
COMMENT ON COLUMN "meeting"."end_time" IS '会议结束时间';
COMMENT ON COLUMN "meeting"."creator" IS '创建人';
COMMENT ON COLUMN "meeting"."content" IS '会议内容';
COMMENT ON COLUMN "meeting"."create_time" IS '创建时间';

ALTER TABLE "meeting" ADD CONSTRAINT "meeting_pkey" PRIMARY KEY ("id");

-- meeting 表没有初始数据

-- ----------------------------
-- 表结构: news (新闻表)
-- ----------------------------
DROP TABLE IF EXISTS "news";
CREATE TABLE "news" (
                        "id"         BIGSERIAL NOT NULL,
                        "title"      varchar(200) NOT NULL,
                        "summary"    varchar(500) NULL,
                        "author"     varchar(100) NULL,
                        "imageUrl"   bytea NULL,
                        "content"    text NULL,
                        "createTime" timestamp NULL DEFAULT now(),
                        "user_id"    bigint NULL,
                        "status"     smallint NULL DEFAULT 0
);

COMMENT ON TABLE "news" IS '新闻表';
COMMENT ON COLUMN "news"."title" IS '新闻标题';
COMMENT ON COLUMN "news"."summary" IS '新闻简介';
COMMENT ON COLUMN "news"."author" IS '作者';
COMMENT ON COLUMN "news"."imageUrl" IS '新闻图片(bytea存储)';
COMMENT ON COLUMN "news"."content" IS '新闻内容';
COMMENT ON COLUMN "news"."createTime" IS '创建时间';
COMMENT ON COLUMN "news"."user_id" IS '发布者ID';
COMMENT ON COLUMN "news"."status" IS '状态：0-待审核 1-已通过 2-已拒绝';

ALTER TABLE "news" ADD CONSTRAINT "news_pkey" PRIMARY KEY ("id");
-- 由于 bytea 数据很长，这里只做示例，实际使用时应通过程序插入
-- INSERT INTO "news" ("id", "title", "summary", "author", "imageUrl", "content", "createTime", "user_id", "status") VALUES (1, '22', '22', '22', E'\\x...很长的数据...', '11111', '2025-06-28 15:01:58', 5, 1);
-- SELECT setval('news_id_seq', (SELECT MAX(id) FROM news));


-- ----------------------------
-- 表结构: permission (权限表)
-- ----------------------------
DROP TABLE IF EXISTS "permission";
CREATE TABLE "permission" (
                              "id"          BIGSERIAL NOT NULL,
                              "name"        varchar(50) NOT NULL,
                              "code"        varchar(50) NOT NULL,
                              "description" varchar(200) NULL,
                              "create_time" timestamp NULL DEFAULT now(),
                              "update_time" timestamp NULL DEFAULT now()
);
COMMENT ON TABLE "permission" IS '权限表';
COMMENT ON COLUMN "permission"."name" IS '权限名称';
COMMENT ON COLUMN "permission"."code" IS '权限编码';
COMMENT ON COLUMN "permission"."description" IS '权限描述';
COMMENT ON COLUMN "permission"."create_time" IS '创建时间';
COMMENT ON COLUMN "permission"."update_time" IS '更新时间';

ALTER TABLE "permission" ADD CONSTRAINT "permission_pkey" PRIMARY KEY ("id");
ALTER TABLE "permission" ADD CONSTRAINT "uk_permission_code" UNIQUE ("code");

CREATE TRIGGER "permission_update_time_trigger" BEFORE UPDATE ON "permission" FOR EACH ROW EXECUTE PROCEDURE update_timestamp_func();

-- ----------------------------
-- 插入权限数据
-- ----------------------------
INSERT INTO "permission" VALUES
                             (1, '用户管理', 'USER_MANAGE', '用户管理权限', '2025-06-21 20:56:40', '2025-06-21 20:56:40'),
                             (2, '企业管理', 'ENTERPRISE_MANAGE', '企业管理权限', '2025-06-21 20:56:40', '2025-06-21 20:56:40'),
                             (3, '个人信息管理', 'PROFILE_MANAGE', '个人信息管理权限', '2025-06-21 20:56:40', '2025-06-21 20:56:40'),
                             (4, '行业动态发布', 'NEWS_PUBLISH', '发布行业动态', '2025-06-24 22:38:02', '2025-06-24 22:38:06'),
                             (5, '行业动态审核', 'NEWS_AUDIT', '审核行业动态', '2025-06-24 22:39:00', '2025-06-24 22:39:02'),
                             (6, '行业动态编辑', 'NEWS_EDIT', '编辑行业动态', '2025-06-24 22:39:32', '2025-06-24 22:39:34'),
                             (7, '行业动态删除', 'NEWS_DELETE', '删除行业动态', '2025-06-24 22:40:37', '2025-06-24 22:40:38'),
                             (8, '行业动态查看', 'NEWS_VIEW', '查看行业动态', '2025-06-24 22:41:18', '2025-06-24 22:41:19'),
                             (9, '会议查看', 'MEETING_VIEW', '会议查看权限', '2025-06-30 16:12:58', '2025-06-30 16:12:59'),
                             (10, '会议发布', 'MEETING_PUBLISH', '会议发布权限', '2025-06-30 16:13:37', '2025-06-30 16:13:38'),
                             (11, '会议编辑', 'MEETING_EDIT', '会议编辑权限', '2025-06-30 16:14:19', '2025-06-30 16:14:20'),
                             (12, '会议删除', 'MEETING_DELETE', '会议删除权限', '2025-06-30 16:15:23', '2025-06-30 16:15:24'),
                             (13, '会议审核', 'MEETING_AUDIT', '会议审核权限', '2025-06-30 16:16:01', '2025-06-30 16:16:01');
SELECT setval('permission_id_seq', (SELECT MAX(id) FROM permission));


-- ----------------------------
-- 表结构: role (角色表)
-- ----------------------------
DROP TABLE IF EXISTS "role";
CREATE TABLE "role" (
                        "id"          BIGSERIAL NOT NULL,
                        "name"        varchar(50) NOT NULL,
                        "code"        varchar(50) NOT NULL,
                        "description" varchar(200) NULL,
                        "create_time" timestamp NULL DEFAULT now(),
                        "update_time" timestamp NULL DEFAULT now()
);

COMMENT ON TABLE "role" IS '角色表';
COMMENT ON COLUMN "role"."name" IS '角色名称';
COMMENT ON COLUMN "role"."code" IS '角色编码';
COMMENT ON COLUMN "role"."description" IS '角色描述';
COMMENT ON COLUMN "role"."create_time" IS '创建时间';
COMMENT ON COLUMN "role"."update_time" IS '更新时间';

ALTER TABLE "role" ADD CONSTRAINT "role_pkey" PRIMARY KEY ("id");
ALTER TABLE "role" ADD CONSTRAINT "uk_role_code" UNIQUE ("code");
CREATE TRIGGER "role_update_time_trigger" BEFORE UPDATE ON "role" FOR EACH ROW EXECUTE PROCEDURE update_timestamp_func();

-- ----------------------------
-- 插入角色数据
-- ----------------------------
INSERT INTO "role" VALUES
                       (1, '超级管理员', 'ROLE_ADMIN', '系统超级管理员', '2025-06-21 20:56:40', '2025-06-21 20:56:40'),
                       (2, '企业用户', 'ROLE_ENTERPRISE', '企业普通用户', '2025-06-21 20:56:40', '2025-06-21 20:56:40');
SELECT setval('role_id_seq', (SELECT MAX(id) FROM role));


-- ----------------------------
-- 表结构: role_permission (角色权限关联表)
-- ----------------------------
DROP TABLE IF EXISTS "role_permission";
CREATE TABLE "role_permission" (
                                   "id"            BIGSERIAL NOT NULL,
                                   "role_id"       bigint NOT NULL,
                                   "permission_id" bigint NOT NULL,
                                   "create_time"   timestamp NULL DEFAULT now(),
                                   "update_time"   timestamp NULL DEFAULT now()
);
COMMENT ON TABLE "role_permission" IS '角色权限关联表';
COMMENT ON COLUMN "role_permission"."role_id" IS '角色ID';
COMMENT ON COLUMN "role_permission"."permission_id" IS '权限ID';

ALTER TABLE "role_permission" ADD CONSTRAINT "role_permission_pkey" PRIMARY KEY ("id");
ALTER TABLE "role_permission" ADD CONSTRAINT "uk_role_permission" UNIQUE ("role_id", "permission_id");
CREATE TRIGGER "role_permission_update_time_trigger" BEFORE UPDATE ON "role_permission" FOR EACH ROW EXECUTE PROCEDURE update_timestamp_func();

-- ----------------------------
-- 插入角色权限数据
-- ----------------------------
INSERT INTO "role_permission" VALUES
                                  (1, 1, 2, '2025-06-21 20:56:40', '2025-06-21 20:56:40'),
                                  (2, 1, 3, '2025-06-21 20:56:40', '2025-06-21 20:56:40'),
                                  (3, 1, 1, '2025-06-21 20:56:40', '2025-06-21 20:56:40'),
                                  (4, 1, 4, '2025-06-21 20:56:40', '2025-06-24 22:43:24'),
                                  (5, 1, 5, '2025-06-24 22:42:11', '2025-06-24 22:42:12'),
                                  (6, 1, 6, '2025-06-24 22:42:29', '2025-06-24 22:42:31'),
                                  (7, 1, 7, '2025-06-24 22:42:30', '2025-06-24 22:42:32'),
                                  (8, 1, 8, '2025-06-24 22:43:21', '2025-06-24 22:43:22'),
                                  (9, 2, 3, '2025-06-24 22:44:20', '2025-06-24 22:44:25'),
                                  (10, 2, 4, '2025-06-24 22:44:21', '2025-06-24 22:44:26'),
                                  (11, 2, 6, '2025-06-24 22:44:22', '2025-06-24 22:44:27'),
                                  (12, 2, 7, '2025-06-24 22:44:23', '2025-06-24 22:44:27'),
                                  (13, 2, 8, '2025-06-24 22:44:24', '2025-06-24 22:44:28'),
                                  (14, 1, 9, '2025-06-30 16:17:44', '2025-06-30 16:17:44'),
                                  (15, 1, 10, '2025-06-30 16:17:53', '2025-06-30 16:17:54'),
                                  (16, 1, 11, '2025-06-30 16:18:07', '2025-06-30 16:18:08'),
                                  (17, 1, 12, '2025-06-30 16:18:23', '2025-06-30 16:18:24'),
                                  (18, 1, 13, '2025-06-30 16:18:31', '2025-06-30 16:18:32'),
                                  (19, 2, 9, '2025-06-30 16:18:45', '2025-06-30 16:18:46'),
                                  (20, 2, 10, '2025-06-30 16:18:53', '2025-06-30 16:18:53'),
                                  (21, 2, 11, '2025-06-30 16:18:59', '2025-06-30 16:19:00'),
                                  (22, 2, 12, '2025-06-30 16:19:11', '2025-06-30 16:19:11');
SELECT setval('role_permission_id_seq', (SELECT MAX(id) FROM role_permission));

-- ----------------------------
-- 表结构: user (用户表)
-- ----------------------------
DROP TABLE IF EXISTS "user";
CREATE TABLE "user" (
    "id" SERIAL PRIMARY KEY,
    "username" VARCHAR(64) NOT NULL UNIQUE,
    "password" VARCHAR(128) NOT NULL,
    "nickname" VARCHAR(64),
    "phone" VARCHAR(32),
    "email" VARCHAR(128),
    "gender" VARCHAR(8),
    "avatar" BYTEA,
    "enterprise_id" INT,
    "status" INT DEFAULT 1
);
COMMENT ON TABLE "user" IS '用户表';
COMMENT ON COLUMN "user"."username" IS '用户名';
COMMENT ON COLUMN "user"."password" IS '密码';
COMMENT ON COLUMN "user"."nickname" IS '昵称';
COMMENT ON COLUMN "user"."phone" IS '手机号';
COMMENT ON COLUMN "user"."email" IS '邮箱';
COMMENT ON COLUMN "user"."gender" IS '性别';
COMMENT ON COLUMN "user"."avatar" IS '头像(bytea存储)';
COMMENT ON COLUMN "user"."enterprise_id" IS '企业ID';
COMMENT ON COLUMN "user"."status" IS '状态：0-禁用 1-启用';
COMMENT ON COLUMN "user"."create_time" IS '创建时间';
COMMENT ON COLUMN "user"."update_time" IS '更新时间';

ALTER TABLE "user" ADD CONSTRAINT "user_pkey" PRIMARY KEY ("id");
ALTER TABLE "user" ADD CONSTRAINT "uk_user_username" UNIQUE ("username");
CREATE TRIGGER "user_update_time_trigger" BEFORE UPDATE ON "user" FOR EACH ROW EXECUTE PROCEDURE update_timestamp_func();

-- ----------------------------
-- 插入用户数据
-- ----------------------------
-- 由于 bytea 数据很长，这里只做示例，实际使用时应通过程序插入
-- INSERT INTO "user" ("id", "username", "password", "nickname", "phone", "email", "gender", "avatar", "enterprise_id", "status", "create_time", "update_time") VALUES (1, 'admin', '1234567', '系统管理员', '13900139001', 'admin1@example.com', '男', E'\\x...很长的数据...', NULL, 1, '2025-06-21 20:56:40', '2025-06-30 15:47:09');
-- SELECT setval('user_id_seq', (SELECT MAX(id) FROM "user"));

-- ----------------------------
-- 表结构: user_role (用户角色关联表)
-- ----------------------------
DROP TABLE IF EXISTS "user_role";
CREATE TABLE "user_role" (
                             "id"          BIGSERIAL NOT NULL,
                             "user_id"     bigint NOT NULL,
                             "role_id"     bigint NOT NULL,
                             "create_time" timestamp NULL DEFAULT now(),
                             "update_time" timestamp NULL DEFAULT now()
);

COMMENT ON TABLE "user_role" IS '用户角色关联表';
COMMENT ON COLUMN "user_role"."user_id" IS '用户ID';
COMMENT ON COLUMN "user_role"."role_id" IS '角色ID';

ALTER TABLE "user_role" ADD CONSTRAINT "user_role_pkey" PRIMARY KEY ("id");
ALTER TABLE "user_role" ADD CONSTRAINT "uk_user_role" UNIQUE ("user_id", "role_id");
CREATE TRIGGER "user_role_update_time_trigger" BEFORE UPDATE ON "user_role" FOR EACH ROW EXECUTE PROCEDURE update_timestamp_func();

-- ----------------------------
-- 插入用户角色数据
-- ----------------------------
INSERT INTO "user_role" VALUES
                            (1, 1, 1, '2025-06-21 20:56:40', '2025-06-21 20:56:40'),
                            (2, 2, 2, '2025-06-21 20:56:40', '2025-06-21 20:56:40'),
                            (3, 3, 2, '2025-06-21 20:56:40', '2025-06-21 20:56:40'),
                            (4, 4, 2, '2025-06-21 20:56:40', '2025-06-21 20:56:40'),
                            (5, 5, 2, '2025-06-21 20:56:40', '2025-06-21 20:56:40'),
                            (6, 6, 2, '2025-06-21 20:56:40', '2025-06-21 20:56:40'),
                            (8, 12, 2, '2025-06-24 21:22:59', '2025-06-24 21:22:59'),
                            (9, 13, 2, '2025-06-26 20:32:39', '2025-06-26 20:32:39'),
                            (10, 14, 2, '2025-06-28 20:44:53', '2025-06-28 20:44:53'),
                            (11, 15, 2, '2025-06-29 15:41:41', '2025-06-29 15:41:41'),
                            (12, 16, 2, '2025-06-29 15:50:02', '2025-06-29 15:50:02');
SELECT setval('user_role_id_seq', (SELECT MAX(id) FROM user_role)); 