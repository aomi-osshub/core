package controller

import "net/http"

type Route struct {
	pattern string
	handler func(http.ResponseWriter, *http.Request)
}

func (routers []Route) ServeHTTP(w http.ResponseWriter, r *http.Request) {

}
