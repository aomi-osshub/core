package controller

type Payload map[string]interface{}

type Result struct {
	Status string

	Describe string

	Payload Payload
}
