/*
 Navicat Premium Data Transfer

 Source Server         : xchat-server
 Source Server Type    : SQLite
 Source Server Version : 3030001
 Source Schema         : main

 Target Server Type    : SQLite
 Target Server Version : 3030001
 File Encoding         : 65001

 Date: 30/03/2022 22:31:31
*/

PRAGMA foreign_keys = false;

-- ----------------------------
-- Table structure for r_members
-- ----------------------------
DROP TABLE IF EXISTS "r_members";
CREATE TABLE "r_members" (
  "rid" integer NOT NULL,
  "uid_code" text NOT NULL,
  "role" text NOT NULL DEFAULT '',
  "permission" integer NOT NULL DEFAULT 7,
  "join_time" integer NOT NULL DEFAULT 0,
  PRIMARY KEY ("rid", "uid_code")
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
  PRIMARY KEY ("tid", "uid")
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
  "creation_time" integer NOT NULL DEFAULT 0
);

-- ----------------------------
-- Table structure for t_messages
-- ----------------------------
DROP TABLE IF EXISTS "t_messages";
CREATE TABLE "t_messages" (
  "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  "room_id" text NOT NULL,
  "sender" text NOT NULL,
  "type" integer NOT NULL,
  "content" text NOT NULL,
  "signature" text NOT NULL DEFAULT '',
  "is_delete" integer NOT NULL DEFAULT 0,
  "time_stamp" integer NOT NULL DEFAULT 0
);

-- ----------------------------
-- Table structure for t_room_attributes
-- ----------------------------
DROP TABLE IF EXISTS "t_room_attributes";
CREATE TABLE "t_room_attributes" (
  "rid" integer NOT NULL,
  "key" text NOT NULL,
  "value" text,
  PRIMARY KEY ("rid", "key")
);

-- ----------------------------
-- Table structure for t_rooms
-- ----------------------------
DROP TABLE IF EXISTS "t_rooms";
CREATE TABLE "t_rooms" (
  "rid" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  "status" integer NOT NULL DEFAULT 0,
  "creation_time" integer NOT NULL DEFAULT 0
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
  "uid" text NOT NULL,
  "key" text NOT NULL,
  "value" text NOT NULL,
  PRIMARY KEY ("uid", "key")
);

-- ----------------------------
-- Table structure for t_users
-- ----------------------------
DROP TABLE IF EXISTS "t_users";
CREATE TABLE "t_users" (
  "uid" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  "uid_code" text NOT NULL,
  "status" integer NOT NULL DEFAULT 0,
  "public_key" text NOT NULL DEFAULT '',
  "signature" text NOT NULL DEFAULT '',
  "time_stamp" integer NOT NULL DEFAULT 0
);

-- ----------------------------
-- Auto increment value for t_mails
-- ----------------------------

-- ----------------------------
-- Auto increment value for t_messages
-- ----------------------------
UPDATE "main"."sqlite_sequence" SET seq = 24 WHERE name = 't_messages';

-- ----------------------------
-- Auto increment value for t_rooms
-- ----------------------------
UPDATE "main"."sqlite_sequence" SET seq = 2 WHERE name = 't_rooms';

-- ----------------------------
-- Auto increment value for t_users
-- ----------------------------
UPDATE "main"."sqlite_sequence" SET seq = 3 WHERE name = 't_users';

PRAGMA foreign_keys = true;
