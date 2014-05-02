package main

import (
	"database/sql"
	"time"

	"github.com/coopernurse/gorp"
	_ "github.com/mattn/go-sqlite3"
)

type Tag struct {
	Id    int64 `db:"tag_id"`
	Title string
}

const (
	ABSENT  = iota
	LITTLE  = iota
	PRESENT = iota
	MUCH    = iota
)

const (
	NEGATIVE = iota
	NEUTRAL  = iota
	POSITIVE = iota
)

type Occurance struct {
	Id         int64
	Tag        int64
	Day        time.Time
	Degree     uint8 //should be an enum with possible values (absent, little, present, much)
	Positivity uint8 //should be an enum with possible values (negative, neutral, positive)
}

func initDb() *gorp.DbMap {
	db, err := sql.Open("sqlite3", "db.sqlite3")
	checkErr(err, "sql.Open failed")

	dbmap := &gorp.DbMap{Db: db, Dialect: gorp.SqliteDialect{}}

	//Add tables.  This should really be done elsewhere, but for now we're just throwing it here.
	dbmap.AddTableWithName(Tag{}, "tags").SetKeys(true, "Id")
	dbmap.AddTableWithName(Occurance{}, "occurances").SetKeys(true, "Id")

	err = dbmap.CreateTablesIfNotExists()
	checkErr(err, "Create tables failed")

	return dbmap

}
