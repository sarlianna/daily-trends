package main

import (
	"fmt"

	"github.com/zenazn/goji"
)

func main() {
	goji.Get("/", fmt.Fprintf(w, "Hello world!"))
	goji.Serve()

}
