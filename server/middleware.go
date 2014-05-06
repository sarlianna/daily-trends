package main

/*
func midparseJSONBody(request *http.Request) (map[string]interface{}, error) {
	//get data out of request body
	var body []byte
	body = make([]byte, 1000, 1000)
	n, err := request.Body.Read(body)
	if err != nil {
		return nil, err
	}
	if n == 0 {
		return nil, nil
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
*/
