/*
 Navicat Premium Data Transfer

 Source Server         : xchat-server
 Source Server Type    : SQLite
 Source Server Version : 3030001
 Source Schema         : main

 Target Server Type    : SQLite
 Target Server Version : 3030001
 File Encoding         : 65001

 Date: 31/01/2022 22:50:20
*/

PRAGMA foreign_keys = false;

-- ----------------------------
-- Table structure for r_members
-- ----------------------------
DROP TABLE IF EXISTS "r_members";
CREATE TABLE "r_members" (
  "room_id" integer NOT NULL,
  "uid" integer NOT NULL,
  "role" text NOT NULL DEFAULT '',
  "permissions" integer NOT NULL DEFAULT 7,
  "join_time" integer NOT NULL DEFAULT 0,
  PRIMARY KEY ("room_id", "uid"),
  CONSTRAINT "room_id" FOREIGN KEY ("room_id") REFERENCES "t_rooms" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT "uid" FOREIGN KEY ("uid") REFERENCES "t_users" ("uid") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for r_title_own
-- ----------------------------
DROP TABLE IF EXISTS "r_title_own";
CREATE TABLE "r_title_own" (
  "tid" integer NOT NULL,
  "uid" integer NOT NULL,
  "img_file_id" integer DEFAULT 0,
  "creation_time" integer NOT NULL,
  PRIMARY KEY ("tid", "uid"),
  CONSTRAINT "tid" FOREIGN KEY ("tid") REFERENCES "t_titles" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT "uid" FOREIGN KEY ("uid") REFERENCES "t_users" ("uid") ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT "img_file_id" FOREIGN KEY ("img_file_id") REFERENCES "t_files" ("id") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for sqlite_sequence
-- ----------------------------
DROP TABLE IF EXISTS "sqlite_sequence";
CREATE TABLE sqlite_sequence(name,seq);

-- ----------------------------
-- Table structure for t_files
-- ----------------------------
DROP TABLE IF EXISTS "t_files";
CREATE TABLE "t_files" (
  "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  "dir_id" integer NOT NULL,
  "title" text NOT NULL,
  "size" integer NOT NULL DEFAULT 0,
  "type" text NOT NULL DEFAULT '',
  "status" integer NOT NULL DEFAULT 0,
  "hash" text NOT NULL DEFAULT '',
  "quote_count" integer NOT NULL DEFAULT 0,
  "create_time" integer NOT NULL DEFAULT 0,
  CONSTRAINT "dir_id" FOREIGN KEY ("dir_id") REFERENCES "t_files" ("id") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for t_mails
-- ----------------------------
DROP TABLE IF EXISTS "t_mails";
CREATE TABLE "t_mails" (
  "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  "sender_id" integer NOT NULL,
  "receiver_id" integer NOT NULL,
  "theme" text NOT NULL DEFAULT '',
  "content" text NOT NULL DEFAULT '',
  "creation_time" integer NOT NULL DEFAULT 0,
  CONSTRAINT "sender_id" FOREIGN KEY ("sender_id") REFERENCES "t_users" ("uid") ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT "receiver_id" FOREIGN KEY ("receiver_id") REFERENCES "t_users" ("uid") ON DELETE NO ACTION ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for t_messages
-- ----------------------------
DROP TABLE IF EXISTS "t_messages";
CREATE TABLE "t_messages" (
  "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  "sender_id" integer NOT NULL,
  "room_id" integer NOT NULL,
  "type" text NOT NULL,
  "content" text NOT NULL,
  "sign" text NOT NULL,
  "delete_mark" integer NOT NULL DEFAULT 0,
  "time_stamp" integer NOT NULL DEFAULT 0,
  CONSTRAINT "sender_id" FOREIGN KEY ("sender_id") REFERENCES "t_users" ("uid") ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT "room_id" FOREIGN KEY ("room_id") REFERENCES "t_rooms" ("id") ON DELETE NO ACTION ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for t_rooms
-- ----------------------------
DROP TABLE IF EXISTS "t_rooms";
CREATE TABLE "t_rooms" (
  "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  "topic" text NOT NULL,
  "file_id" integer NOT NULL,
  "description" text NOT NULL DEFAULT '',
  "delete_mark" integer NOT NULL DEFAULT 0,
  "creation_time" integer NOT NULL DEFAULT 0,
  CONSTRAINT "file_id" FOREIGN KEY ("file_id") REFERENCES "t_files" ("id") ON DELETE RESTRICT ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for t_titles
-- ----------------------------
DROP TABLE IF EXISTS "t_titles";
CREATE TABLE "t_titles" (
  "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  "title" text NOT NULL,
  "creation_time" integer NOT NULL DEFAULT 0
);

-- ----------------------------
-- Table structure for t_user_attributes
-- ----------------------------
DROP TABLE IF EXISTS "t_user_attributes";
CREATE TABLE "t_user_attributes" (
  "uid" integer NOT NULL,
  "key" text NOT NULL,
  "value" text NOT NULL,
  PRIMARY KEY ("uid", "key"),
  CONSTRAINT "uid" FOREIGN KEY ("uid") REFERENCES "t_users" ("uid") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for t_users
-- ----------------------------
DROP TABLE IF EXISTS "t_users";
CREATE TABLE "t_users" (
  "uid" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  "uid_code" text NOT NULL,
  "level" integer NOT NULL DEFAULT 0,
  "exp" integer NOT NULL DEFAULT 0,
  "status" integer NOT NULL DEFAULT 0,
  "time_stamp" integer NOT NULL DEFAULT 0
);

-- ----------------------------
-- Auto increment value for t_files
-- ----------------------------

-- ----------------------------
-- Auto increment value for t_mails
-- ----------------------------

-- ----------------------------
-- Auto increment value for t_messages
-- ----------------------------

-- ----------------------------
-- Auto increment value for t_rooms
-- ----------------------------

-- ----------------------------
-- Auto increment value for t_titles
-- ----------------------------
UPDATE "main"."sqlite_sequence" SET seq = 1 WHERE name = 't_titles';

-- ----------------------------
-- Auto increment value for t_users
-- ----------------------------
UPDATE "main"."sqlite_sequence" SET seq = 1 WHERE name = 't_users';

PRAGMA foreign_keys = true;
