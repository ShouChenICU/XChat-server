/*
 Navicat Premium Data Transfer

 Source Server         : xchat-server
 Source Server Type    : SQLite
 Source Server Version : 3030001
 Source Schema         : main

 Target Server Type    : SQLite
 Target Server Version : 3030001
 File Encoding         : 65001

 Date: 03/01/2022 23:00:15
*/

PRAGMA foreign_keys = false;

-- ----------------------------
-- Table structure for r_members
-- ----------------------------
DROP TABLE IF EXISTS "r_members";
CREATE TABLE "r_members" (
  "rid" integer NOT NULL,
  "uid_code" text NOT NULL,
  "role" integer NOT NULL DEFAULT 0,
  "join_time" integer NOT NULL DEFAULT 0,
  PRIMARY KEY ("rid", "uid_code"),
  CONSTRAINT "rid" FOREIGN KEY ("rid") REFERENCES "t_rooms" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT "uid_code" FOREIGN KEY ("uid_code") REFERENCES "t_users" ("id_code") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for r_title_own
-- ----------------------------
DROP TABLE IF EXISTS "r_title_own";
CREATE TABLE "r_title_own" (
  "tid" integer NOT NULL,
  "uid_code" text NOT NULL,
  "creation_time" integer NOT NULL DEFAULT 0,
  PRIMARY KEY ("tid", "uid_code"),
  CONSTRAINT "tid" FOREIGN KEY ("tid") REFERENCES "t_titles" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT "uid_code" FOREIGN KEY ("uid_code") REFERENCES "t_users" ("uid_code") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for sqlite_sequence
-- ----------------------------
DROP TABLE IF EXISTS "sqlite_sequence";
CREATE TABLE sqlite_sequence(name,seq);

-- ----------------------------
-- Table structure for t_mails
-- ----------------------------
DROP TABLE IF EXISTS "t_mails";
CREATE TABLE "t_mails" (
  "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  "sender" text NOT NULL,
  "receiver" text NOT NULL,
  "theme" text NOT NULL DEFAULT '',
  "content" text NOT NULL DEFAULT '',
  "creation_time" integer NOT NULL DEFAULT 0,
  CONSTRAINT "sender" FOREIGN KEY ("sender") REFERENCES "t_users" ("uid_code") ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT "receiver" FOREIGN KEY ("receiver") REFERENCES "t_users" ("uid_code") ON DELETE NO ACTION ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for t_messages
-- ----------------------------
DROP TABLE IF EXISTS "t_messages";
CREATE TABLE "t_messages" (
  "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  "sender" text NOT NULL,
  "rid" integer NOT NULL,
  "type" text NOT NULL,
  "content" text NOT NULL,
  "time_stamp" integer NOT NULL DEFAULT 0,
  CONSTRAINT "uid_code" FOREIGN KEY ("sender") REFERENCES "t_users" ("uid_code") ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT "rid" FOREIGN KEY ("rid") REFERENCES "t_rooms" ("id") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for t_rooms
-- ----------------------------
DROP TABLE IF EXISTS "t_rooms";
CREATE TABLE "t_rooms" (
  "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  "uid_code" text NOT NULL,
  "topic" text NOT NULL,
  "level" integer NOT NULL,
  "creation_time" integer NOT NULL DEFAULT 0,
  CONSTRAINT "uid_code" FOREIGN KEY ("uid_code") REFERENCES "t_users" ("uid_code") ON DELETE CASCADE ON UPDATE CASCADE
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
  "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  "uid_code" text NOT NULL,
  "key" text NOT NULL,
  "value" text NOT NULL,
  CONSTRAINT "uid_code" FOREIGN KEY ("uid_code") REFERENCES "t_users" ("uid_code") ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for t_users
-- ----------------------------
DROP TABLE IF EXISTS "t_users";
CREATE TABLE "t_users" (
  "uid_code" text NOT NULL,
  "nick" text NOT NULL,
  "tid" integer NOT NULL DEFAULT 0,
  "level" integer NOT NULL DEFAULT 0,
  "exp" integer NOT NULL DEFAULT 0,
  "status" integer NOT NULL DEFAULT 0,
  "time_stamp" integer NOT NULL DEFAULT 0,
  PRIMARY KEY ("uid_code"),
  CONSTRAINT "tid" FOREIGN KEY ("tid") REFERENCES "t_titles" ("id") ON DELETE SET DEFAULT ON UPDATE CASCADE
);

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

-- ----------------------------
-- Auto increment value for t_user_attributes
-- ----------------------------

PRAGMA foreign_keys = true;
