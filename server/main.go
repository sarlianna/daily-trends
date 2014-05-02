package main

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"time"

	"github.com/zenazn/goji"
	"github.com/zenazn/goji/web"
)

//Should I really have a global like this?
var dbmap = initDb()

func main() {
	defer dbmap.Db.Close()

	//static page routes
	//goji.Get("/", index)

	// api routes
	goji.Get("/api/tags", getTags)
	goji.Get("/api/tags/:id", getTag)
	goji.Post("/api/tags", createTag)
	goji.Put("/api/tags/:id", updateTag)
	goji.Delete("/api/tags/:id", deleteTag)
	goji.Get("/api/days", getDays)
	goji.Get("/api/days/:id", getDay)
	goji.Post("/api/days", createDay)
	goji.Put("/api/days/:id", updateDay)
	goji.Delete("/api/days/:id", deleteDay)

	goji.Serve()

}

/*
func index(c web.C, writer http.ResponseWriter, request *http.Request) {
	return
}
*/

func getTags(c web.C, writer http.ResponseWriter, request *http.Request) {
	var tags []Tag
	_, err := dbmap.Select(&tags, "select * from tags")
	checkErr(err, "Get all tags failed:")
	jsonTags, err := json.Marshal(tags)
	fmt.Fprintf(writer, string(jsonTags))
}

func getTag(c web.C, writer http.ResponseWriter, request *http.Request) {
	//get tag by id
	tag, err := dbmap.Get(Tag{}, c.URLParams["id"])
	checkErr(err, "Get tag by id failed:")

	//return json tag
	jsonTag, err := json.Marshal(tag)
	checkErr(err, "json conversion failed:")
	fmt.Fprintf(writer, string(jsonTag))
}

func createTag(c web.C, writer http.ResponseWriter, request *http.Request) {
	tagJson, err := parseJSONBody(request)
	checkErr(err, "Parsing request body failed:")

	newTag := &Tag{0, tagJson["title"].(string)}
	err = dbmap.Insert(newTag)
	checkErr(err, "Inserting new tag into db failed:")

	//return new tag data
	jsonNewTag, jsonerr := json.Marshal(newTag)
	checkErr(jsonerr, "json conversion failed:")
	fmt.Fprintf(writer, string(jsonNewTag))
}

func updateTag(c web.C, writer http.ResponseWriter, request *http.Request) {
	tagJson, err := parseJSONBody(request)
	checkErr(err, "Parsing request body failed:")

	//get existing tag
	obj, geterr := dbmap.Get(Tag{}, c.URLParams["id"])
	checkErr(geterr, "Get tag by id failed")

	//update and store tag
	tag := obj.(*Tag)
	tag.Title = tagJson["title"].(string)

	_, err = dbmap.Update(tag)

	//return new tag data
	jsonTag, jsonerr := json.Marshal(tag)
	checkErr(jsonerr, "json conversion failed:")
	fmt.Fprintf(writer, string(jsonTag))
}

func deleteTag(c web.C, writer http.ResponseWriter, request *http.Request) {
	//get tag by id
	tag, err := dbmap.Get(Tag{}, c.URLParams["id"])
	checkErr(err, "Get tag by id failed:")

	_, err = dbmap.Delete(tag)
	checkErr(err, "Delete tag failed:")
	fmt.Fprintf(writer, "{\"status\":\"success\"}")
}

func getDays(c web.C, writer http.ResponseWriter, request *http.Request) {
	var occurances []Occurance
	_, err := dbmap.Select(&occurances, "select * from occurances")
	checkErr(err, "Get all days failed:")
	jsonOccurances, err := json.Marshal(occurances)
	fmt.Fprintf(writer, string(jsonOccurances))
}

func getDay(c web.C, writer http.ResponseWriter, request *http.Request) {
	//get occurance by id
	occurance, err := dbmap.Get(Occurance{}, c.URLParams["id"])
	checkErr(err, "Get occurance by id failed:")

	//return json occurance
	jsonOccurance, err := json.Marshal(occurance)
	checkErr(err, "json conversion failed:")
	fmt.Fprintf(writer, string(jsonOccurance))
}

func createDay(c web.C, writer http.ResponseWriter, request *http.Request) {
	occuranceJson, err := parseJSONBody(request)
	checkErr(err, "Parsing request body failed:")

	newOccurance := &Occurance{
		0,
		occuranceJson["tag"].(int64),
		occuranceJson["day"].(time.Time),
		occuranceJson["degree"].(uint8),
		occuranceJson["positivity"].(uint8),
	}

	err = dbmap.Insert(newOccurance)
	checkErr(err, "Inserting new occurance into db failed:")

	//return new tag data
	jsonNewOccurance, jsonerr := json.Marshal(newOccurance)
	checkErr(jsonerr, "json conversion failed:")
	fmt.Fprintf(writer, string(jsonNewOccurance))
}

func updateDay(c web.C, writer http.ResponseWriter, request *http.Request) {
	occuranceJson, err := parseJSONBody(request)
	checkErr(err, "Parsing request body failed:")

	//get existing occurance
	obj, geterr := dbmap.Get(Occurance{}, c.URLParams["id"])
	checkErr(geterr, "Get occurance by id failed")

	//update and store occurance
	occurance := obj.(*Occurance)
	if occuranceJson["tag"] != nil {
		occurance.Tag = occuranceJson["tag"].(int64)
	}
	if occuranceJson["day"] != nil {
		occurance.Day = occuranceJson["day"].(time.Time)
	}
	if occuranceJson["degree"] != nil {
		occurance.Degree = occuranceJson["degree"].(uint8)
	}
	if occuranceJson["positivity"] != nil {
		occurance.Positivity = occuranceJson["positivity"].(uint8)
	}

	_, err = dbmap.Update(occurance)

	//return new occurance data
	jsonOccurance, jsonerr := json.Marshal(occurance)
	checkErr(jsonerr, "json conversion failed:")
	fmt.Fprintf(writer, string(jsonOccurance))
}

func deleteDay(c web.C, writer http.ResponseWriter, request *http.Request) {
	//get occurance by id
	occurance, err := dbmap.Get(Occurance{}, c.URLParams["id"])
	checkErr(err, "Get occurance by id failed:")

	_, err = dbmap.Delete(occurance)
	checkErr(err, "Delete occurance failed:")
	fmt.Fprintf(writer, "{\"status\":\"success\"}")
}

func parseJSONBody(request *http.Request) (map[string]interface{}, error) {
	//get data out of request body
	var body []byte
	body = make([]byte, 1000, 1000)
	n, err := request.Body.Read(body)
	if err != nil {
		return nil, err
	}
	body = body[0:n]

	//convert raw data to json
	var bodyJson map[string]interface{}
	err = json.Unmarshal(body, &bodyJson)
	if err != nil {
		return nil, err
	}

	return bodyJson, nil
}

func checkErr(err error, msg string) {
	if err != nil {
		log.Fatalln(msg, err)
	}
}
